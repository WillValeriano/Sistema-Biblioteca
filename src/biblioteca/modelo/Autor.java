package biblioteca.modelo;

import java.io.Serializable;

// Relación de AGREGACIÓN con MaterialBibliografico:
// El Autor existe por sí mismo aunque se elimine un libro.
public class Autor implements Serializable {

    private static int contadorAutores = 1;

    private int idAutor;
    private String nombreCompleto;
    private String nacionalidad;

    public Autor(String nombreCompleto, String nacionalidad) {
        this.idAutor = contadorAutores++;
        this.nombreCompleto = nombreCompleto;
        this.nacionalidad = nacionalidad;
    }

    public static void reiniciarContador() {
        contadorAutores = 1;
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