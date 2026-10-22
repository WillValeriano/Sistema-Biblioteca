package biblioteca.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Obra del catálogo (abstracta). Superclase de {@link Libro} y {@link Revista}.
 *
 * Relación de COMPOSICIÓN con {@link Ejemplar}: los ejemplares solo se crean desde el material
 * (método {@link #agregarEjemplar(String)}) y no tienen sentido sin él.
 * Si se elimina el material, se eliminan sus ejemplares.
 */
public abstract class MaterialBibliografico implements Identificable, Buscable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String codigo;
    private String titulo;
    private int anioPublicacion;
    private String categoria;
    private int vecesPrestado;
    private final List<Ejemplar> ejemplares = new ArrayList<>();

    protected MaterialBibliografico(String codigo, String titulo, int anioPublicacion, String categoria) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.anioPublicacion = anioPublicacion;
        this.categoria = categoria;
    }

    /** "Libro" o "Revista". */
    public abstract String getTipo();

    /** Plazo máximo que admite este tipo de material, más allá de la categoría del socio. */
    public abstract int getPlazoMaximoDias();

    /** Dato descriptivo según el tipo: autores para libros, número/edición para revistas. */
    public abstract String getDetalle();

    /** Crea un nuevo ejemplar físico perteneciente a este material (composición). */
    public Ejemplar agregarEjemplar(String ubicacion) {
        String codigoEjemplar = codigo + "-" + String.format("%02d", ejemplares.size() + 1);
        Ejemplar nuevo = new Ejemplar(codigoEjemplar, this, ubicacion);
        ejemplares.add(nuevo);
        return nuevo;
    }

    public Optional<Ejemplar> primerEjemplarDisponible() {
        for (Ejemplar e : ejemplares) {
            if (e.estaDisponible()) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    public int getCantidadDisponibles() {
        int cantidad = 0;
        for (Ejemplar e : ejemplares) {
            if (e.estaDisponible()) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public boolean tieneEjemplaresPrestados() {
        for (Ejemplar e : ejemplares) {
            if (e.getEstado() == EstadoEjemplar.PRESTADO) {
                return true;
            }
        }
        return false;
    }

    void registrarPrestamo() {
        vecesPrestado++;
    }

    @Override
    public boolean coincideCon(String texto) {
        return Buscable.contieneAlguno(texto, codigo, titulo, categoria, getDetalle(), String.valueOf(anioPublicacion));
    }

    @Override
    public String getId() {
        return codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getVecesPrestado() {
        return vecesPrestado;
    }

    public List<Ejemplar> getEjemplares() {
        return Collections.unmodifiableList(ejemplares);
    }

    @Override
    public String toString() {
        return titulo + " (" + getTipo() + ", " + anioPublicacion + ")";
    }
}
