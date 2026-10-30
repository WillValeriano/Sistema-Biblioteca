package biblioteca.modelo;

// Representa los estados posibles de un préstamo.
// En la tabla visual cada estado se pintará con una etiqueta de distinto color (verde, rojo, gris).
public enum EstadoPrestamo {
    ACTIVO("Activo"),
    DEVUELTO("Devuelto"),
    VENCIDO("Vencido");

    private final String descripcion;

    EstadoPrestamo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}