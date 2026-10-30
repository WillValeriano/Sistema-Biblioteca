package biblioteca.modelo;

// Socio hereda de Persona (tiene dni, nombre, telefono, email)
// Y a su vez es "abstract" porque cada socio debe ser Estudiante, Docente o General.
public abstract class Socio extends Persona {

    // Contador automático para que la app asigne el número de socio sola (1001, 1002...)
    private static int contadorSocios = 1000;

    protected int nroSocio;
    protected boolean activo; // Por si queremos suspender o dar de baja a un socio

    public Socio(String dni, String nombre, String telefono, String email) {
        super(dni, nombre, telefono, email);
        contadorSocios++;
        this.nroSocio = contadorSocios;
        this.activo = true;
    }

    // Métodos para reiniciar o sincronizar el contador automático
    public static void reiniciarContador() {
        contadorSocios = 1000;
    }

    public static void actualizarContadorSiEsMayor(int nroExistente) {
        if (nroExistente > contadorSocios) {
            contadorSocios = nroExistente;
        }
    }

    // POLIMORFISMO CONECTADO A TU PANTALLA DE EDICIÓN:
    // Cada tipo de socio sabe qué beneficio pedirle a la Configuración de la biblioteca.
    public abstract int getDiasMaximoPrestamo(ConfiguracionBiblioteca config);
    public abstract int getCantidadMaximaLibros(ConfiguracionBiblioteca config);
    public abstract String getTipoSocio();
    public abstract String getDetalleExtra(); // Mostrará la carrera, el departamento o la ocupación

    @Override
    public String getRol() {
        return "Socio " + getTipoSocio();
    }

    public int getNroSocio() { return nroSocio; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Socio #" + nroSocio + " - " + nombre + " (" + getTipoSocio() + ")";
    }
}