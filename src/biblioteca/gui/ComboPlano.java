package biblioteca.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Apariencia plana para los JComboBox, coherente con el resto de la interfaz. */
public class ComboPlano extends BasicComboBoxUI {

    public static <T> JComboBox<T> aplicar(JComboBox<T> combo) {
        combo.setUI(new ComboPlano());
        combo.setFont(Estilo.fuente(Font.PLAIN, 13));
        combo.setBackground(Estilo.SUPERFICIE);
        combo.setForeground(Estilo.TEXTO);
        combo.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        Dimension d = combo.getPreferredSize();
        combo.setPreferredSize(new Dimension(Math.max(d.width + 16, 150), 34));
        return combo;
    }

    @Override
    protected JButton createArrowButton() {
        JButton b = new JButton() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Estilo.SUPERFICIE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Estilo.TEXTO_SUAVE);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                g2.dispose();
            }
        };
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setFocusable(false);
        b.setPreferredSize(new Dimension(26, 26));
        return b;
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle bounds, boolean hasFocus) {
        g.setColor(Estilo.SUPERFICIE);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    protected java.awt.Insets getInsets() {
        return new java.awt.Insets(1, 8, 1, 1);
    }
}
