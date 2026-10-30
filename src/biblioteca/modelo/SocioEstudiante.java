package biblioteca.modelo;

public class SocioEstudiante extends Socio {

    private String carrera;

    public SocioEstudiante(String dni, String nombre, String telefono, String email, String carrera) {
        super(dni, nombre, telefono, email);
        this.carrera = carrera;
    }

    @Override
    public int getDiasMaximoPrestamo(ConfiguracionBiblioteca config) {
        return config.getDiasPrestamoEstudiante();
    }

    @Override
    public int getCantidadMaximaLibros(ConfiguracionBiblioteca config) {
        return config.getMaxLibrosEstudiante();
    }

    @Override
    public String getTipoSocio() {
        return "Estudiante";
    }

    @Override
    public String getDetalleExtra() {
        return "Carrera: " + carrera;
    }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
}