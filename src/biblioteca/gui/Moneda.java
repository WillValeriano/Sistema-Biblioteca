package biblioteca.gui;

import biblioteca.modelo.Formato;

/**
 * Envoltorio de un monto para las tablas: se muestra formateado ("$ 1.500,00")
 * pero se ordena por su valor numérico.
 */
public record Moneda(double valor) implements Comparable<Moneda> {

    @Override
    public int compareTo(Moneda otra) {
        return Double.compare(valor, otra.valor);
    }

    @Override
    public String toString() {
        return Formato.moneda(valor);
    }
}
