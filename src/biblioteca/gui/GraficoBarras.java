package biblioteca.gui;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// Dibuja un gráfico de barras horizontales para el Panel de Inicio
public class GraficoBarras extends JPanel {

    private String titulo;
    private Map<String, Integer> datos;
    private Map<String, Color> colores;

    public GraficoBarras(String titulo) {
        this.titulo = titulo;
        this.datos = new LinkedHashMap<>();
        this.colores = new LinkedHashMap<>();
        setBackground(Estilo.COLOR_TARJETA);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.COLOR_BORDE, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        setPreferredSize(new Dimension(350, 220));
    }

    public void limpiarDatos() {
        datos.clear();
        colores.clear();
    }

    public void agregarBarra(String etiqueta, int valor, Color color) {
        datos.put(etiqueta, valor);
        colores.put(etiqueta, color);
        repaint(); // Le avisa a Swing que vuelva a dibujar el gráfico
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujamos el título del gráfico
        g2.setFont(Estilo.FUENTE_SUBTITULO);
        g2.setColor(Estilo.COLOR_TEXTO);
        g2.drawString(titulo, 16, 28);

        if (datos.isEmpty()) {
            g2.setFont(Estilo.FUENTE_NORMAL);
            g2.setColor(Estilo.COLOR_NEUTRO);
            g2.drawString("Sin datos registrados", 16, 70);
            return;
        }

        // Buscamos el valor máximo para calcular el largo proporcional de cada barra
        int maxValor = 1;
        for (int v : datos.values()) {
            if (v > maxValor) {
                maxValor = v;
            }
        }

        int y = 55;
        int anchoMaximoBarra = Math.max(80, getWidth() - 190);

        for (Map.Entry<String, Integer> entrada : datos.entrySet()) {
            String etiqueta = entrada.getKey();
            int valor = entrada.getValue();
            Color color = colores.getOrDefault(etiqueta, Estilo.COLOR_PRIMARIO);

            // Etiqueta a la izquierda
            g2.setFont(Estilo.FUENTE_NORMAL);
            g2.setColor(Estilo.COLOR_TEXTO);
            g2.drawString(etiqueta, 16, y + 16);

            // Fondo gris de la barra
            int xBarra = 135;
            g2.setColor(new Color(241, 245, 249));
            g2.fillRoundRect(xBarra, y, anchoMaximoBarra, 22, 8, 8);

            // Barra rellena proporcional
            int anchoRelleno = (int) ((valor / (double) maxValor) * anchoMaximoBarra);
            if (valor > 0 && anchoRelleno < 10) {
                anchoRelleno = 10;
            }
            g2.setColor(color);
            g2.fillRoundRect(xBarra, y, anchoRelleno, 22, 8, 8);

            // Número a la derecha de la barra
            g2.setFont(Estilo.FUENTE_NEGRITA);
            g2.setColor(Estilo.COLOR_TEXTO);
            g2.drawString(String.valueOf(valor), xBarra + anchoMaximoBarra + 10, y + 16);

            y += 36;
        }
    }
}