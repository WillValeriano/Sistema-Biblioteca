package biblioteca.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// Componente visual que muestra un indicador numérico (ej: "Total Materiales", "Préstamos Activos")
public class TarjetaEstadistica extends JPanel {

    private JLabel lblTitulo;
    private JLabel lblValor;
    private JLabel lblSubtitulo;
    private Color colorAcento;

    public TarjetaEstadistica(String titulo, String valorInicial, String subtitulo, Color colorAcento) {
        this.colorAcento = colorAcento;
        setLayout(new BorderLayout(8, 4));
        setBackground(Estilo.COLOR_TARJETA);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, this.colorAcento),
                new EmptyBorder(14, 16, 14, 16)
        ));

        lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(Estilo.COLOR_NEUTRO);

        lblValor = new JLabel(valorInicial);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValor.setForeground(Estilo.COLOR_TEXTO);

        lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(Estilo.FUENTE_PEQUENA);
        lblSubtitulo.setForeground(this.colorAcento);

        add(lblTitulo, BorderLayout.NORTH);
        add(lblValor, BorderLayout.CENTER);
        add(lblSubtitulo, BorderLayout.SOUTH);
    }

    // Permite actualizar los números cuando se agrega o devuelve un libro
    public void actualizar(String nuevoValor, String nuevoSubtitulo) {
        lblValor.setText(nuevoValor);
        lblSubtitulo.setText(nuevoSubtitulo);
    }
}