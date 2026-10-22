package biblioteca.gui;

import biblioteca.modelo.Ejemplar;
import biblioteca.modelo.Libro;
import biblioteca.modelo.MaterialBibliografico;
import biblioteca.modelo.Revista;
import biblioteca.servicio.Biblioteca;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import java.util.List;

/** Catálogo: libros y revistas con su disponibilidad. */
public class PanelCatalogo extends PanelListado<MaterialBibliografico> {

    private static final long serialVersionUID = 1L;

    private final JComboBox<String> filtroTipo =
            new JComboBox<>(new String[]{"Todo el catálogo", "Solo libros", "Solo revistas", "Con ejemplares disponibles"});

    public PanelCatalogo(Biblioteca biblioteca) {
        super(biblioteca, "Catálogo", "Libros y revistas de la biblioteca y sus ejemplares físicos.",
                "Buscar por título, autor, ISBN, categoría…");
        filtroTipo.addActionListener(e -> aplicarFiltro());
        agregarFiltro(filtroTipo);

        JButton nuevoLibro = Estilo.botonPrimario("+ Nuevo libro");
        nuevoLibro.addActionListener(e -> DialogoMaterial.abrir(ventana(), biblioteca, true));
        JButton nuevaRevista = Estilo.botonSecundario("+ Nueva revista");
        nuevaRevista.addActionListener(e -> DialogoMaterial.abrir(ventana(), biblioteca, false));
        JButton ejemplares = Estilo.botonSecundario("Ejemplares");
        ejemplares.addActionListener(e -> requerirSeleccion("un material").ifPresent(this::alDobleClic));
        JButton eliminar = Estilo.botonSecundario("Eliminar");
        eliminar.addActionListener(e -> requerirSeleccion("un material").ifPresent(this::eliminar));

        agregarAccion(eliminar);
        agregarAccion(ejemplares);
        agregarAccion(nuevaRevista);
        agregarAccion(nuevoLibro);
    }

    @Override
    protected void definirColumnas(ModeloTabla<MaterialBibliografico> m) {
        m.columna("Código", String.class, 70, MaterialBibliografico::getCodigo)
                .columna("Tipo", String.class, 70, MaterialBibliografico::getTipo)
                .columna("Título", String.class, 230, MaterialBibliografico::getTitulo)
                .columna("Autor / Edición", String.class, 230, MaterialBibliografico::getDetalle)
                .columna("Año", Integer.class, 55, MaterialBibliografico::getAnioPublicacion)
                .columna("Categoría", String.class, 170, MaterialBibliografico::getCategoria)
                .columna("Disponibles", String.class, 90,
                        x -> x.getCantidadDisponibles() + " de " + x.getEjemplares().size())
                .columna("Estado", String.class, 110,
                        x -> x.getCantidadDisponibles() > 0 ? "Disponible" : "Sin stock")
                .columna("Préstamos", Integer.class, 80, MaterialBibliografico::getVecesPrestado);
    }

    @Override
    protected List<MaterialBibliografico> obtenerDatos() {
        return biblioteca.listarMateriales();
    }

    @Override
    protected boolean coincide(MaterialBibliografico fila, String texto) {
        return fila.coincideCon(texto);
    }

    @Override
    protected boolean pasaFiltroExtra(MaterialBibliografico fila) {
        return switch (filtroTipo.getSelectedIndex()) {
            case 1 -> fila instanceof Libro;
            case 2 -> fila instanceof Revista;
            case 3 -> fila.getCantidadDisponibles() > 0;
            default -> true;
        };
    }

    @Override
    protected String textoResumen() {
        return biblioteca.listarMateriales().size() + " títulos · " + biblioteca.getTotalEjemplares() + " ejemplares";
    }

    /** Muestra los ejemplares del material (composición) y permite agregar o enviar a reparación. */
    @Override
    protected void alDobleClic(MaterialBibliografico material) {
        ModeloTabla<Ejemplar> m = new ModeloTabla<Ejemplar>()
                .columna("Código", String.class, 110, Ejemplar::getCodigo)
                .columna("Estado", String.class, 140, e -> e.getEstado().toString())
                .columna("Ubicación", String.class, 160, Ejemplar::getUbicacion);
        String sub = material.getTipo() + " · " + material.getDetalle() + " · " + material.getAnioPublicacion()
                + (material instanceof Libro l && !l.getIsbn().isBlank() ? " · ISBN " + l.getIsbn() : "")
                + (material instanceof Revista r && !r.getIssn().isBlank() ? " · ISSN " + r.getIssn() : "");
        new DialogoDetalle<>(ventana(), material.getTitulo(), sub, m, material::getEjemplares)
                .accionSobreSeleccion("Reparación sí / no",
                        e -> Mensajes.intentar(this, () -> biblioteca.cambiarReparacion(e)))
                .accion("+ Agregar ejemplar", () -> biblioteca.agregarEjemplar(material,
                        material.getEjemplares().isEmpty() ? "" : material.getEjemplares().get(0).getUbicacion()))
                .mostrar();
    }

    private void eliminar(MaterialBibliografico material) {
        if (Mensajes.confirmar(this, "Eliminar material",
                "¿Eliminar \"" + material.getTitulo() + "\" y sus " + material.getEjemplares().size() + " ejemplares?")) {
            Mensajes.intentar(this, () -> biblioteca.eliminarMaterial(material));
        }
    }

    private java.awt.Window ventana() {
        return SwingUtilities.getWindowAncestor(this);
    }
}
