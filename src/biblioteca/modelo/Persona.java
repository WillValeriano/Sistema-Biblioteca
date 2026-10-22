package biblioteca.modelo;

import java.io.Serializable;

/**
 * Clase abstracta base de toda persona registrada en el sistema.
 * Superclase de {@link Socio} y {@link Bibliotecario}.
 */
public abstract class Persona implements Identificable, Buscable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    protected Persona(String dni, String nombre, String apellido, String email, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
    }

    /** Cada subclase indica qué rol cumple la persona en la biblioteca. */
    public abstract String getRol();

    public String getNombreCompleto() {
        return apellido + ", " + nombre;
    }

    @Override
    public boolean coincideCon(String texto) {
        return Buscable.contieneAlguno(texto, dni, nombre, apellido, email, getId());
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
