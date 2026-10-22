package biblioteca.excepciones;

/**
 * Excepción de negocio: se lanza cuando una operación viola una regla de la biblioteca
 * (socio suspendido, ejemplar no disponible, límite de préstamos alcanzado, etc.).
 * Es una excepción verificada (checked) para obligar a la interfaz a manejarla.
 */
public class OperacionInvalidaException extends Exception {

    private static final long serialVersionUID = 1L;

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
