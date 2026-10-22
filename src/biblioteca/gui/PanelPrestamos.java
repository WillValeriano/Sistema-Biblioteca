package biblioteca.gui;

import biblioteca.modelo.Buscable;
import biblioteca.modelo.EstadoPrestamo;
import biblioteca.modelo.Formato;
import biblioteca.modelo.Multa;
import biblioteca.modelo.Prestamo;
import biblioteca.servicio.Biblioteca;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/** Préstamos: registrar, devolver y renovar. */
public class PanelPrestamos extends PanelListado<Prestamo> {

    private static final long serialVersionUID = 1L;

    private final JComboBox<String> filtroEstado =
            new JComboBox<>(new String[]{"En curso (activos y vencidos)", "Solo vencidos", "Devueltos", "Todos"});

    public PanelPrestamos(Biblioteca biblioteca) {
        super(biblioteca, "Préstamos", "Retiros, devoluciones y renovaciones. Los vencidos se marcan en rojo.",
                "Buscar por socio, título o N.º…");
        filtroEstado.addActionListener(e -> aplicarFiltro());
        agregarFiltro(filtroEstado);

        JButton nuevo = Estilo.botonPrimario("+ Nuevo préstamo");
        nuevo.addActionListener(e -> DialogoPrestamo.abrir(SwingUtilities.getWindowAncestor(this), biblioteca, null));
        JButton devolver = Estilo.boton("Registrar devolución", Estilo.EXITO);
        devolver.addActionListener(e -> requerirSeleccion("un préstamo").ifPresent(this::devolver));
        JButton renovar = Estilo.botonSecundario("Renovar");
        renovar.addActionListener(e -> requerirSeleccion("un préstamo")
                .ifPresent(p -> Mensajes.intentar(this, () -> biblioteca.renovar(p))));

        agregarAccion(renovar);
        agregarAccion(devolver);
        agregarAccion(nuevo);
    }

    @Override
    protected void definirColumnas(ModeloTabla<Prestamo> m) {
        m.columna("N.º", String.class, 70, Prestamo::getId)
                .columna("Socio", String.class, 170, p -> p.getSocio().getNombreCompleto())
                .columna("Material", String.class, 210, p -> p.getEjemplar().getMaterial().getTitulo())
                .columna("Ejemplar", String.class, 75, p -> p.getEjemplar().getCodigo())
                .columna("Retiro", LocalDate.class, 90, Prestamo::getFechaPrestamo)
                .columna("Vence", LocalDate.class, 90, Prestamo::getFechaVencimiento)
                .columna("Devuelto", LocalDate.class, 90, Prestamo::getFechaDevolucion)
                .columna("Atraso", Integer.class, 60, p -> p.getDiasAtraso(biblioteca.hoy()))
                .columna("Estado", String.class, 105, p -> p.getEstado(biblioteca.hoy()).toString())
                .columna("Atendió", String.class, 80, p -> p.getBibliotecario().getNombre());
    }

    @Override
    protected List<Prestamo> obtenerDatos() {
        List<Prestamo> lista = biblioteca.listarPrestamos();
        Collections.reverse(lista); // los más recientes primero
        return lista;
    }

    @Override
    protected boolean coincide(Prestamo p, String texto) {
        return Buscable.contieneAlguno(texto, p.getId(), p.getEjemplar().getCodigo())
                || p.getSocio().coincideCon(texto)
                || p.getEjemplar().getMaterial().coincideCon(texto);
    }

    @Override
    protected boolean pasaFiltroExtra(Prestamo p) {
        EstadoPrestamo estado = p.getEstado(biblioteca.hoy());
        return switch (filtroEstado.getSelectedIndex()) {
            case 0 -> estado != EstadoPrestamo.DEVUELTO;
            case 1 -> estado == EstadoPrestamo.VENCIDO;
            case 2 -> estado == EstadoPrestamo.DEVUELTO;
            default -> true;
        };
    }

    @Override
    protected String textoResumen() {
        int vencidos = biblioteca.prestamosPorEstado(EstadoPrestamo.VENCIDO).size();
        int activos = biblioteca.prestamosPorEstado(EstadoPrestamo.ACTIVO).size();
        return activos + (activos == 1 ? " activo · " : " activos · ") + vencidos + (vencidos == 1 ? " vencido" : " vencidos");
    }

    @Override
    protected void alDobleClic(Prestamo p) {
        String texto = "<html><b>" + p.getEjemplar().getMaterial().getTitulo() + "</b> (" + p.getEjemplar().getCodigo() + ")<br>"
                + "Socio: " + p.getSocio() + "<br>"
                + "Retiro: " + Formato.fecha(p.getFechaPrestamo()) + " · Vence: " + Formato.fecha(p.getFechaVencimiento())
                + " · Renovaciones: " + p.getRenovaciones() + "/" + Prestamo.MAX_RENOVACIONES + "<br>"
                + "Devuelto: " + Formato.fecha(p.getFechaDevolucion()) + "<br>"
                + "Atendió: " + p.getBibliotecario()
                + (p.getMulta() != null ? "<br>Multa: " + Formato.moneda(p.getMulta().getMonto())
                + (p.getMulta().estaPagada() ? " (pagada)" : " (pendiente)") : "")
                + "</html>";
        Mensajes.info(this, "Préstamo " + p.getId(), texto);
    }

    /** Antes de devolver, anticipa al bibliotecario si se generará multa y de cuánto. */
    private void devolver(Prestamo p) {
        if (p.estaDevuelto()) {
            Mensajes.error(this, "El préstamo " + p.getId() + " ya fue devuelto.");
            return;
        }
        int atraso = p.getDiasAtraso(biblioteca.hoy());
        String detalle = "¿Registrar la devolución de \"" + p.getEjemplar().getMaterial().getTitulo() + "\"?";
        if (atraso > 0) {
            double bruto = biblioteca.getPoliticaMulta().calcular(atraso);
            double neto = bruto * (1 - p.getSocio().getDescuentoMulta());
            detalle += "\n\nSe devuelve con " + atraso + " día(s) de atraso."
                    + "\nMulta: " + Formato.moneda(neto)
                    + (p.getSocio().getDescuentoMulta() > 0
                    ? " (incluye " + Math.round(p.getSocio().getDescuentoMulta() * 100) + "% de descuento "
                    + p.getSocio().getTipo().toLowerCase() + ")" : "")
                    + "\nPolítica vigente: " + biblioteca.getPoliticaMulta().getDescripcion();
        } else {
            detalle += "\n\nSe devuelve en término, sin multa.";
        }
        if (!Mensajes.confirmar(this, "Registrar devolución", detalle)) {
            return;
        }
        Mensajes.intentar(this, () -> {
            Multa multa = biblioteca.devolver(p);
            if (multa != null) {
                Mensajes.info(this, "Multa generada", "Se generó la multa " + multa.getId() + " por "
                        + Formato.moneda(multa.getMonto()) + ".\nEl socio queda suspendido hasta abonarla.");
            }
        });
    }
}
