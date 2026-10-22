package biblioteca.gui;

import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.modelo.SocioDocente;
import biblioteca.modelo.SocioEstudiante;
import biblioteca.modelo.SocioGeneral;
import biblioteca.servicio.Biblioteca;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.util.List;

/** Socios: alta, edición, baja y consulta de historial. */
public class PanelSocios extends PanelListado<Socio> {

    private static final long serialVersionUID = 1L;

    private final JComboBox<String> filtroTipo =
            new JComboBox<>(new String[]{"Todas las categorías", "Estudiantes", "Docentes", "Público general"});
    private final JComboBox<String> filtroEstado =
            new JComboBox<>(new String[]{"Cualquier estado", "Habilitado", "Suspendido", "Baja"});

    public PanelSocios(Biblioteca biblioteca) {
        super(biblioteca, "Socios", "Estudiantes, docentes y público general con sus condiciones de préstamo.",
                "Buscar por nombre, DNI o N.º…");
        filtroTipo.addActionListener(e -> aplicarFiltro());
        filtroEstado.addActionListener(e -> aplicarFiltro());
        agregarFiltro(filtroTipo);
        agregarFiltro(filtroEstado);

        JButton nuevo = Estilo.botonPrimario("+ Nuevo socio");
        nuevo.addActionListener(e -> DialogoSocio.abrirAlta(ventana(), biblioteca));
        JButton editar = Estilo.botonSecundario("Editar");
        editar.addActionListener(e -> requerirSeleccion("un socio")
                .ifPresent(s -> DialogoSocio.abrirEdicion(ventana(), biblioteca, s)));
        JButton historial = Estilo.botonSecundario("Historial");
        historial.addActionListener(e -> requerirSeleccion("un socio").ifPresent(this::alDobleClic));
        JButton baja = Estilo.botonSecundario("Baja / Reactivar");
        baja.addActionListener(e -> requerirSeleccion("un socio").ifPresent(this::cambiarEstado));

        agregarAccion(baja);
        agregarAccion(historial);
        agregarAccion(editar);
        agregarAccion(nuevo);
    }

    @Override
    protected void definirColumnas(ModeloTabla<Socio> m) {
        m.columna("N.º", String.class, 70, Socio::getNumeroSocio)
                .columna("Apellido y nombre", String.class, 200, Socio::getNombreCompleto)
                .columna("DNI", String.class, 90, Socio::getDni)
                .columna("Categoría", String.class, 95, Socio::getTipo)
                .columna("Email", String.class, 200, Socio::getEmail)
                .columna("Préstamos", String.class, 85, s -> s.getPrestamosActivos().size() + " / " + s.getMaxPrestamos())
                .columna("Deuda", Moneda.class, 95, s -> new Moneda(s.getDeudaPendiente()))
                .columna("Estado", String.class, 120, s -> s.getEstado(biblioteca.hoy()))
                .columna("Alta", LocalDate.class, 90, Socio::getFechaAlta);
    }

    @Override
    protected List<Socio> obtenerDatos() {
        return biblioteca.listarSocios();
    }

    @Override
    protected boolean coincide(Socio fila, String texto) {
        return fila.coincideCon(texto);
    }

    @Override
    protected boolean pasaFiltroExtra(Socio s) {
        boolean tipoOk = switch (filtroTipo.getSelectedIndex()) {
            case 1 -> s instanceof SocioEstudiante;
            case 2 -> s instanceof SocioDocente;
            case 3 -> s instanceof SocioGeneral;
            default -> true;
        };
        boolean estadoOk = filtroEstado.getSelectedIndex() == 0
                || s.getEstado(biblioteca.hoy()).equals(filtroEstado.getSelectedItem());
        return tipoOk && estadoOk;
    }

    @Override
    protected String textoResumen() {
        long activos = biblioteca.listarSocios().stream().filter(Socio::isActivo).count();
        return activos + " socios activos";
    }

    /** Historial de préstamos del socio (asociación Socio 1 — * Prestamo). */
    @Override
    protected void alDobleClic(Socio socio) {
        ModeloTabla<Prestamo> m = new ModeloTabla<Prestamo>()
                .columna("Préstamo", String.class, 75, Prestamo::getId)
                .columna("Material", String.class, 210, p -> p.getEjemplar().getMaterial().getTitulo())
                .columna("Retiro", LocalDate.class, 90, Prestamo::getFechaPrestamo)
                .columna("Vence", LocalDate.class, 90, Prestamo::getFechaVencimiento)
                .columna("Devuelto", LocalDate.class, 90, Prestamo::getFechaDevolucion)
                .columna("Estado", String.class, 100, p -> p.getEstado(biblioteca.hoy()).toString())
                .columna("Multa", Moneda.class, 90, p -> p.getMulta() == null ? null : new Moneda(p.getMulta().getMonto()));
        String sub = socio.getNumeroSocio() + " · " + socio.getRol() + " · DNI " + socio.getDni()
                + " · hasta " + socio.getMaxPrestamos() + " préstamos de " + socio.getDiasPrestamo() + " días";
        new DialogoDetalle<>(ventana(), socio.getNombreCompleto(), sub, m, socio::getHistorial)
                .accion("+ Nuevo préstamo", () -> DialogoPrestamo.abrir(ventana(), biblioteca, socio))
                .mostrar();
    }

    private void cambiarEstado(Socio socio) {
        String accion = socio.isActivo() ? "dar de baja" : "reactivar";
        if (Mensajes.confirmar(this, "Cambiar estado", "¿Querés " + accion + " a " + socio.getNombreCompleto() + "?")) {
            Mensajes.intentar(this, () -> biblioteca.cambiarEstadoSocio(socio));
        }
    }

    private java.awt.Window ventana() {
        return SwingUtilities.getWindowAncestor(this);
    }
}
