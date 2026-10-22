package biblioteca.gui;

import biblioteca.modelo.Formato;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.util.Map;

/**
 * Pinta las celdas: filas alternadas, fechas y montos formateados, y los estados
 * ("Vencido", "Habilitado", "Pagada"...) con un punto de color para leerlos de un vistazo.
 */
public class RenderizadorCelda extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private static final Map<String, Color> COLORES_ESTADO = Map.ofEntries(
            Map.entry("Activo", Estilo.ACENTO),
            Map.entry("Vencido", Estilo.PELIGRO),
            Map.entry("Devuelto", Estilo.TEXTO_SUAVE),
            Map.entry("Habilitado", Estilo.EXITO),
            Map.entry("Suspendido", Estilo.ALERTA),
            Map.entry("Baja", Estilo.TEXTO_SUAVE),
            Map.entry("Pendiente", Estilo.PELIGRO),
            Map.entry("Pagada", Estilo.EXITO),
            Map.entry("Disponible", Estilo.EXITO),
            Map.entry("Prestado", Estilo.ACENTO),
            Map.entry("En reparación", Estilo.ALERTA),
            Map.entry("Sin stock", Estilo.PELIGRO));

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean foco, int fila, int columna) {
        Object mostrado = (valor instanceof LocalDate fecha) ? Formato.fecha(fecha) : valor;
        JLabel l = (JLabel) super.getTableCellRendererComponent(tabla, mostrado, seleccionada, false, fila, columna);
        l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        l.setFont(Estilo.fuente(Font.PLAIN, 13));
        l.setForeground(Estilo.TEXTO);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        if (!seleccionada) {
            l.setBackground(fila % 2 == 0 ? Estilo.SUPERFICIE : Estilo.FILA_ALTERNA);
        }

        if (valor instanceof Number || valor instanceof Moneda) {
            l.setHorizontalAlignment(SwingConstants.RIGHT);
        } else if (valor instanceof LocalDate || valor == null) {
            l.setHorizontalAlignment(SwingConstants.CENTER);
            if (valor == null) {
                l.setText("—");
                l.setForeground(Estilo.TEXTO_SUAVE);
            }
        }

        if (valor instanceof String texto && COLORES_ESTADO.containsKey(texto)) {
            l.setText("●  " + texto);
            l.setForeground(COLORES_ESTADO.get(texto));
            l.setFont(Estilo.fuente(Font.BOLD, 13));
        }
        return l;
    }
}
