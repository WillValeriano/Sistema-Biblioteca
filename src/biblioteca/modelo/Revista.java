package biblioteca.modelo;

// Una Revista ES UN MaterialBibliografico (igual que Libro, ver comentarios ahí).
// Agrega lo propio de una revista: ISSN, número de edición y periodicidad.
public class Revista extends MaterialBibliografico {

    private String issn;
    private int numeroEdicion;
    private String periodicidad; // Ej: "Mensual", "Semanal", "Anual"

    public Revista(String titulo, Autor autor, int anioPublicacion, String genero,
                   String ubicacionEstanteria, String sinopsis, String rutaImagen,
                   int cantidadEjemplares, String issn, int numeroEdicion, String periodicidad) {
        super(titulo, autor, anioPublicacion, genero, ubicacionEstanteria, sinopsis, rutaImagen, cantidadEjemplares);
        this.issn = issn;
        this.numeroEdicion = numeroEdicion;
        this.periodicidad = periodicidad;
    }

    @Override
    public String getTipoMaterial() {
        return "Revista";
    }

    @Override
    public String getIdentificadorUnico() {
        return "ISSN: " + issn + " | Ed. #" + numeroEdicion + " (" + periodicidad + ")";
    }

    public String getIssn() { return issn; }
    public void setIssn(String issn) { this.issn = issn; }

    public int getNumeroEdicion() { return numeroEdicion; }
    public void setNumeroEdicion(int numeroEdicion) { this.numeroEdicion = numeroEdicion; }

    public String getPeriodicidad() { return periodicidad; }
    public void setPeriodicidad(String periodicidad) { this.periodicidad = periodicidad; }
}