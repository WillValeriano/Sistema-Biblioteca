package biblioteca.modelo;

public class SocioGeneral extends Socio {

    private String ocupacion;

    public SocioGeneral(String dni, String nombre, String telefono, String email, String ocupacion) {
        super(dni, nombre, telefono, email);
        this.ocupacion = ocupacion;
    }

    @Override
    public int getDiasMaximoPrestamo(ConfiguracionBiblioteca config) {
        return config.getDiasPrestamoGeneral();
    }

    @Override
    public int getCantidadMaximaLibros(ConfiguracionBiblioteca config) {
        return config.getMaxLibrosGeneral();
    }

    @Override
    public String getTipoSocio() {
        return "General";
    }

    @Override
    public String getDetalleExtra() {
        return "Ocupación: " + ocupacion;
    }

    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }
}