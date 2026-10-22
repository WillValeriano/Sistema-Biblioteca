package biblioteca.gui;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Gráfico de barras horizontales dibujado a mano con Java2D (sin librerías externas).
 * Se usa para el ranking de materiales más prestados.
 */
public class GraficoBarras extends JComponent {

    private static final long serialVersionUID = 1L;

    private record Barra(String etiqueta, int valor) {
    }

    private final List<Barra> barras = new ArrayList<>();

    public GraficoBarras() {
        setPreferredSize(new Dimension(420, 220));
    }

    public void limpiar() {
        barras.clear();
    }

    public void agregar(String etiqueta, int valor) {
        barras.add(new Barra(etiqueta, valor));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Estilo.SUPERFICIE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (barras.isEmpty()) {
            g2.setColor(Estilo.TEXTO_SUAVE);
            g2.setFont(Estilo.fuente(Font.PLAIN, 13));
            g2.drawString("Todavía no hay préstamos registrados.", 4, 24);
            g2.dispose();
            return;
        }

        int max = barras.stream().mapToInt(Barra::valor).max().orElse(1);
        int alto = Math.min(34, (getHeight() - 8) / barras.size());
        int anchoEtiqueta = Math.min(190, getWidth() / 3);
        int anchoDisponible = getWidth() - anchoEtiqueta - 46;
        g2.setFont(Estilo.fuente(Font.PLAIN, 13));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < barras.size(); i++) {
            Barra b = barras.get(i);
            int y = 4 + i * alto;
            int centro = y + alto / 2 + fm.getAscent() / 2 - 2;

            g2.setColor(Estilo.TEXTO);
            g2.drawString(recortar(b.etiqueta(), fm, anchoEtiqueta - 10), 0, centro);

            int largo = Math.max(6, (int) ((double) b.valor() / max * anchoDisponible));
            int altoBarra = alto - 12;
            g2.setColor(new java.awt.Color(0xE8EEF4));
            g2.fillRoundRect(anchoEtiqueta, y + 6, anchoDisponible, altoBarra, 8, 8);
            g2.setColor(i == 0 ? Estilo.PRIMARIO : Estilo.ACENTO);
            g2.fillRoundRect(anchoEtiqueta, y + 6, largo, altoBarra, 8, 8);

            g2.setColor(Estilo.TEXTO);
            g2.setFont(Estilo.fuente(Font.BOLD, 13));
            g2.drawString(String.valueOf(b.valor()), anchoEtiqueta + anchoDisponible + 10, centro);
            g2.setFont(Estilo.fuente(Font.PLAIN, 13));
        }
        g2.dispose();
    }

    private static String recortar(String texto, FontMetrics fm, int ancho) {
        if (fm.stringWidth(texto) <= ancho) {
            return texto;
        }
        String t = texto;
        while (t.length() > 1 && fm.stringWidth(t + "…") > ancho) {
            t = t.substring(0, t.length() - 1);
        }
        return t + "…";
    }
}
