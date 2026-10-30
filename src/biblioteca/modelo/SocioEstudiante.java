package biblioteca.modelo;

// Un SocioEstudiante ES UN Socio (que a su vez ES UNA Persona).
// Lo único que cambia respecto de los otros socios es CUÁNTOS días y CUÁNTOS libros
// puede llevarse: se los pide a la ConfiguracionBiblioteca. Ese es el POLIMORFISMO:
// la Biblioteca llama a socio.getCantidadMaximaLibros(config) sin saber de qué tipo es el socio.
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