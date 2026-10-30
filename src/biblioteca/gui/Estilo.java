package biblioteca.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

// ¿QUÉ ES ESTA CLASE?
// Una "caja de herramientas" visual. Junta en un solo lugar los colores, las fuentes y
// métodos para crear botones/campos/tablas con el mismo aspecto en todas las pantallas.
// Si querés cambiar el color principal de TODO el programa, lo cambiás solo acá.
//
// Todo es "static": se usa directamente, ej: Estilo.COLOR_PRIMARIO o Estilo.crearBotonPrimario("Guardar").
// "final" en los colores = son constantes, no se pueden modificar después.
public class Estilo {

    // --- PALETA DE COLORES MODERNA ---
    public static final Color COLOR_FONDO = new Color(241, 245, 249);        // Gris azulado muy suave
    public static final Color COLOR_TARJETA = Color.WHITE;                   // Blanco puro para paneles
    public static final Color COLOR_MENU_LATERAL = new Color(15, 23, 42);    // Azul noche oscuro
    public static final Color COLOR_MENU_HOVER = new Color(30, 41, 59);      // Azul intermedio al pasar el mouse
    public static final Color COLOR_PRIMARIO = new Color(37, 99, 235);       // Azul vibrante principal
    public static final Color COLOR_EXITO = new Color(22, 163, 74);          // Verde esmeralda
    public static final Color COLOR_ADVERTENCIA = new Color(217, 119, 6);    // Naranja / Ámbar
    public static final Color COLOR_PELIGRO = new Color(220, 38, 38);        // Rojo intenso
    public static final Color COLOR_NEUTRO = new Color(100, 116, 139);       // Gris pizarra
    public static final Color COLOR_TEXTO = new Color(30, 41, 59);           // Texto oscuro principal
    public static final Color COLOR_BORDE = new Color(226, 232, 240);       // Gris claro para líneas

    // --- TIPOGRAFÍAS ---
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_NEGRITA = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FUENTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 12);

    // --- CREADOR DE BOTONES ESTILIZADOS ---
    public static JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_NEGRITA);
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(8, 16, 8, 16));
        return boton;
    }

    public static JButton crearBotonPrimario(String texto) {
        return crearBoton(texto, COLOR_PRIMARIO);
    }

    public static JButton crearBotonExito(String texto) {
        return crearBoton(texto, COLOR_EXITO);
    }

    public static JButton crearBotonAdvertencia(String texto) {
        return crearBoton(texto, COLOR_ADVERTENCIA);
    }

    public static JButton crearBotonPeligro(String texto) {
        return crearBoton(texto, COLOR_PELIGRO);
    }

    public static JButton crearBotonSecundario(String texto) {
        return crearBoton(texto, COLOR_NEUTRO);
    }

    // --- CAMPO DE TEXTO Y BUSCADOR ---
    public static JTextField crearCampoTexto() {
        JTextField campo = new JTextField(18);
        campo.setFont(FUENTE_NORMAL);
        campo.setForeground(COLOR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return campo;
    }

    // --- ESTILIZADOR DE TABLAS CON ETIQUETAS DE COLOR AUTOMÁTICAS ---
    // Le da formato a una JTable y pinta de color las celdas según su texto:
    // verde = bien (Disponible, Activo, Pagada), rojo = problema (Vencido, Pendiente), etc.
    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(FUENTE_NORMAL);
        tabla.setRowHeight(34);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(new Color(219, 234, 254));
        tabla.setSelectionForeground(COLOR_TEXTO);
        tabla.setFillsViewportHeight(true);

        // Estilo del encabezado de la tabla
        JTableHeader header = tabla.getTableHeader();
        header.setFont(FUENTE_NEGRITA);
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(COLOR_TEXTO);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDE));

        // Un "renderizador" decide CÓMO se dibuja cada celda de la tabla.
        // "new DefaultTableCellRenderer() { ... }" es una CLASE ANÓNIMA: creamos una clase hija
        // sin nombre, en el momento, solo para cambiarle un método (getTableCellRendererComponent).
        DefaultTableCellRenderer renderizador = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                String texto = (value != null) ? value.toString() : "";

                if (!isSelected) {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    label.setForeground(COLOR_TEXTO);
                    label.setFont(FUENTE_NORMAL);

                    // Coloreamos estados clave automáticamente
                    if (texto.equalsIgnoreCase("Disponible") || texto.equalsIgnoreCase("Activo")
                            || texto.equalsIgnoreCase("Pagada") || texto.equalsIgnoreCase("En término")) {
                        label.setForeground(COLOR_EXITO);
                        label.setFont(FUENTE_NEGRITA);
                    } else if (texto.equalsIgnoreCase("Vencido") || texto.equalsIgnoreCase("Pendiente")
                            || texto.equalsIgnoreCase("Extraviado") || texto.equalsIgnoreCase("Suspendido")) {
                        label.setForeground(COLOR_PELIGRO);
                        label.setFont(FUENTE_NEGRITA);
                    } else if (texto.equalsIgnoreCase("Prestado") || texto.equalsIgnoreCase("En Reparación")) {
                        label.setForeground(COLOR_ADVERTENCIA);
                        label.setFont(FUENTE_NEGRITA);
                    } else if (texto.equalsIgnoreCase("Devuelto")) {
                        label.setForeground(COLOR_NEUTRO);
                        label.setFont(FUENTE_NEGRITA);
                    }
                }
                return label;
            }
        };

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderizador);
        }
    }

    // --- FORMATEADOR DE MONEDA: convierte 3000.0 en "$ 3.000,00" (formato argentino) ---
    public static String formatearMoneda(double monto) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
        return formato.format(monto);
    }

    // --- CARTELES EMERGENTES (ventanitas de aviso, error y confirmación Sí/No) ---
    public static void mostrarInfo(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Información del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarError(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Atención", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String pregunta) {
        int respuesta = JOptionPane.showConfirmDialog(
                padre, pregunta, "Confirmar Operación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );
        return respuesta == JOptionPane.YES_OPTION;
    }

    // --- CARGADOR Y GENERADOR DE PORTADAS DE LIBROS ---
    // Si el material tiene una foto, la usa; si no, DIBUJA una tapa azul con el título.
    public static ImageIcon crearIconoPortada(String rutaImagen, String titulo, int ancho, int alto) {
        // 1. Si el usuario cargó una foto real desde su PC y el archivo existe, la escalamos:
        if (rutaImagen != null && !rutaImagen.trim().isEmpty()) {
            File archivo = new File(rutaImagen);
            if (archivo.exists()) {
                ImageIcon original = new ImageIcon(rutaImagen);
                Image escalada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                return new ImageIcon(escalada);
            }
        }

        // 2. Si no tiene foto cargada, dibujamos una portada de libro ilustrada con su título:
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagen.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo de tapa de libro en degradado azul
        GradientPaint gradiente = new GradientPaint(0, 0, COLOR_PRIMARIO, ancho, alto, COLOR_MENU_LATERAL);
        g2.setPaint(gradiente);
        g2.fillRoundRect(0, 0, ancho, alto, 14, 14);

        // Lomo del libro a la izquierda
        g2.setColor(new Color(255, 255, 255, 45));
        g2.fillRect(10, 0, 6, alto);

        // Borde decorativo
        g2.setColor(new Color(255, 255, 255, 90));
        g2.drawRoundRect(6, 6, ancho - 13, alto - 13, 10, 10);

        // Escribimos el título centrado en la tapa
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        String textoCorto = (titulo != null && titulo.length() > 22) ? titulo.substring(0, 20) + "..." : titulo;
        FontMetrics fm = g2.getFontMetrics();
        int x = Math.max(18, (ancho - fm.stringWidth(textoCorto)) / 2);
        g2.drawString(textoCorto != null ? textoCorto : " Obra ", x, alto / 2);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(219, 234, 254));
        g2.drawString("BIBLIOTECA", (ancho - g2.getFontMetrics().stringWidth("BIBLIOTECA")) / 2, (alto / 2) + 22);

        g2.dispose();
        return new ImageIcon(imagen);
    }
}