package biblioteca.gui;

import biblioteca.excepciones.OperacionInvalidaException;
import biblioteca.modelo.Formato;
import biblioteca.modelo.MaterialBibliografico;
import biblioteca.modelo.Socio;
import biblioteca.servicio.Biblioteca;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Registro de un préstamo: se elige socio y material en dos listas con búsqueda.
 * Antes de confirmar, se muestra si el socio está habilitado y la fecha de vencimiento.
 */
public class DialogoPrestamo extends DialogoBase {

    private static final long serialVersionUID = 1L;

    private final transient Biblioteca biblioteca;
    private final JList<Socio> listaSocios = new JList<>();
    private final JList<MaterialBibliografico> listaMateriales = new JList<>();
    private final JLabel resumen = new JLabel(" ");

    private DialogoPrestamo(Window padre, Biblioteca biblioteca, Socio socioInicial) {
        super(padre, "Nuevo préstamo", "Atiende: " + biblioteca.getBibliotecarioActual()
                + " · Fecha: " + Formato.fecha(biblioteca.hoy()), "Registrar préstamo");
        this.biblioteca = biblioteca;

        List<Socio> socios = biblioteca.listarSocios().stream().filter(Socio::isActivo).toList();
        List<MaterialBibliografico> materiales = biblioteca.materialesConDisponibles();

        listaSocios.setCellRenderer(DialogoPrestamo.<Socio>renderer(s -> "<html><b>" + s.getNombreCompleto() + "</b><br><font color='#6B7785'>"
                + s.getNumeroSocio() + " · " + s.getTipo() + " · " + s.getEstado(biblioteca.hoy()) + "</font></html>"));
        listaMateriales.setCellRenderer(DialogoPrestamo.<MaterialBibliografico>renderer(m -> "<html><b>" + m.getTitulo() + "</b><br><font color='#6B7785'>"
                + m.getTipo() + " · " + m.getDetalle() + " · " + m.getCantidadDisponibles() + " disp.</font></html>"));

        JPanel columnas = new JPanel(new GridLayout(1, 2, 14, 0));
        columnas.setOpaque(false);
        columnas.add(columnaConBusqueda("1. Socio", "Buscar por nombre, DNI o N.º…", listaSocios, socios,
                Socio::coincideCon));
        columnas.add(columnaConBusqueda("2. Material", "Buscar por título, autor, código…", listaMateriales,
                materiales, MaterialBibliografico::coincideCon));
        agregarFilaCompleta(columnas);

        resumen.setFont(Estilo.fuente(Font.PLAIN, 13));
        resumen.setOpaque(true);
        resumen.setBackground(Estilo.FONDO);
        resumen.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        agregarFilaCompleta(resumen);

        listaSocios.addListSelectionListener(e -> actualizarResumen());
        listaMateriales.addListSelectionListener(e -> actualizarResumen());
        if (socioInicial != null) {
            listaSocios.setSelectedValue(socioInicial, true);
        }
        actualizarResumen();
    }

    public static void abrir(Window padre, Biblioteca biblioteca, Socio socioInicial) {
        new DialogoPrestamo(padre, biblioteca, socioInicial).mostrar();
    }

    private <T> JPanel columnaConBusqueda(String titulo, String ayuda, JList<T> lista, List<T> datos,
                                          BiPredicate<T, String> coincide) {
        DefaultListModel<T> modeloLista = new DefaultListModel<>();
        datos.forEach(modeloLista::addElement);
        lista.setModel(modeloLista);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFixedCellHeight(48);

        CampoBusqueda busqueda = new CampoBusqueda(ayuda, () -> { });
        busqueda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filtrar() {
                T seleccionado = lista.getSelectedValue();
                modeloLista.clear();
                for (T d : datos) {
                    if (coincide.test(d, busqueda.getText())) {
                        modeloLista.addElement(d);
                    }
                }
                if (seleccionado != null && modeloLista.contains(seleccionado)) {
                    lista.setSelectedValue(seleccionado, true);
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }
        });

        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        JPanel arriba = new JPanel(new BorderLayout(0, 6));
        arriba.setOpaque(false);
        arriba.add(Estilo.etiquetaSeccion(titulo), BorderLayout.NORTH);
        arriba.add(busqueda, BorderLayout.SOUTH);
        p.add(arriba, BorderLayout.NORTH);
        javax.swing.JScrollPane sp = Estilo.scroll(lista);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setPreferredSize(new Dimension(340, 300));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private static <T> DefaultListCellRenderer renderer(Function<T, String> texto) {
        return new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selected, boolean focus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, texto.apply((T) value), index,
                        selected, false);
                l.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                l.setFont(Estilo.fuente(Font.PLAIN, 13));
                if (!selected) {
                    l.setBackground(index % 2 == 0 ? Estilo.SUPERFICIE : Estilo.FILA_ALTERNA);
                }
                return l;
            }
        };
    }

    private void actualizarResumen() {
        Socio socio = listaSocios.getSelectedValue();
        MaterialBibliografico material = listaMateriales.getSelectedValue();
        getBotonAceptar().setEnabled(false);
        if (socio == null || material == null) {
            resumen.setForeground(Estilo.TEXTO_SUAVE);
            resumen.setText("Elegí un socio y un material para ver las condiciones del préstamo.");
            return;
        }
        LocalDate hoy = biblioteca.hoy();
        try {
            socio.verificarHabilitado(hoy);
        } catch (OperacionInvalidaException e) {
            resumen.setForeground(Estilo.PELIGRO);
            resumen.setText("<html><b>No se puede prestar:</b> " + e.getMessage() + "</html>");
            return;
        }
        int plazo = Math.min(socio.getDiasPrestamo(), material.getPlazoMaximoDias());
        String motivo = plazo < socio.getDiasPrestamo()
                ? " (limitado por ser " + material.getTipo().toLowerCase() + ")" : " (socio " + socio.getTipo() + ")";
        resumen.setForeground(Estilo.TEXTO);
        resumen.setText("<html>Plazo: <b>" + plazo + " días</b>" + motivo + " · Vence el <b>"
                + Formato.fecha(hoy.plusDays(plazo)) + "</b> · Préstamos activos del socio: "
                + socio.getPrestamosActivos().size() + " de " + socio.getMaxPrestamos() + "</html>");
        getBotonAceptar().setEnabled(true);
    }

    @Override
    protected void aceptar() {
        Socio socio = listaSocios.getSelectedValue();
        MaterialBibliografico material = listaMateriales.getSelectedValue();
        if (socio == null || material == null) {
            return;
        }
        if (Mensajes.intentar(this, () -> biblioteca.prestar(socio, material))) {
            dispose();
        }
    }
}
