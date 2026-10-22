package biblioteca.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;

/**
 * Ventana modal base para los formularios: título, cuerpo con etiquetas alineadas
 * y botones Cancelar / Aceptar. Las subclases agregan sus campos y definen {@link #aceptar()}.
 */
public abstract class DialogoBase extends JDialog {

    private static final long serialVersionUID = 1L;

    protected final JPanel cuerpo = new JPanel(new GridBagLayout());
    private final JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private final JButton botonAceptar;
    private int filaActual;

    protected DialogoBase(Window padre, String titulo, String descripcion, String textoAceptar) {
        super(padre, titulo, ModalityType.APPLICATION_MODAL);
        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(Estilo.SUPERFICIE);
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 24, 18, 24));

        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(titulo);
        t.setFont(Estilo.fuente(Font.BOLD, 18));
        t.setForeground(Estilo.TEXTO);
        encabezado.add(t);
        if (descripcion != null) {
            encabezado.add(Box.createVerticalStrut(3));
            encabezado.add(Estilo.subtitulo(descripcion));
        }

        cuerpo.setOpaque(false);

        JButton cancelar = Estilo.botonSecundario("Cancelar");
        cancelar.addActionListener(e -> dispose());
        botonAceptar = Estilo.botonPrimario(textoAceptar);
        botonAceptar.addActionListener(e -> aceptar());
        botones.setOpaque(false);
        botones.add(cancelar);
        botones.add(botonAceptar);

        raiz.add(encabezado, BorderLayout.NORTH);
        raiz.add(cuerpo, BorderLayout.CENTER);
        raiz.add(botones, BorderLayout.SOUTH);
        setContentPane(raiz);
        getRootPane().setDefaultButton(botonAceptar);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    protected abstract void aceptar();

    /** Agrega una fila "Etiqueta: [campo]" al formulario. */
    protected void agregarCampo(String etiqueta, JComponent campo) {
        JLabel l = new JLabel(etiqueta);
        l.setFont(Estilo.fuente(Font.BOLD, 12));
        l.setForeground(Estilo.TEXTO_SUAVE);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = filaActual;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 0, 5, 14);
        cuerpo.add(l, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = filaActual;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(5, 0, 5, 0);
        cuerpo.add(campo, c);
        filaActual++;
    }

    /** Agrega un componente que ocupa todo el ancho (notas, resúmenes). */
    protected void agregarFilaCompleta(JComponent componente) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = filaActual++;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(8, 0, 4, 0);
        cuerpo.add(componente, c);
    }

    protected JButton getBotonAceptar() {
        return botonAceptar;
    }

    protected void mostrar() {
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    protected static javax.swing.JTextField campo(int columnas) {
        javax.swing.JTextField f = new javax.swing.JTextField(columnas);
        f.setFont(Estilo.fuente(Font.PLAIN, 13));
        f.setBorder(Estilo.campoBorde());
        return f;
    }
}
