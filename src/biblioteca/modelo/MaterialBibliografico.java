package biblioteca.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// ¿QUÉ ES UN MATERIAL BIBLIOGRÁFICO?
// Es la OBRA en sí (el título, el autor, el año...), no una copia física.
// Las copias físicas son los Ejemplares que viven en la lista "ejemplares".
//
// Es "abstract": no se puede hacer "new MaterialBibliografico(...)".
// Siempre se crea un tipo concreto: Libro, Revista o Tesis (HERENCIA).
// Todo lo que tienen en común está acá; lo que cambia está en cada clase hija.
public abstract class MaterialBibliografico implements Serializable {

    // Contador compartido: arranca en 100, así el primer material será el #101
    private static int contadorMateriales = 100;

    protected int idMaterial;
    protected String titulo;
    protected Autor autor; // AGREGACIÓN: recibe un objeto Autor que tiene vida propia
    protected int anioPublicacion;
    protected String genero;
    protected String ubicacionEstanteria; // Ej: "Pasillo A - Estante 2"
    protected String sinopsis;
    protected String rutaImagen; // Guarda la ruta de la foto de portada en tu PC

    // COMPOSICIÓN: La lista de ejemplares nace y vive dentro del material
    protected List<Ejemplar> ejemplares;

    public MaterialBibliografico(String titulo, Autor autor, int anioPublicacion, String genero,
                                 String ubicacionEstanteria, String sinopsis, String rutaImagen,
                                 int cantidadEjemplaresIniciales) {
        contadorMateriales++;
        this.idMaterial = contadorMateriales;
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.genero = genero;
        this.ubicacionEstanteria = ubicacionEstanteria;
        this.sinopsis = sinopsis;
        this.rutaImagen = rutaImagen;

        // Inicializamos la composición creando los ejemplares adentro del constructor
        this.ejemplares = new ArrayList<>();
        for (int i = 0; i < cantidadEjemplaresIniciales; i++) {
            this.ejemplares.add(new Ejemplar(this));
        }
    }

    public static void reiniciarContador() {
        contadorMateriales = 100;
    }

    public static void actualizarContadorSiEsMayor(int idExistente) {
        if (idExistente > contadorMateriales) {
            contadorMateriales = idExistente;
        }
    }

    // POLIMORFISMO: métodos "abstract" = sin código acá.
    // Cada clase hija (Libro, Revista, Tesis) está OBLIGADA a escribirlos a su manera.
    // Ej: material.getTipoMaterial() devuelve "Libro", "Revista" o "Tesis"
    // según qué objeto sea en realidad, sin que tengamos que preguntar con un if.
    public abstract String getTipoMaterial();
    public abstract String getIdentificadorUnico(); // Mostrará el ISBN, el ISSN o el Área Académica

    // Agrega una nueva copia física a este libro
    public Ejemplar agregarNuevoEjemplar() {
        Ejemplar nuevo = new Ejemplar(this);
        this.ejemplares.add(nuevo);
        return nuevo;
    }

    // Cuenta cuántas copias están DISPONIBLES.
    // "for (Ejemplar ej : ejemplares)" se lee: "para cada ejemplar ej de la lista ejemplares..."
    public int getCantidadDisponibles() {
        int contador = 0;
        for (Ejemplar ej : ejemplares) {
            if (ej.estaDisponible()) {
                contador++;
            }
        }
        return contador;
    }

    // Busca y devuelve el primer ejemplar libre para poder prestarlo
    public Ejemplar obtenerPrimerEjemplarDisponible() {
        for (Ejemplar ej : ejemplares) {
            if (ej.estaDisponible()) {
                return ej;
            }
        }
        return null; // Devuelve null si están todos prestados o en reparación
    }

    // --- Getters y Setters para poder editar todo desde la aplicación ---
    public int getIdMaterial() { return idMaterial; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getUbicacionEstanteria() { return ubicacionEstanteria; }
    public void setUbicacionEstanteria(String ubicacionEstanteria) { this.ubicacionEstanteria = ubicacionEstanteria; }

    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }

    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }

    public List<Ejemplar> getEjemplares() { return ejemplares; }
    public int getCantidadTotalEjemplares() { return ejemplares.size(); }

    @Override
    public String toString() {
        return "[" + getTipoMaterial() + "] " + titulo + " - " + autor.getNombreCompleto();
    }
}