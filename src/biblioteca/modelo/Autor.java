package biblioteca.modelo;

import java.io.Serializable;

// ¿QUÉ ES UN AUTOR?
// La persona (o institución) que escribió un material.
//
// Relación de AGREGACIÓN con MaterialBibliografico:
// el Autor existe por sí mismo; si se borra un libro, el autor sigue existiendo
// (y puede tener otros libros). Es la diferencia con la COMPOSICIÓN de Ejemplar.
public class Autor implements Serializable {

    // Contador compartido: el primer autor será el #1, el segundo el #2...
    private static int contadorAutores = 0;

    private int idAutor;
    private String nombreCompleto;
    private String nacionalidad;

    public Autor(String nombreCompleto, String nacionalidad) {
        contadorAutores++;
        this.idAutor = contadorAutores;
        this.nombreCompleto = nombreCompleto;
        this.nacionalidad = nacionalidad;
    }

    public static void reiniciarContador() {
        contadorAutores = 0;
    }

    public static void actualizarContadorSiEsMayor(int idExistente) {
        if (idExistente > contadorAutores) {
            contadorAutores = idExistente;
        }
    }

    public int getIdAutor() { return idAutor; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    @Override
    public String toString() {
        return nombreCompleto + " (" + nacionalidad + ")";
    }
}