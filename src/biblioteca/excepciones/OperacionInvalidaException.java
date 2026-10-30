package biblioteca.excepciones;

// ¿PARA QUÉ SIRVE ESTA EXCEPCIÓN?
// Cuando alguien intenta hacer algo que las reglas no permiten
// (ej: prestarle un libro a un socio con multas impagas), la Biblioteca hace:
//     throw new OperacionInvalidaException("El socio tiene multas pendientes...");
// Eso CORTA el método en ese punto y "lanza" el error hacia quien lo llamó.
// La pantalla lo atrapa con try { ... } catch (OperacionInvalidaException e) { ... }
// y le muestra al usuario el mensaje con e.getMessage().
//
// "extends Exception" = es una excepción "chequeada": Java OBLIGA a quien llame
// al método a manejarla con try/catch (o a declararla con "throws").
public class OperacionInvalidaException extends Exception {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}