package biblioteca.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Ventana de detalle genérica: una tabla de solo lectura (ej.: ejemplares de un libro,
 * historial de un socio) y, opcionalmente, acciones sobre la fila elegida.
 */
public class DialogoDetalle<T> extends JDialog {

    private static final long serialVersionUID = 1L;

    private final ModeloTabla<T> modelo;
    private final transient Supplier<List<T>> datos;
    private final JTable tabla;
    private final JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

    public DialogoDetalle(Window padre, String titulo, String subtitulo, ModeloTabla<T> modelo,
                          Supplier<List<T>> datos) {
        super(padre, titulo, ModalityType.APPLICATION_MODAL);
        this.modelo = modelo;
        this.datos = datos;

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBackground(Estilo.SUPERFICIE);
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 24, 18, 24));

        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(titulo);
        t.setFont(Estilo.fuente(Font.BOLD, 18));
        encabezado.add(t);
        encabezado.add(Box.createVerticalStrut(3));
        encabezado.add(Estilo.subtitulo(subtitulo));

        tabla = new JTable(modelo);
        Estilo.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modelo.aplicarAnchos(tabla);
        javax.swing.JScrollPane sp = Estilo.scroll(tabla);
        sp.setPreferredSize(new Dimension(760, 300));

        JButton cerrar = Estilo.botonSecundario("Cerrar");
        cerrar.addActionListener(e -> dispose());
        acciones.setOpaque(false);
        acciones.add(cerrar);

        raiz.add(encabezado, BorderLayout.NORTH);
        raiz.add(sp, BorderLayout.CENTER);
        raiz.add(acciones, BorderLayout.SOUTH);
        setContentPane(raiz);
        refrescar();
    }

    /** Agrega un botón que actúa sobre la fila seleccionada y luego refresca la tabla. */
    public DialogoDetalle<T> accionSobreSeleccion(String texto, Consumer<T> accion) {
        JButton b = Estilo.botonSecundario(texto);
        b.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                Mensajes.info(this, "Seleccioná un registro", "Primero seleccioná una fila.");
                return;
            }
            accion.accept(modelo.getFila(tabla.convertRowIndexToModel(fila)));
            refrescar();
        });
        acciones.add(b, acciones.getComponentCount() - 1);
        return this;
    }

    /** Agrega un botón que no necesita selección. */
    public DialogoDetalle<T> accion(String texto, Runnable accion) {
        JButton b = Estilo.botonPrimario(texto);
        b.addActionListener(e -> {
            accion.run();
            refrescar();
        });
        acciones.add(b, acciones.getComponentCount() - 1);
        return this;
    }

    private void refrescar() {
        modelo.setFilas(datos.get());
    }

    public void mostrar() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
