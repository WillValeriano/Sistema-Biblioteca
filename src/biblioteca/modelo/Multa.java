package biblioteca.modelo;

import biblioteca.excepciones.OperacionInvalidaException;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Multa por devolución tardía. Solo la crea {@link Prestamo} (composición).
 */
public class Multa implements Identificable, Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final Prestamo prestamo;
    private final int diasAtraso;
    private final double monto;
    private final LocalDate fechaGeneracion;
    private LocalDate fechaPago;

    Multa(String id, Prestamo prestamo, int diasAtraso, double monto, LocalDate fechaGeneracion) {
        this.id = id;
        this.prestamo = prestamo;
        this.diasAtraso = diasAtraso;
        this.monto = Math.round(monto * 100.0) / 100.0;
        this.fechaGeneracion = fechaGeneracion;
    }

    public void pagar(LocalDate fecha) throws OperacionInvalidaException {
        if (estaPagada()) {
            throw new OperacionInvalidaException("La multa " + id + " ya fue abonada el " + Formato.fecha(fechaPago) + ".");
        }
        this.fechaPago = fecha;
    }

    public boolean estaPagada() {
        return fechaPago != null;
    }

    @Override
    public String getId() {
        return id;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }
}
