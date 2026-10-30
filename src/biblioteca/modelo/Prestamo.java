package biblioteca.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Asocia un Socio, un Ejemplar físico y el Empleado que registró la operación
public class Prestamo implements Serializable {

    private static int contadorPrestamos = 1;

    private int idPrestamo;
    private Socio socio;
    private Ejemplar ejemplar;
    private Empleado empleadoResponsable; // NUEVO: registra qué empleado hizo el préstamo
    private LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucionReal; // Queda en null hasta que el socio devuelve el libro
    private int cantidadRenovaciones;      // NUEVO: cuenta cuántas veces se renovó

    public Prestamo(Socio socio, Ejemplar ejemplar, Empleado empleadoResponsable, int diasPrestamo) {
        this.idPrestamo = contadorPrestamos++;
        this.socio = socio;
        this.ejemplar = ejemplar;
        this.empleadoResponsable = empleadoResponsable;
        this.fechaPrestamo = LocalDate.now(); // Toma la fecha de hoy automáticamente
        this.fechaVencimiento = this.fechaPrestamo.plusDays(diasPrestamo); // Suma los días según el tipo de socio
        this.fechaDevolucionReal = null;
        this.cantidadRenovaciones = 0;

        // Cambia el estado del ejemplar a PRESTADO
        this.ejemplar.prestar();
    }

    // Constructor adicional para poder cargar préstamos con fechas pasadas en los Datos de Ejemplo
    public Prestamo(Socio socio, Ejemplar ejemplar, Empleado empleadoResponsable, 
                    LocalDate fechaPrestamo, LocalDate fechaVencimiento) {
        this.idPrestamo = contadorPrestamos++;
        this.socio = socio;
        this.ejemplar = ejemplar;
        this.empleadoResponsable = empleadoResponsable;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaDevolucionReal = null;
        this.cantidadRenovaciones = 0;
        this.ejemplar.prestar();
    }

    public static void reiniciarContador() {
        contadorPrestamos = 1;
    }

    public static void actualizarContadorSiEsMayor(int idExistente) {
        if (idExistente > contadorPrestamos) {
            contadorPrestamos = idExistente;
        }
    }

    // Calcula en tiempo real si está ACTIVO, DEVUELTO o VENCIDO
    public EstadoPrestamo getEstado() {
        if (fechaDevolucionReal != null) {
            return EstadoPrestamo.DEVUELTO;
        }
        if (LocalDate.now().isAfter(fechaVencimiento)) {
            return EstadoPrestamo.VENCIDO;
        }
        return EstadoPrestamo.ACTIVO;
    }

    // Registra la devolución del ejemplar hoy
    public void registrarDevolucion() {
        this.fechaDevolucionReal = LocalDate.now();
        this.ejemplar.devolver(); // Libera la copia física a DISPONIBLE
    }

    // NUEVO: Permite renovar el préstamo sumando más días al vencimiento
    public void renovarPrestamo(int diasExtra) {
        this.fechaVencimiento = this.fechaVencimiento.plusDays(diasExtra);
        this.cantidadRenovaciones++;
    }

    // NUEVO: Para tu defensa oral, permite simular que el préstamo venció hace "X" días
    public void simularAtraso(int diasAtraso) {
        this.fechaPrestamo = LocalDate.now().minusDays(diasAtraso + 7);
        this.fechaVencimiento = LocalDate.now().minusDays(diasAtraso);
    }

    // Calcula cuántos días de atraso lleva (0 si está en término)
    public long getDiasAtraso() {
        LocalDate fechaReferencia = (fechaDevolucionReal != null) ? fechaDevolucionReal : LocalDate.now();
        if (fechaReferencia.isAfter(fechaVencimiento)) {
            return ChronoUnit.DAYS.between(fechaVencimiento, fechaReferencia);
        }
        return 0;
    }

    // Getters
    public int getIdPrestamo() { return idPrestamo; }
    public Socio getSocio() { return socio; }
    public Ejemplar getEjemplar() { return ejemplar; }
    public Empleado getEmpleadoResponsable() { return empleadoResponsable; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public int getCantidadRenovaciones() { return cantidadRenovaciones; }
}