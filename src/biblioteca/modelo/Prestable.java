package biblioteca.modelo;

// En UML se dibuja con <<interface>> arriba del nombre.
// Define el "contrato" que debe cumplir cualquier objeto que se pueda prestar.
public interface Prestable {
    
    boolean estaDisponible();
    void prestar();
    void devolver();
}