package biblioteca.gui;

import biblioteca.modelo.Buscable;
import biblioteca.modelo.Formato;
import biblioteca.modelo.Multa;
import biblioteca.servicio.Biblioteca;

import javax.swing.JButton;
import javax.swing.JComboBox;
import java.time.LocalDate;
import java.util.List;

/** Multas por atraso y registro de pagos. */
public class PanelMultas extends PanelListado<Multa> {

    private static final long serialVersionUID = 1L;

    private final JComboBox<String> filtroEstado = new JComboBox<>(new String[]{"Pendientes", "Pagadas", "Todas"});

    public PanelMultas(Biblioteca biblioteca) {
        super(biblioteca, "Multas", "Se generan automáticamente al devolver con atraso, según la política vigente.",
                "Buscar por socio, material o N.º de multa…");
        filtroEstado.addActionListener(e -> aplicarFiltro());
        agregarFiltro(filtroEstado);

        JButton pagar = Estilo.boton("Registrar pago", Estilo.EXITO);
        pagar.addActionListener(e -> requerirSeleccion("una multa").ifPresent(this::pagar));
        agregarAccion(pagar);
    }

    @Override
    protected void definirColumnas(ModeloTabla<Multa> m) {
        m.columna("N.º", String.class, 70, Multa::getId)
                .columna("Socio", String.class, 180, x -> x.getPrestamo().getSocio().getNombreCompleto())
                .columna("Categoría", String.class, 90, x -> x.getPrestamo().getSocio().getTipo())
                .columna("Material", String.class, 210, x -> x.getPrestamo().getEjemplar().getMaterial().getTitulo())
                .columna("Préstamo", String.class, 75, x -> x.getPrestamo().getId())
                .columna("Días", Integer.class, 50, Multa::getDiasAtraso)
                .columna("Monto", Moneda.class, 100, x -> new Moneda(x.getMonto()))
                .columna("Generada", LocalDate.class, 90, Multa::getFechaGeneracion)
                .columna("Pago", LocalDate.class, 90, Multa::getFechaPago)
                .columna("Estado", String.class, 105, x -> x.estaPagada() ? "Pagada" : "Pendiente");
    }

    @Override
    protected List<Multa> obtenerDatos() {
        return biblioteca.listarMultas();
    }

    @Override
    protected boolean coincide(Multa m, String texto) {
        return Buscable.contieneAlguno(texto, m.getId(), m.getPrestamo().getId())
                || m.getPrestamo().getSocio().coincideCon(texto)
                || m.getPrestamo().getEjemplar().getMaterial().coincideCon(texto);
    }

    @Override
    protected boolean pasaFiltroExtra(Multa m) {
        return switch (filtroEstado.getSelectedIndex()) {
            case 0 -> !m.estaPagada();
            case 1 -> m.estaPagada();
            default -> true;
        };
    }

    @Override
    protected String textoResumen() {
        return "Pendiente de cobro: " + Formato.moneda(biblioteca.getDeudaTotalPendiente());
    }

    private void pagar(Multa m) {
        if (m.estaPagada()) {
            Mensajes.error(this, "La multa " + m.getId() + " ya está paga.");
            return;
        }
        if (Mensajes.confirmar(this, "Registrar pago", "¿Registrar el pago de " + Formato.moneda(m.getMonto())
                + " de " + m.getPrestamo().getSocio().getNombreCompleto() + "?")) {
            Mensajes.intentar(this, () -> biblioteca.pagarMulta(m));
        }
    }
}
