package biblioteca.modelo;

// Una Tesis ES UN MaterialBibliografico (igual que Libro, ver comentarios ahí).
// Agrega lo propio de una tesis: director, facultad y grado académico.
public class Tesis extends MaterialBibliografico {

    private String directorTesis;
    private String facultad;
    private String gradoAcademico; // Ej: "Ingeniería", "Licenciatura", "Doctorado"

    public Tesis(String titulo, Autor autor, int anioPublicacion, String genero,
                 String ubicacionEstanteria, String sinopsis, String rutaImagen,
                 int cantidadEjemplares, String directorTesis, String facultad, String gradoAcademico) {
        super(titulo, autor, anioPublicacion, genero, ubicacionEstanteria, sinopsis, rutaImagen, cantidadEjemplares);
        this.directorTesis = directorTesis;
        this.facultad = facultad;
        this.gradoAcademico = gradoAcademico;
    }

    @Override
    public String getTipoMaterial() {
        return "Tesis";
    }

    @Override
    public String getIdentificadorUnico() {
        return gradoAcademico + " - " + facultad + " (Dir: " + directorTesis + ")";
    }

    public String getDirectorTesis() { return directorTesis; }
    public void setDirectorTesis(String directorTesis) { this.directorTesis = directorTesis; }

    public String getFacultad() { return facultad; }
    public void setFacultad(String facultad) { this.facultad = facultad; }

    public String getGradoAcademico() { return gradoAcademico; }
    public void setGradoAcademico(String gradoAcademico) { this.gradoAcademico = gradoAcademico; }
}