package biblioteca.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Libro del catálogo. Agrega a uno o más {@link Autor} (agregación).
 */
public class Libro extends MaterialBibliografico {

    private static final long serialVersionUID = 1L;

    private String isbn;
    private String editorial;
    private final List<Autor> autores = new ArrayList<>();

    public Libro(String codigo, String titulo, int anioPublicacion, String categoria,
                 String isbn, String editorial, List<Autor> autores) {
        super(codigo, titulo, anioPublicacion, categoria);
        this.isbn = isbn;
        this.editorial = editorial;
        this.autores.addAll(autores);
    }

    @Override
    public String getTipo() {
        return "Libro";
    }

    /** Un libro puede prestarse hasta 30 días (el plazo real lo limita la categoría del socio). */
    @Override
    public int getPlazoMaximoDias() {
        return 30;
    }

    @Override
    public String getDetalle() {
        return autores.stream().map(Autor::getNombre).collect(Collectors.joining(", "));
    }

    @Override
    public boolean coincideCon(String texto) {
        return super.coincideCon(texto) || Buscable.contieneAlguno(texto, isbn, editorial);
    }

    public void setAutores(List<Autor> nuevos) {
        autores.clear();
        autores.addAll(nuevos);
    }

    public List<Autor> getAutores() {
        return Collections.unmodifiableList(autores);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }
}
