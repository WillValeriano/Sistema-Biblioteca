package biblioteca.gui;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Insets;

/** Campo de texto con texto de ayuda (placeholder) y aviso en cada tecla. */
public class CampoBusqueda extends JTextField {

    private static final long serialVersionUID = 1L;

    private final String ayuda;

    public CampoBusqueda(String ayuda, Runnable alCambiar) {
        this.ayuda = ayuda;
        setFont(Estilo.fuente(Font.PLAIN, 13));
        setBorder(Estilo.campoBorde());
        setPreferredSize(new Dimension(280, 34));
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                alCambiar.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                alCambiar.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                alCambiar.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Estilo.TEXTO_SUAVE);
            g2.setFont(getFont());
            Insets in = getInsets();
            int y = (getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
            g2.drawString(ayuda, in.left, y);
            g2.dispose();
        }
    }
}
