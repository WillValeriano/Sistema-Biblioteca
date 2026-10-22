package biblioteca.modelo;

/** Estados posibles de un ejemplar físico. */
public enum EstadoEjemplar {
    DISPONIBLE("Disponible"),
    PRESTADO("Prestado"),
    EN_REPARACION("En reparación");

    private final String descripcion;

    EstadoEjemplar(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
