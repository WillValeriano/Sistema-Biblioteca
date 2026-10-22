package biblioteca.modelo;

/**
 * Empleado de la biblioteca que registra los préstamos y devoluciones.
 */
public class Bibliotecario extends Persona {

    private static final long serialVersionUID = 1L;

    private final String legajo;
    private String turno;

    public Bibliotecario(String legajo, String dni, String nombre, String apellido,
                         String email, String telefono, String turno) {
        super(dni, nombre, apellido, email, telefono);
        this.legajo = legajo;
        this.turno = turno;
    }

    @Override
    public String getId() {
        return legajo;
    }

    @Override
    public String getRol() {
        return "Bibliotecario";
    }

    public String getLegajo() {
        return legajo;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    @Override
    public String toString() {
        return getNombre() + " " + getApellido() + " (turno " + turno + ")";
    }
}
