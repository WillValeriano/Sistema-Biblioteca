package biblioteca.modelo;

/** Estado de un préstamo, calculado a partir de sus fechas. */
public enum EstadoPrestamo {
    ACTIVO("Activo"),
    VENCIDO("Vencido"),
    DEVUELTO("Devuelto");

    private final String descripcion;

    EstadoPrestamo(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
