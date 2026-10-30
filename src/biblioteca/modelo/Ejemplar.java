package biblioteca.modelo;

import java.io.Serializable;

// Implementa la interfaz Prestable (cumple el contrato de préstamo)
// Y forma parte de una COMPOSICIÓN dentro de MaterialBibliografico.
public class Ejemplar implements Prestable, Serializable {

    private static int contadorEjemplares = 1000;

    private String codigoInventario; // Ej: "EJ-1001"
    private MaterialBibliografico material; // A qué libro/revista pertenece esta copia
    private EstadoEjemplar estado;
    private String observacion; // Ej: "Buen estado", "Tapa rayada", "En encuadernación"

    public Ejemplar(MaterialBibliografico material) {
        contadorEjemplares++;
        this.codigoInventario = "EJ-" + contadorEjemplares;
        this.material = material;
        this.estado = EstadoEjemplar.DISPONIBLE;
        this.observacion = "Ejemplar en buen estado";
    }

    public static void reiniciarContador() {
        contadorEjemplares = 1000;
    }

    public static void actualizarContadorSiEsMayor(int numero) {
        if (numero > contadorEjemplares) {
            contadorEjemplares = numero;
        }
    }

    // --- Métodos obligatorios de la interfaz Prestable ---
    @Override
    public boolean estaDisponible() {
        return this.estado == EstadoEjemplar.DISPONIBLE;
    }

    @Override
    public void prestar() {
        if (estaDisponible()) {
            this.estado = EstadoEjemplar.PRESTADO;
        }
    }

    @Override
    public void devolver() {
        this.estado = EstadoEjemplar.DISPONIBLE;
    }

    // --- Getters y Setters (permiten mandar el ejemplar a reparación desde la GUI) ---
    public String getCodigoInventario() { return codigoInventario; }
    public MaterialBibliografico getMaterial() { return material; }

    public EstadoEjemplar getEstado() { return estado; }
    public void setEstado(EstadoEjemplar estado) { this.estado = estado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    @Override
    public String toString() {
        return codigoInventario + " - " + material.getTitulo() + " [" + estado.getDescripcion() + "]";
    }
}