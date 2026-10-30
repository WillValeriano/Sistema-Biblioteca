package biblioteca.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// ¿QUÉ ES UN PRÉSTAMO?
// Es el registro de que un Socio se llevó un Ejemplar (una copia física),
// quién lo atendió (Empleado) y las fechas: cuándo se lo llevó, cuándo vence y cuándo lo devolvió.
//
// Fijate que NO guarda el estado (Activo/Vencido/Devuelto) en una variable:
// lo CALCULA cada vez en getEstado() a partir de las fechas. Así nunca queda desactualizado.
public class Prestamo implements Serializable {

    // Contador compartido: el primer préstamo será el #1, el segundo el #2...
    private static int contadorPrestamos = 0;

    private int idPrestamo;
    private Socio socio;
    private Ejemplar ejemplar;
    private Empleado empleadoResponsable;  // Qué empleado registró el préstamo
    private LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucionReal; // Queda en null hasta que el socio devuelve el libro
    private int cantidadRenovaciones;      // Cuántas veces se extendió el plazo

    // CONSTRUCTOR 1 (el que se usa normalmente): el préstamo empieza HOY
    // y vence dentro de "diasPrestamo" días.
    // "this(...)" llama al CONSTRUCTOR 2 de abajo, así no repetimos el mismo código dos veces.
    public Prestamo(Socio socio, Ejemplar ejemplar, Empleado empleadoResponsable, int diasPrestamo) {
        this(socio, ejemplar, empleadoResponsable, LocalDate.now(), LocalDate.now().plusDays(diasPrestamo));
    }

    // CONSTRUCTOR 2: permite elegir las fechas a mano.
    // Lo usan los Datos de Ejemplo para crear préstamos "del pasado" (vencidos, devueltos, etc.).
    public Prestamo(Socio socio, Ejemplar ejemplar, Empleado empleadoResponsable,
                    LocalDate fechaPrestamo, LocalDate fechaVencimiento) {
        contadorPrestamos++;
        this.idPrestamo = contadorPrestamos;
        this.socio = socio;
        this.ejemplar = ejemplar;
        this.empleadoResponsable = empleadoResponsable;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaDevolucionReal = null;
        this.cantidadRenovaciones = 0;

        // La copia física pasa a estado PRESTADO
        this.ejemplar.prestar();
    }

    public static void reiniciarContador() {
        contadorPrestamos = 0;
    }

    public static void actualizarContadorSiEsMayor(int idExistente) {
        if (idExistente > contadorPrestamos) {
            contadorPrestamos = idExistente;
        }
    }

    // Calcula en el momento si está ACTIVO, DEVUELTO o VENCIDO:
    //  - Si tiene fecha de devolución  -> DEVUELTO
    //  - Si hoy ya pasó el vencimiento -> VENCIDO
    //  - Si no                         -> ACTIVO
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

    // Renueva el préstamo sumando más días al vencimiento
    public void renovarPrestamo(int diasExtra) {
        this.fechaVencimiento = this.fechaVencimiento.plusDays(diasExtra);
        this.cantidadRenovaciones++;
    }

    // Para la defensa oral: simula que el préstamo venció hace "diasAtraso" días
    public void simularAtraso(int diasAtraso) {
        this.fechaPrestamo = LocalDate.now().minusDays(diasAtraso + 7);
        this.fechaVencimiento = LocalDate.now().minusDays(diasAtraso);
    }

    // Calcula cuántos días de atraso lleva (0 si está en término).
    // Si ya lo devolvió, cuenta hasta el día de la devolución; si no, hasta hoy.
    public long getDiasAtraso() {
        LocalDate fechaReferencia;
        if (fechaDevolucionReal != null) {
            fechaReferencia = fechaDevolucionReal;
        } else {
            fechaReferencia = LocalDate.now();
        }

        if (fechaReferencia.isAfter(fechaVencimiento)) {
            // ChronoUnit.DAYS.between(a, b) = cuántos días hay entre la fecha a y la fecha b
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