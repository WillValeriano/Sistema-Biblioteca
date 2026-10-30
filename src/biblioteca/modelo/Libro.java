package biblioteca.modelo;

public class Libro extends MaterialBibliografico {

    private String isbn;
    private String editorial;
    private int numeroPaginas;

    public Libro(String titulo, Autor autor, int anioPublicacion, String genero,
                 String ubicacionEstanteria, String sinopsis, String rutaImagen,
                 int cantidadEjemplares, String isbn, String editorial, int numeroPaginas) {
        super(titulo, autor, anioPublicacion, genero, ubicacionEstanteria, sinopsis, rutaImagen, cantidadEjemplares);
        this.isbn = isbn;
        this.editorial = editorial;
        this.numeroPaginas = numeroPaginas;
    }

    @Override
    public String getTipoMaterial() {
        return "Libro";
    }

    @Override
    public String getIdentificadorUnico() {
        return "ISBN: " + isbn + " | Edit: " + editorial;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public int getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }
}