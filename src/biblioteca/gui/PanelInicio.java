package biblioteca.gui;

import biblioteca.modelo.EstadoPrestamo;
import biblioteca.modelo.Formato;
import biblioteca.modelo.MaterialBibliografico;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.servicio.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Tablero de inicio: indicadores, ranking de préstamos y vencimientos. */
public class PanelInicio extends JPanel implements Actualizable {

    private static final long serialVersionUID = 1L;

    private final transient Biblioteca biblioteca;
    private final javax.swing.JLabel saludo = Estilo.titulo("");
    private final javax.swing.JLabel fecha = Estilo.subtitulo("");
    private final TarjetaEstadistica tCatalogo = new TarjetaEstadistica("Catálogo", Estilo.PRIMARIO);
    private final TarjetaEstadistica tSocios = new TarjetaEstadistica("Socios activos", Estilo.ACENTO);
    private final TarjetaEstadistica tPrestamos = new TarjetaEstadistica("Préstamos en curso", Estilo.EXITO);
    private final TarjetaEstadistica tVencidos = new TarjetaEstadistica("Vencidos", Estilo.PELIGRO);
    private final TarjetaEstadistica tMultas = new TarjetaEstadistica("Multas a cobrar", Estilo.ALERTA);
    private final GraficoBarras grafico = new GraficoBarras();
    private final ModeloTabla<Prestamo> modeloVencimientos = new ModeloTabla<>();

    public PanelInicio(Biblioteca biblioteca, Runnable irAPrestamos) {
        super(new BorderLayout(0, 18));
        this.biblioteca = biblioteca;
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        // ---- Encabezado con accesos rápidos ----
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(saludo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(fecha);

        JButton nuevoPrestamo = Estilo.botonPrimario("+ Nuevo préstamo");
        nuevoPrestamo.addActionListener(e -> DialogoPrestamo.abrir(SwingUtilities.getWindowAncestor(this), biblioteca, null));
        JButton verPrestamos = Estilo.botonSecundario("Ir a préstamos");
        verPrestamos.addActionListener(e -> irAPrestamos.run());
        JPanel atajos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        atajos.setOpaque(false);
        atajos.add(verPrestamos);
        atajos.add(nuevoPrestamo);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(atajos, BorderLayout.EAST);

        // ---- Tarjetas ----
        JPanel tarjetas = new JPanel(new GridLayout(1, 5, 14, 0));
        tarjetas.setOpaque(false);
        tarjetas.add(tCatalogo);
        tarjetas.add(tSocios);
        tarjetas.add(tPrestamos);
        tarjetas.add(tVencidos);
        tarjetas.add(tMultas);

        JPanel norte = new JPanel(new BorderLayout(0, 18));
        norte.setOpaque(false);
        norte.add(encabezado, BorderLayout.NORTH);
        norte.add(tarjetas, BorderLayout.SOUTH);

        // ---- Ranking ----
        JPanel cajaGrafico = caja("Más prestados", grafico);

        // ---- Vencimientos ----
        modeloVencimientos
                .columna("Vence", LocalDate.class, 100, Prestamo::getFechaVencimiento)
                .columna("Socio", String.class, 150, p -> p.getSocio().getNombreCompleto())
                .columna("Material", String.class, 170, p -> p.getEjemplar().getMaterial().getTitulo())
                .columna("Estado", String.class, 95, p -> p.getEstado(biblioteca.hoy()).toString());
        JTable tabla = new JTable(modeloVencimientos);
        Estilo.estilizarTabla(tabla);
        modeloVencimientos.aplicarAnchos(tabla);
        JPanel cajaVencimientos = caja("Próximos vencimientos y atrasos", Estilo.scroll(tabla));

        JPanel centro = new JPanel(new GridLayout(1, 2, 14, 0));
        centro.setOpaque(false);
        centro.add(cajaGrafico);
        centro.add(cajaVencimientos);

        add(norte, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
    }

    private static JPanel caja(String titulo, java.awt.Component contenido) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Estilo.SUPERFICIE);
        p.setBorder(Estilo.tarjeta());
        p.add(Estilo.etiquetaSeccion(titulo), BorderLayout.NORTH);
        p.add(contenido, BorderLayout.CENTER);
        return p;
    }

    @Override
    public void actualizar() {
        LocalDate hoy = biblioteca.hoy();
        String nombre = biblioteca.getBibliotecarioActual() == null ? "" : ", " + biblioteca.getBibliotecarioActual().getNombre();
        saludo.setText("Hola" + nombre);
        String dia = hoy.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR")));
        fecha.setText(biblioteca.getNombre() + " · " + Character.toUpperCase(dia.charAt(0)) + dia.substring(1));

        List<MaterialBibliografico> materiales = biblioteca.listarMateriales();
        tCatalogo.setDatos(String.valueOf(materiales.size()), biblioteca.getTotalEjemplares() + " ejemplares físicos");

        List<Socio> socios = biblioteca.listarSocios();
        long activos = socios.stream().filter(Socio::isActivo).count();
        long suspendidos = socios.stream().filter(s -> "Suspendido".equals(s.getEstado(hoy))).count();
        tSocios.setDatos(String.valueOf(activos), suspendidos + " suspendidos");

        int enCurso = biblioteca.prestamosPorEstado(EstadoPrestamo.ACTIVO).size();
        int vencidos = biblioteca.prestamosPorEstado(EstadoPrestamo.VENCIDO).size();
        tPrestamos.setDatos(String.valueOf(enCurso + vencidos), enCurso + " dentro del plazo");
        tVencidos.setDatos(String.valueOf(vencidos), vencidos == 0 ? "Todo al día" : "Requieren seguimiento");

        int pendientes = biblioteca.multasPendientes().size();
        tMultas.setDatos(Formato.moneda(biblioteca.getDeudaTotalPendiente()), pendientes + " multas pendientes");

        grafico.limpiar();
        for (MaterialBibliografico m : biblioteca.masPrestados(6)) {
            grafico.agregar(m.getTitulo(), m.getVecesPrestado());
        }
        grafico.repaint();

        List<Prestamo> enCursoOrdenados = biblioteca.listarPrestamos().stream()
                .filter(p -> !p.estaDevuelto())
                .sorted(Comparator.comparing(Prestamo::getFechaVencimiento))
                .toList();
        modeloVencimientos.setFilas(enCursoOrdenados);
    }
}
