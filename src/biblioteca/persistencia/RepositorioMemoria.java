package biblioteca.persistencia;

import biblioteca.modelo.Identificable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Implementación en memoria del {@link Repositorio}, respaldada por un LinkedHashMap
 * (búsqueda por id en O(1) y orden de inserción). Es serializable, así que se guarda
 * en disco junto con la biblioteca.
 */
public class RepositorioMemoria<T extends Identificable> implements Repositorio<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, T> datos = new LinkedHashMap<>();

    @Override
    public void guardar(T entidad) {
        datos.put(entidad.getId(), entidad);
    }

    @Override
    public boolean eliminar(String id) {
        return datos.remove(id) != null;
    }

    @Override
    public Optional<T> buscarPorId(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<T> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public List<T> filtrar(Predicate<T> criterio) {
        List<T> resultado = new ArrayList<>();
        for (T entidad : datos.values()) {
            if (criterio.test(entidad)) {
                resultado.add(entidad);
            }
        }
        return resultado;
    }

    @Override
    public int cantidad() {
        return datos.size();
    }
}
