package biblioteca.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/** Tarjeta de indicador: etiqueta, número grande y una línea de detalle. */
public class TarjetaEstadistica extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Color acento;
    private final JLabel valor = new JLabel("0");
    private final JLabel detalle = Estilo.subtitulo(" ");

    public TarjetaEstadistica(String etiqueta, Color acento) {
        this.acento = acento;
        setBackground(Estilo.SUPERFICIE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE),
                BorderFactory.createEmptyBorder(14, 20, 14, 16)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        valor.setFont(Estilo.fuente(Font.BOLD, 28));
        valor.setForeground(Estilo.TEXTO);
        add(Estilo.etiquetaSeccion(etiqueta));
        add(Box.createVerticalStrut(6));
        add(valor);
        add(Box.createVerticalStrut(2));
        add(detalle);
    }

    public void setDatos(String valorPrincipal, String textoDetalle) {
        valor.setText(valorPrincipal);
        detalle.setText(textoDetalle);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(acento);
        g.fillRect(0, 0, 4, getHeight());
    }
}
