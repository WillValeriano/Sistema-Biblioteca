package biblioteca.gui;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Modelo de tabla genérico: cada columna se define con un nombre, un tipo y una función
 * que extrae el valor del objeto de la fila. Así un mismo modelo sirve para
 * libros, socios, préstamos o multas sin escribir un TableModel por cada uno.
 *
 * @param <T> tipo de objeto que representa cada fila
 */
public class ModeloTabla<T> extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private record Columna<T>(String nombre, Class<?> tipo, int ancho, Function<T, Object> extractor) {
    }

    private final List<Columna<T>> columnas = new ArrayList<>();
    private final List<T> filas = new ArrayList<>();

    public ModeloTabla<T> columna(String nombre, Class<?> tipo, int ancho, Function<T, Object> extractor) {
        columnas.add(new Columna<>(nombre, tipo, ancho, extractor));
        return this;
    }

    public void setFilas(List<T> nuevas) {
        filas.clear();
        filas.addAll(nuevas);
        fireTableDataChanged();
    }

    public T getFila(int indiceModelo) {
        return filas.get(indiceModelo);
    }

    public int indiceDe(T fila) {
        return filas.indexOf(fila);
    }

    /** Aplica los anchos preferidos definidos para cada columna. */
    public void aplicarAnchos(JTable tabla) {
        for (int i = 0; i < columnas.size(); i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(columnas.get(i).ancho());
        }
    }

    @Override
    public int getRowCount() {
        return filas.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.size();
    }

    @Override
    public String getColumnName(int columna) {
        return columnas.get(columna).nombre();
    }

    @Override
    public Class<?> getColumnClass(int columna) {
        return columnas.get(columna).tipo();
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        return columnas.get(columna).extractor().apply(filas.get(fila));
    }
}
