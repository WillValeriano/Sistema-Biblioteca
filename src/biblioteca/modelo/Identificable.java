package biblioteca.modelo;

/**
 * Contrato para toda entidad que posee un identificador único dentro del sistema.
 * Lo usan los repositorios genéricos para guardar y recuperar objetos.
 */
public interface Identificable {

    String getId();
}
