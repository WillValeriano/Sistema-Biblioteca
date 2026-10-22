package biblioteca.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

/** Encabezado de tabla plano, con indicador de orden (▲ / ▼). */
public class RenderizadorEncabezado extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean foco, int fila, int columna) {
        String texto = String.valueOf(valor);
        RowSorter<?> sorter = tabla.getRowSorter();
        if (sorter != null) {
            List<? extends RowSorter.SortKey> claves = sorter.getSortKeys();
            if (!claves.isEmpty() && claves.get(0).getColumn() == tabla.convertColumnIndexToModel(columna)) {
                texto += claves.get(0).getSortOrder() == SortOrder.ASCENDING ? "  ▲" : "  ▼";
            }
        }
        JLabel l = (JLabel) super.getTableCellRendererComponent(tabla, texto, false, false, fila, columna);
        l.setFont(Estilo.fuente(Font.BOLD, 12));
        l.setForeground(Estilo.TEXTO_SUAVE);
        l.setBackground(Estilo.FONDO);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDE),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        return l;
    }
}
