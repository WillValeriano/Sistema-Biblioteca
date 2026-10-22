package biblioteca.modelo;

import java.time.LocalDate;

/**
 * Docente: hasta 5 libros por 14 días y 25% de descuento en multas.
 */
public class SocioDocente extends Socio {

    private static final long serialVersionUID = 1L;

    private String departamento;

    public SocioDocente(String numeroSocio, String dni, String nombre, String apellido, String email,
                        String telefono, LocalDate fechaAlta, String departamento) {
        super(numeroSocio, dni, nombre, apellido, email, telefono, fechaAlta);
        this.departamento = departamento;
    }

    @Override
    public int getMaxPrestamos() {
        return 5;
    }

    @Override
    public int getDiasPrestamo() {
        return 14;
    }

    @Override
    public double getDescuentoMulta() {
        return 0.25;
    }

    @Override
    public String getTipo() {
        return "Docente";
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}
