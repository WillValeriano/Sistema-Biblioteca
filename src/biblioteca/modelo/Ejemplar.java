package biblioteca.modelo;

import biblioteca.excepciones.OperacionInvalidaException;

import java.io.Serializable;

/**
 * Copia física de un material. Es lo que realmente se presta.
 * Implementa {@link Prestable}. Su constructor es de paquete: solo
 * {@link MaterialBibliografico} puede crearlo (composición).
 */
public class Ejemplar implements Prestable, Identificable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String codigo;
    private final MaterialBibliografico material;
    private EstadoEjemplar estado;
    private String ubicacion;

    Ejemplar(String codigo, MaterialBibliografico material, String ubicacion) {
        this.codigo = codigo;
        this.material = material;
        this.ubicacion = ubicacion;
        this.estado = EstadoEjemplar.DISPONIBLE;
    }

    @Override
    public boolean estaDisponible() {
        return estado == EstadoEjemplar.DISPONIBLE;
    }

    @Override
    public void prestar() throws OperacionInvalidaException {
        if (!estaDisponible()) {
            throw new OperacionInvalidaException("El ejemplar " + codigo + " no está disponible (" + estado + ").");
        }
        estado = EstadoEjemplar.PRESTADO;
        material.registrarPrestamo();
    }

    @Override
    public void devolver() {
        estado = EstadoEjemplar.DISPONIBLE;
    }

    /** Alterna entre "en reparación" y "disponible". No se permite si está prestado. */
    public void cambiarReparacion() throws OperacionInvalidaException {
        if (estado == EstadoEjemplar.PRESTADO) {
            throw new OperacionInvalidaException("No se puede enviar a reparación un ejemplar prestado.");
        }
        estado = (estado == EstadoEjemplar.EN_REPARACION) ? EstadoEjemplar.DISPONIBLE : EstadoEjemplar.EN_REPARACION;
    }

    @Override
    public String getId() {
        return codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public MaterialBibliografico getMaterial() {
        return material;
    }

    public EstadoEjemplar getEstado() {
        return estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String toString() {
        return codigo + " · " + material.getTitulo();
    }
}
