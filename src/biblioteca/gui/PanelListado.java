package biblioteca.gui;

import biblioteca.servicio.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

/**
 * Pantalla base de listado (patrón Template Method): arma el encabezado, la barra de
 * búsqueda, la tabla ordenable y la barra de acciones. Cada subclase solo define
 * QUÉ columnas mostrar, DE DÓNDE salen los datos y CÓMO se filtra.
 *
 * @param <T> tipo de objeto listado (MaterialBibliografico, Socio, Prestamo, Multa)
 */
public abstract class PanelListado<T> extends JPanel implements Actualizable {

    private static final long serialVersionUID = 1L;

    protected final transient Biblioteca biblioteca;
    protected final ModeloTabla<T> modelo = new ModeloTabla<>();
    protected final JTable tabla;
    private final TableRowSorter<ModeloTabla<T>> sorter;
    private final CampoBusqueda busqueda;
    private final JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private final JLabel contador = Estilo.subtitulo("");
    private final JLabel resumen = new JLabel();

    protected PanelListado(Biblioteca biblioteca, String titulo, String subtitulo, String ayudaBusqueda) {
        super(new BorderLayout(0, 14));
        this.biblioteca = biblioteca;
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(22, 26, 18, 26));

        definirColumnas(modelo);
        tabla = new JTable(modelo);
        Estilo.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modelo.aplicarAnchos(tabla);
        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionado().ifPresent(PanelListado.this::alDobleClic);
                }
            }
        });

        busqueda = new CampoBusqueda(ayudaBusqueda, this::aplicarFiltro);

        // ---- Encabezado ----
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Estilo.titulo(titulo));
        textos.add(Box.createVerticalStrut(2));
        textos.add(Estilo.subtitulo(subtitulo));

        resumen.setFont(Estilo.fuente(Font.BOLD, 14));
        resumen.setForeground(Estilo.TEXTO);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(resumen, BorderLayout.EAST);

        // ---- Barra de búsqueda y filtros ----
        filtros.setOpaque(false);
        filtros.add(busqueda);
        acciones.setOpaque(false);
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.add(filtros, BorderLayout.WEST);
        barra.add(acciones, BorderLayout.EAST);

        JPanel norte = new JPanel(new BorderLayout(0, 14));
        norte.setOpaque(false);
        norte.add(encabezado, BorderLayout.NORTH);
        norte.add(barra, BorderLayout.SOUTH);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.add(contador, BorderLayout.WEST);
        pie.add(Estilo.subtitulo("Doble clic en una fila para ver el detalle · clic en el encabezado para ordenar"),
                BorderLayout.EAST);

        add(norte, BorderLayout.NORTH);
        add(Estilo.scroll(tabla), BorderLayout.CENTER);
        add(pie, BorderLayout.SOUTH);
    }

    // ---- Métodos que define cada subclase (los "pasos" del template) ----

    protected abstract void definirColumnas(ModeloTabla<T> modelo);

    protected abstract List<T> obtenerDatos();

    protected abstract boolean coincide(T fila, String texto);

    /** Filtro adicional (combo de tipo/estado). Por defecto acepta todo. */
    protected boolean pasaFiltroExtra(T fila) {
        return true;
    }

    protected void alDobleClic(T fila) {
    }

    /** Texto destacado a la derecha del título (ej.: deuda total). */
    protected String textoResumen() {
        return "";
    }

    // ---- Comportamiento común ----

    @Override
    public void actualizar() {
        Optional<T> anterior = seleccionado();
        modelo.setFilas(obtenerDatos());
        aplicarFiltro();
        anterior.ifPresent(this::seleccionar);
        resumen.setText(textoResumen());
    }

    protected final void aplicarFiltro() {
        String texto = busqueda.getText();
        sorter.setRowFilter(new RowFilter<ModeloTabla<T>, Integer>() {
            @Override
            public boolean include(Entry<? extends ModeloTabla<T>, ? extends Integer> entrada) {
                T fila = modelo.getFila(entrada.getIdentifier());
                return pasaFiltroExtra(fila) && coincide(fila, texto);
            }
        });
        int visibles = tabla.getRowCount();
        int total = modelo.getRowCount();
        contador.setText(visibles == total ? total + " registros" : visibles + " de " + total + " registros");
    }

    protected Optional<T> seleccionado() {
        int vista = tabla.getSelectedRow();
        if (vista < 0) {
            return Optional.empty();
        }
        return Optional.of(modelo.getFila(tabla.convertRowIndexToModel(vista)));
    }

    /** Devuelve la fila seleccionada o avisa al usuario que debe elegir una. */
    protected Optional<T> requerirSeleccion(String queElegir) {
        Optional<T> sel = seleccionado();
        if (sel.isEmpty()) {
            Mensajes.info(this, "Seleccioná un registro", "Primero seleccioná " + queElegir + " en la tabla.");
        }
        return sel;
    }

    protected void seleccionar(T fila) {
        int indiceModelo = modelo.indiceDe(fila);
        if (indiceModelo < 0) {
            return;
        }
        int vista = tabla.convertRowIndexToView(indiceModelo);
        if (vista >= 0) {
            tabla.setRowSelectionInterval(vista, vista);
            tabla.scrollRectToVisible(tabla.getCellRect(vista, 0, true));
        }
    }

    protected void agregarFiltro(JComponent componente) {
        if (componente instanceof javax.swing.JComboBox<?> combo) {
            ComboPlano.aplicar(combo);
        }
        filtros.add(componente);
    }

    protected void agregarAccion(JButton boton) {
        acciones.add(boton);
    }
}
