package biblioteca.modelo;

// Un "enum" es una lista CERRADA de valores posibles. Un ejemplar solo puede estar
// en uno de estos 4 estados; no se puede inventar otro. Se compara con "==":
//     if (ejemplar.getEstado() == EstadoEjemplar.PRESTADO) { ... }
// Cada valor guarda además un texto lindo para mostrar en pantalla ("En Reparación").
// En UML esto se dibuja con el estereotipo <<enumeration>> arriba del nombre.
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