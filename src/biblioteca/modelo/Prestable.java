package biblioteca.modelo;

import biblioteca.excepciones.OperacionInvalidaException;

/**
 * Contrato para cualquier objeto físico que puede salir de la biblioteca y volver.
 * Lo implementa {@link Ejemplar}.
 */
public interface Prestable {

    boolean estaDisponible();

    void prestar() throws OperacionInvalidaException;

    void devolver();
}
