package biblioteca.gui;

import biblioteca.modelo.Bibliotecario;
import biblioteca.modelo.Formato;
import biblioteca.multas.MultaFijaDiaria;
import biblioteca.multas.MultaProgresiva;
import biblioteca.multas.PoliticaMulta;
import biblioteca.persistencia.DatosDeEjemplo;
import biblioteca.persistencia.GestorPersistencia;
import biblioteca.servicio.Biblioteca;
import biblioteca.servicio.BibliotecaListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ventana principal. Observa a la biblioteca: ante cualquier cambio refresca
 * todas las pantallas y guarda automáticamente en disco.
 */
public class VentanaPrincipal extends JFrame implements BibliotecaListener {

    private static final long serialVersionUID = 1L;

    public static final String INICIO = "Inicio";
    public static final String CATALOGO = "Catálogo";
    public static final String SOCIOS = "Socios";
    public static final String PRESTAMOS = "Préstamos";
    public static final String MULTAS = "Multas";

    private static final PoliticaMulta POLITICA_FIJA = new MultaFijaDiaria(500);
    private static final PoliticaMulta POLITICA_PROGRESIVA = new MultaProgresiva(500, 0.5);

    private final transient Biblioteca biblioteca;
    private final transient GestorPersistencia persistencia;
    private final transient List<Actualizable> pantallas = new ArrayList<>();
    private final Map<String, BotonNavegacion> botones = new LinkedHashMap<>();
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);

    private final JLabel fechaSistema = new JLabel();
    private final JLabel chipSimulacion = new JLabel();
    private final JLabel politica = new JLabel();
    private final JLabel estado = new JLabel(" ");
    private final JLabel guardado = Estilo.subtitulo("");
    private final JComboBox<Bibliotecario> atiende = new JComboBox<>();
    private JRadioButtonMenuItem menuFija;
    private JRadioButtonMenuItem menuProgresiva;

    public VentanaPrincipal(Biblioteca biblioteca, GestorPersistencia persistencia) {
        super("Gestión de Biblioteca — " + biblioteca.getNombre());
        this.biblioteca = biblioteca;
        this.persistencia = persistencia;

        PanelInicio inicio = new PanelInicio(biblioteca, () -> mostrarSeccion(PRESTAMOS));
        registrar(INICIO, inicio);
        registrar(CATALOGO, new PanelCatalogo(biblioteca));
        registrar(SOCIOS, new PanelSocios(biblioteca));
        registrar(PRESTAMOS, new PanelPrestamos(biblioteca));
        registrar(MULTAS, new PanelMultas(biblioteca));

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.add(crearBarraSuperior(), BorderLayout.NORTH);
        derecha.add(contenido, BorderLayout.CENTER);
        derecha.add(crearBarraEstado(), BorderLayout.SOUTH);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(crearMenuLateral(), BorderLayout.WEST);
        raiz.add(derecha, BorderLayout.CENTER);
        setContentPane(raiz);
        setJMenuBar(crearMenu());

        biblioteca.agregarListener(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardar();
                dispose();
                System.exit(0);
            }
        });

        setMinimumSize(new Dimension(1180, 720));
        setSize(1360, 820);
        setLocationRelativeTo(null);
        mostrarSeccion(INICIO);
        actualizarTodo();
        estado.setText("Listo. Datos cargados desde " + persistencia.getArchivo().toAbsolutePath());
    }

    /** Crea la ventana y la muestra. */
    public static VentanaPrincipal abrir(Biblioteca biblioteca, GestorPersistencia persistencia) {
        VentanaPrincipal v = new VentanaPrincipal(biblioteca, persistencia);
        v.setVisible(true);
        return v;
    }

    private void registrar(String nombre, JComponent panel) {
        contenido.add(panel, nombre);
        pantallas.add((Actualizable) panel);
    }

    public void mostrarSeccion(String nombre) {
        tarjetas.show(contenido, nombre);
        botones.forEach((n, b) -> b.setSeleccionado(n.equals(nombre)));
    }

    // =====================================================================
    // Observer: reacción a cada cambio de la biblioteca
    // =====================================================================

    @Override
    public void bibliotecaCambio(String descripcionDelCambio) {
        actualizarTodo();
        estado.setText(descripcionDelCambio);
        guardar();
    }

    private void actualizarTodo() {
        for (Actualizable a : pantallas) {
            a.actualizar();
        }
        fechaSistema.setText("Fecha del sistema: " + Formato.fecha(biblioteca.hoy()));
        int d = biblioteca.getDesplazamientoDias();
        chipSimulacion.setVisible(d != 0);
        chipSimulacion.setText(" Simulación " + (d > 0 ? "+" : "") + d + " día" + (Math.abs(d) == 1 ? "" : "s") + " ");
        PoliticaMulta p = biblioteca.getPoliticaMulta();
        politica.setText("<html><font color='#A9B8CB'>MULTAS</font><br>" + p.getNombre() + "<br><font color='#A9B8CB'>"
                + p.getDescripcion() + "</font></html>");
        if (menuFija != null) {
            menuFija.setSelected(p instanceof MultaFijaDiaria);
            menuProgresiva.setSelected(p instanceof MultaProgresiva);
        }
    }

    private void guardar() {
        try {
            persistencia.guardar(biblioteca);
            guardado.setText("Guardado automático · " + persistencia.getArchivo().getFileName());
        } catch (IOException e) {
            guardado.setText("No se pudo guardar: " + e.getMessage());
            guardado.setForeground(Estilo.PELIGRO);
        }
    }

    // =====================================================================
    // Construcción de la interfaz
    // =====================================================================

    private JComponent crearMenuLateral() {
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBackground(Estilo.PRIMARIO_OSCURO);
        lateral.setPreferredSize(new Dimension(220, 0));
        lateral.setBorder(BorderFactory.createEmptyBorder(22, 0, 20, 0));

        JPanel marca = new JPanel();
        marca.setOpaque(false);
        marca.setLayout(new BoxLayout(marca, BoxLayout.Y_AXIS));
        marca.setBorder(BorderFactory.createEmptyBorder(0, 22, 26, 16));
        JLabel nombre = new JLabel("Biblioteca");
        nombre.setFont(Estilo.fuente(Font.BOLD, 21));
        nombre.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Gestión de préstamos");
        sub.setFont(Estilo.fuente(Font.PLAIN, 12));
        sub.setForeground(new Color(0xA9B8CB));
        marca.add(new IconoLibro());
        marca.add(Box.createVerticalStrut(10));
        marca.add(nombre);
        marca.add(sub);

        JPanel navegacion = new JPanel();
        navegacion.setOpaque(false);
        navegacion.setLayout(new BoxLayout(navegacion, BoxLayout.Y_AXIS));
        String[][] items = {
                {INICIO, "Ctrl+1"}, {CATALOGO, "Ctrl+2"}, {SOCIOS, "Ctrl+3"}, {PRESTAMOS, "Ctrl+4"}, {MULTAS, "Ctrl+5"}};
        for (String[] item : items) {
            BotonNavegacion b = new BotonNavegacion(item[0], item[1]);
            b.addActionListener(e -> mostrarSeccion(item[0]));
            botones.put(item[0], b);
            navegacion.add(b);
        }

        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));
        pie.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 16));
        JLabel lblAtiende = new JLabel("ATIENDE");
        lblAtiende.setFont(Estilo.fuente(Font.BOLD, 11));
        lblAtiende.setForeground(new Color(0xA9B8CB));
        lblAtiende.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Bibliotecario b : biblioteca.listarBibliotecarios()) {
            atiende.addItem(b);
        }
        atiende.setSelectedItem(biblioteca.getBibliotecarioActual());
        atiende.addActionListener(e -> {
            Bibliotecario sel = (Bibliotecario) atiende.getSelectedItem();
            if (sel != null && sel != biblioteca.getBibliotecarioActual()) {
                biblioteca.setBibliotecarioActual(sel);
            }
        });
        ComboPlano.aplicar(atiende);
        atiende.setMaximumSize(new Dimension(180, 32));
        atiende.setAlignmentX(Component.LEFT_ALIGNMENT);
        politica.setFont(Estilo.fuente(Font.PLAIN, 12));
        politica.setForeground(Color.WHITE);
        politica.setAlignmentX(Component.LEFT_ALIGNMENT);
        pie.add(lblAtiende);
        pie.add(Box.createVerticalStrut(6));
        pie.add(atiende);
        pie.add(Box.createVerticalStrut(18));
        pie.add(politica);

        lateral.add(marca, BorderLayout.NORTH);
        lateral.add(navegacion, BorderLayout.CENTER);
        lateral.add(pie, BorderLayout.SOUTH);
        return lateral;
    }

    private JComponent crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilo.SUPERFICIE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE),
                BorderFactory.createEmptyBorder(10, 26, 10, 20)));

        fechaSistema.setFont(Estilo.fuente(Font.BOLD, 13));
        fechaSistema.setForeground(Estilo.TEXTO);
        chipSimulacion.setOpaque(true);
        chipSimulacion.setBackground(new Color(0xFFF1D6));
        chipSimulacion.setForeground(Estilo.ALERTA);
        chipSimulacion.setFont(Estilo.fuente(Font.BOLD, 12));
        chipSimulacion.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izquierda.setOpaque(false);
        izquierda.add(fechaSistema);
        izquierda.add(chipSimulacion);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derecha.setOpaque(false);
        JLabel ayuda = Estilo.subtitulo("Simular paso del tiempo:");
        derecha.add(ayuda);
        derecha.add(botonTiempo("+1 día", () -> biblioteca.avanzarDias(1)));
        derecha.add(botonTiempo("+7 días", () -> biblioteca.avanzarDias(7)));
        derecha.add(botonTiempo("Volver a hoy", biblioteca::volverAHoy));

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);
        return barra;
    }

    private static JButton botonTiempo(String texto, Runnable accion) {
        JButton b = Estilo.botonSecundario(texto);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        b.addActionListener(e -> accion.run());
        return b;
    }

    private JComponent crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilo.SUPERFICIE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.BORDE),
                BorderFactory.createEmptyBorder(6, 26, 6, 20)));
        estado.setFont(Estilo.fuente(Font.PLAIN, 12));
        estado.setForeground(Estilo.TEXTO);
        barra.add(estado, BorderLayout.WEST);
        barra.add(guardado, BorderLayout.EAST);
        return barra;
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        JMenuItem guardarAhora = new JMenuItem("Guardar ahora");
        guardarAhora.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        guardarAhora.addActionListener(e -> {
            guardar();
            estado.setText("Datos guardados en " + persistencia.getArchivo().toAbsolutePath());
        });
        JMenuItem restaurar = new JMenuItem("Restaurar datos de ejemplo…");
        restaurar.addActionListener(e -> restaurarDatosDeEjemplo());
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        archivo.add(guardarAhora);
        archivo.add(restaurar);
        archivo.addSeparator();
        archivo.add(salir);

        JMenu ver = new JMenu("Ver");
        String[] secciones = {INICIO, CATALOGO, SOCIOS, PRESTAMOS, MULTAS};
        for (int i = 0; i < secciones.length; i++) {
            String s = secciones[i];
            JMenuItem item = new JMenuItem(s);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1 + i, InputEvent.CTRL_DOWN_MASK));
            item.addActionListener(e -> mostrarSeccion(s));
            ver.add(item);
        }

        JMenu config = new JMenu("Configuración");
        JMenu politicas = new JMenu("Política de multas");
        menuFija = new JRadioButtonMenuItem("Fija: " + POLITICA_FIJA.getDescripcion());
        menuProgresiva = new JRadioButtonMenuItem("Progresiva: " + POLITICA_PROGRESIVA.getDescripcion());
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(menuFija);
        grupo.add(menuProgresiva);
        menuFija.addActionListener(e -> biblioteca.setPoliticaMulta(POLITICA_FIJA));
        menuProgresiva.addActionListener(e -> biblioteca.setPoliticaMulta(POLITICA_PROGRESIVA));
        politicas.add(menuFija);
        politicas.add(menuProgresiva);
        config.add(politicas);

        JMenu ayuda = new JMenu("Ayuda");
        JMenuItem reglas = new JMenuItem("Reglas de préstamo");
        reglas.addActionListener(e -> Mensajes.info(this, "Reglas de préstamo", """
                <html><b>Categorías de socio</b><br>
                · Estudiante: hasta 3 préstamos, 7 días, 50% de descuento en multas<br>
                · Docente: hasta 5 préstamos, 14 días, 25% de descuento en multas<br>
                · Público general: hasta 2 préstamos, 10 días, sin descuento<br><br>
                <b>Materiales</b><br>
                · Libros: hasta 30 días (limitado por la categoría del socio)<br>
                · Revistas: máximo 3 días<br><br>
                <b>Condiciones</b><br>
                · Un socio con multas impagas o préstamos vencidos queda suspendido<br>
                · Cada préstamo se puede renovar una vez, si no está vencido<br>
                · La multa se calcula al devolver, según la política vigente</html>"""));
        JMenuItem acerca = new JMenuItem("Acerca de");
        acerca.addActionListener(e -> Mensajes.info(this, "Acerca de", """
                <html><b>Gestión de Biblioteca</b><br>
                Proyecto Final Integrador — Lenguajes 2<br>
                Ingeniería en Informática<br><br>
                Java + Swing · Programación Orientada a Objetos</html>"""));
        ayuda.add(reglas);
        ayuda.add(acerca);

        barra.add(archivo);
        barra.add(ver);
        barra.add(config);
        barra.add(ayuda);
        return barra;
    }

    private void restaurarDatosDeEjemplo() {
        if (!Mensajes.confirmar(this, "Restaurar datos de ejemplo",
                "Se reemplazarán todos los datos actuales por el conjunto de ejemplo.\n¿Continuar?")) {
            return;
        }
        Biblioteca nueva = DatosDeEjemplo.crear();
        try {
            persistencia.guardar(nueva);
        } catch (IOException ex) {
            Mensajes.error(this, "No se pudo guardar: " + ex.getMessage());
        }
        dispose();
        abrir(nueva, persistencia);
    }

    // =====================================================================
    // Componentes propios del menú lateral
    // =====================================================================

    /** Botón de navegación del menú lateral, con barra indicadora cuando está seleccionado. */
    private static final class BotonNavegacion extends JButton {

        private static final long serialVersionUID = 1L;
        private boolean seleccionado;

        BotonNavegacion(String texto, String atajo) {
            super(texto);
            setToolTipText(atajo);
            setFont(Estilo.fuente(Font.PLAIN, 14));
            setForeground(new Color(0xD5DEE9));
            setHorizontalAlignment(LEFT);
            setBorder(BorderFactory.createEmptyBorder(11, 24, 11, 16));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setSeleccionado(boolean valor) {
            seleccionado = valor;
            setForeground(valor ? Color.WHITE : new Color(0xD5DEE9));
            setFont(Estilo.fuente(valor ? Font.BOLD : Font.PLAIN, 14));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (seleccionado) {
                g.setColor(new Color(255, 255, 255, 28));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Estilo.ACENTO);
                g.fillRect(0, 0, 4, getHeight());
            } else if (getModel().isRollover()) {
                g.setColor(new Color(255, 255, 255, 12));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g);
        }
    }

    /** Pequeño ícono de libro abierto dibujado con Java2D. */
    private static final class IconoLibro extends JComponent {

        private static final long serialVersionUID = 1L;

        IconoLibro() {
            setPreferredSize(new Dimension(38, 30));
            setMaximumSize(new Dimension(38, 30));
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Estilo.ACENTO);
            g2.fillRoundRect(0, 0, 38, 30, 8, 8);
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(2f));
            g2.drawLine(19, 8, 19, 23);
            g2.drawArc(7, 6, 12, 6, 0, 180);
            g2.drawArc(19, 6, 12, 6, 0, 180);
            g2.drawLine(7, 9, 7, 23);
            g2.drawLine(31, 9, 31, 23);
            g2.drawLine(7, 23, 19, 23);
            g2.drawLine(19, 23, 31, 23);
            g2.dispose();
        }
    }
}
