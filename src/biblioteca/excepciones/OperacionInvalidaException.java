package biblioteca.excepciones;

// Excepción personalizada para validar las reglas de negocio de la biblioteca
public class OperacionInvalidaException extends Exception {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}