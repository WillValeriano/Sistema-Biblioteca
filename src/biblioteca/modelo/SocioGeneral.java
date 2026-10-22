package biblioteca.modelo;

import java.time.LocalDate;

/**
 * Público general: hasta 2 libros por 10 días, sin descuento en multas.
 */
public class SocioGeneral extends Socio {

    private static final long serialVersionUID = 1L;

    public SocioGeneral(String numeroSocio, String dni, String nombre, String apellido, String email,
                        String telefono, LocalDate fechaAlta) {
        super(numeroSocio, dni, nombre, apellido, email, telefono, fechaAlta);
    }

    @Override
    public int getMaxPrestamos() {
        return 2;
    }

    @Override
    public int getDiasPrestamo() {
        return 10;
    }

    @Override
    public double getDescuentoMulta() {
        return 0.0;
    }

    @Override
    public String getTipo() {
        return "General";
    }
}
