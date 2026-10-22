package biblioteca.modelo;

import java.time.LocalDate;

/**
 * Estudiante: hasta 3 libros por 7 días y 50% de descuento en multas.
 */
public class SocioEstudiante extends Socio {

    private static final long serialVersionUID = 1L;

    private String carrera;
    private String legajoUniversitario;

    public SocioEstudiante(String numeroSocio, String dni, String nombre, String apellido, String email,
                           String telefono, LocalDate fechaAlta, String carrera, String legajoUniversitario) {
        super(numeroSocio, dni, nombre, apellido, email, telefono, fechaAlta);
        this.carrera = carrera;
        this.legajoUniversitario = legajoUniversitario;
    }

    @Override
    public int getMaxPrestamos() {
        return 3;
    }

    @Override
    public int getDiasPrestamo() {
        return 7;
    }

    @Override
    public double getDescuentoMulta() {
        return 0.5;
    }

    @Override
    public String getTipo() {
        return "Estudiante";
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getLegajoUniversitario() {
        return legajoUniversitario;
    }

    public void setLegajoUniversitario(String legajoUniversitario) {
        this.legajoUniversitario = legajoUniversitario;
    }
}
