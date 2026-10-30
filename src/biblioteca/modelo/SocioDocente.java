package biblioteca.modelo;

// Un SocioDocente ES UN Socio. Igual que SocioEstudiante (ver comentarios ahí),
// pero con los límites de docente y con un departamento en vez de carrera.
public class SocioDocente extends Socio {

    private String departamento;

    public SocioDocente(String dni, String nombre, String telefono, String email, String departamento) {
        super(dni, nombre, telefono, email);
        this.departamento = departamento;
    }

    @Override
    public int getDiasMaximoPrestamo(ConfiguracionBiblioteca config) {
        return config.getDiasPrestamoDocente();
    }

    @Override
    public int getCantidadMaximaLibros(ConfiguracionBiblioteca config) {
        return config.getMaxLibrosDocente();
    }

    @Override
    public String getTipoSocio() {
        return "Docente";
    }

    @Override
    public String getDetalleExtra() {
        return "Depto: " + departamento;
    }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}