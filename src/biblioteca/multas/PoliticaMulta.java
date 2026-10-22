package biblioteca.multas;

import java.io.Serializable;

/**
 * Estrategia de cálculo de multas (patrón Strategy).
 * La biblioteca puede cambiar de política en tiempo de ejecución sin modificar
 * la clase Prestamo: solo se le pasa otra implementación de esta interfaz.
 */
public interface PoliticaMulta extends Serializable {

    /** Monto bruto (sin descuentos) por la cantidad de días de atraso. */
    double calcular(int diasAtraso);

    String getNombre();

    String getDescripcion();
}
