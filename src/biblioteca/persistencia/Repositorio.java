package biblioteca.persistencia;

import biblioteca.modelo.Identificable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Repositorio genérico (patrón Repository). Abstrae dónde y cómo se guardan los objetos:
 * la lógica de negocio solo conoce esta interfaz.
 *
 * @param <T> cualquier entidad que tenga un identificador
 */
public interface Repositorio<T extends Identificable> {

    void guardar(T entidad);

    boolean eliminar(String id);

    Optional<T> buscarPorId(String id);

    List<T> listar();

    List<T> filtrar(Predicate<T> criterio);

    int cantidad();
}
