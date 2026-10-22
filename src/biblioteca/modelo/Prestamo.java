package biblioteca.modelo;

import biblioteca.excepciones.OperacionInvalidaException;
import biblioteca.multas.PoliticaMulta;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Préstamo de un {@link Ejemplar} a un {@link Socio}, registrado por un {@link Bibliotecario}.
 * Al devolverse con atraso genera una {@link Multa} (composición: la multa pertenece al préstamo).
 */
public class Prestamo implements Identificable, Serializable {

    private static final long serialVersionUID = 1L;
    public static final int MAX_RENOVACIONES = 1;

    private final String id;
    private final Socio socio;
    private final Ejemplar ejemplar;
    private final Bibliotecario bibliotecario;
    private final LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucion;
    private int renovaciones;
    private Multa multa;

    /**
     * Crea el préstamo, marca el ejemplar como prestado y lo registra en el historial del socio.
     * El plazo es el menor entre lo que permite la categoría del socio y el tipo de material.
     */
    public Prestamo(String id, Socio socio, Ejemplar ejemplar, Bibliotecario bibliotecario, LocalDate fecha)
            throws OperacionInvalidaException {
        socio.verificarHabilitado(fecha);
        ejemplar.prestar();
        this.id = id;
        this.socio = socio;
        this.ejemplar = ejemplar;
        this.bibliotecario = bibliotecario;
        this.fechaPrestamo = fecha;
        this.fechaVencimiento = fecha.plusDays(calcularPlazo());
        socio.agregarPrestamo(this);
    }

    private int calcularPlazo() {
        return Math.min(socio.getDiasPrestamo(), ejemplar.getMaterial().getPlazoMaximoDias());
    }

    /**
     * Registra la devolución. Si hay atraso, crea la multa aplicando la política vigente
     * y el descuento de la categoría del socio.
     * @return la multa generada, o null si se devolvió a tiempo
     */
    public Multa registrarDevolucion(String idMulta, LocalDate fecha, PoliticaMulta politica)
            throws OperacionInvalidaException {
        if (estaDevuelto()) {
            throw new OperacionInvalidaException("El préstamo " + id + " ya fue devuelto.");
        }
        if (fecha.isBefore(fechaPrestamo)) {
            throw new OperacionInvalidaException("La fecha de devolución no puede ser anterior al préstamo.");
        }
        this.fechaDevolucion = fecha;
        ejemplar.devolver();
        int atraso = getDiasAtraso(fecha);
        if (atraso > 0) {
            double bruto = politica.calcular(atraso);
            double neto = bruto * (1 - socio.getDescuentoMulta());
            this.multa = new Multa(idMulta, this, atraso, neto, fecha);
        }
        return multa;
    }

    /** Extiende el plazo una sola vez, siempre que el préstamo no esté vencido. */
    public void renovar(LocalDate hoy) throws OperacionInvalidaException {
        EstadoPrestamo estado = getEstado(hoy);
        if (estado == EstadoPrestamo.DEVUELTO) {
            throw new OperacionInvalidaException("No se puede renovar un préstamo ya devuelto.");
        }
        if (estado == EstadoPrestamo.VENCIDO) {
            throw new OperacionInvalidaException("El préstamo está vencido: debe devolverse, no renovarse.");
        }
        if (renovaciones >= MAX_RENOVACIONES) {
            throw new OperacionInvalidaException("El préstamo ya fue renovado " + MAX_RENOVACIONES + " vez.");
        }
        renovaciones++;
        fechaVencimiento = hoy.plusDays(calcularPlazo());
    }

    public EstadoPrestamo getEstado(LocalDate hoy) {
        if (estaDevuelto()) {
            return EstadoPrestamo.DEVUELTO;
        }
        return hoy.isAfter(fechaVencimiento) ? EstadoPrestamo.VENCIDO : EstadoPrestamo.ACTIVO;
    }

    /** Días de atraso respecto de una fecha (0 si no hay atraso). */
    public int getDiasAtraso(LocalDate referencia) {
        LocalDate fin = (fechaDevolucion != null) ? fechaDevolucion : referencia;
        long dias = ChronoUnit.DAYS.between(fechaVencimiento, fin);
        return (int) Math.max(0, dias);
    }

    public boolean estaDevuelto() {
        return fechaDevolucion != null;
    }

    @Override
    public String getId() {
        return id;
    }

    public Socio getSocio() {
        return socio;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public Bibliotecario getBibliotecario() {
        return bibliotecario;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public int getRenovaciones() {
        return renovaciones;
    }

    public Multa getMulta() {
        return multa;
    }

    @Override
    public String toString() {
        return id + " · " + ejemplar.getMaterial().getTitulo() + " → " + socio.getNombreCompleto();
    }
}
