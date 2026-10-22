package biblioteca.modelo;

import java.io.Serializable;

/**
 * Autor de un libro. Relación de AGREGACIÓN con {@link Libro}: el autor existe por sí mismo
 * y puede ser compartido por varios libros (si se elimina un libro, el autor sigue existiendo).
 */
public class Autor implements Identificable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String nombre;
    private String nacionalidad;

    public Autor(String id, String nombre, String nacionalidad) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
