package biblioteca.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Paleta, tipografía y fábricas de componentes. Centralizar el estilo acá
 * hace que toda la aplicación se vea coherente y sea fácil de modificar.
 */
public final class Estilo {

    public static final Color PRIMARIO = new Color(0x1F3A5F);
    public static final Color PRIMARIO_OSCURO = new Color(0x162B47);
    public static final Color ACENTO = new Color(0x2E86AB);
    public static final Color FONDO = new Color(0xF3F5F8);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color BORDE = new Color(0xDDE3EA);
    public static final Color TEXTO = new Color(0x1E2A38);
    public static final Color TEXTO_SUAVE = new Color(0x6B7785);
    public static final Color EXITO = new Color(0x2E8B57);
    public static final Color ALERTA = new Color(0xC77700);
    public static final Color PELIGRO = new Color(0xC0392B);
    public static final Color FILA_ALTERNA = new Color(0xF7F9FC);
    public static final Color SELECCION = new Color(0xD8E9F5);

    private static final String FAMILIA = elegirFamilia();

    private Estilo() {
    }

    private static String elegirFamilia() {
        Set<String> disponibles = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String candidata : new String[]{"Segoe UI", "Inter", "Roboto", "Noto Sans", "Helvetica Neue", "DejaVu Sans"}) {
            if (disponibles.contains(candidata)) {
                return candidata;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font fuente(int estilo, float tamanio) {
        return new Font(FAMILIA, estilo, Math.round(tamanio));
    }

    /** Aplica el Look and Feel multiplataforma con la tipografía y colores de la aplicación. */
    public static void instalar() {
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception ignorada) {
            // si falla, se usa el look and feel por defecto
        }
        FontUIResource base = new FontUIResource(fuente(Font.PLAIN, 13));
        for (Object clave : UIManager.getDefaults().keySet().toArray()) {
            if (UIManager.get(clave) instanceof FontUIResource) {
                UIManager.put(clave, base);
            }
        }
        UIManager.put("Panel.background", FONDO);
        UIManager.put("OptionPane.background", SUPERFICIE);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("Table.selectionBackground", SELECCION);
        UIManager.put("Table.selectionForeground", TEXTO);
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(BORDE));
        UIManager.put("TextField.border", campoBorde());
        UIManager.put("ComboBox.background", SUPERFICIE);
        UIManager.put("ComboBox.selectionBackground", SELECCION);
        UIManager.put("ComboBox.selectionForeground", TEXTO);
        UIManager.put("List.selectionBackground", SELECCION);
        UIManager.put("List.selectionForeground", TEXTO);
        UIManager.put("MenuBar.background", SUPERFICIE);
        UIManager.put("ToolTip.background", new Color(0x2B3846));
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(4, 8, 4, 8));
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
    }

    public static Border campoBorde() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(5, 8, 5, 8));
    }

    public static Border tarjeta() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(14, 16, 14, 16));
    }

    /** Botón plano con color de fondo y efecto al pasar el mouse. */
    public static JButton boton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setFont(fuente(Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(fondo);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hover = fondo.darker();
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) {
                    b.setBackground(hover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(fondo);
            }
        });
        return b;
    }

    public static JButton botonPrimario(String texto) {
        return boton(texto, ACENTO);
    }

    /** Botón blanco con borde, para acciones secundarias. */
    public static JButton botonSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(fuente(Font.PLAIN, 13));
        b.setForeground(TEXTO);
        b.setBackground(SUPERFICIE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (b.isEnabled()) {
                    b.setBackground(FILA_ALTERNA);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(SUPERFICIE);
            }
        });
        return b;
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, 22));
        l.setForeground(TEXTO);
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.PLAIN, 13));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    public static JLabel etiquetaSeccion(String texto) {
        JLabel l = new JLabel(texto.toUpperCase());
        l.setFont(fuente(Font.BOLD, 11));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(32);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(0xEDF0F4));
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(SUPERFICIE);
        RenderizadorCelda renderizador = new RenderizadorCelda();
        tabla.setDefaultRenderer(Object.class, renderizador);
        tabla.setDefaultRenderer(Number.class, renderizador);
        tabla.getTableHeader().setDefaultRenderer(new RenderizadorEncabezado());
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tabla.setAutoCreateRowSorter(false);
    }

    public static JScrollPane scroll(JComponent contenido) {
        JScrollPane sp = new JScrollPane(contenido);
        sp.setBorder(BorderFactory.createLineBorder(BORDE));
        sp.getViewport().setBackground(SUPERFICIE);
        return sp;
    }
}
