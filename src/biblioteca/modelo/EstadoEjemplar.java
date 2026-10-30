package biblioteca.modelo;

// En UML esto se dibuja con el estereotipo <<enumeration>> arriba del nombre
public enum EstadoEjemplar {
    DISPONIBLE("Disponible"),
    PRESTADO("Prestado"),
    EN_REPARACION("En Reparación"),
    EXTRAVIADO("Extraviado");

    private final String descripcion;

    EstadoEjemplar(String descripcion) {
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