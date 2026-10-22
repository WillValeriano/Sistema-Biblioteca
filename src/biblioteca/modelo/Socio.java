package biblioteca.modelo;

import biblioteca.excepciones.OperacionInvalidaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Socio de la biblioteca. Es abstracta: cada categoría de socio define sus propias
 * condiciones (cantidad máxima de préstamos, días de plazo y descuento en multas).
 * Esto es polimorfismo: la biblioteca trata a todos como "Socio" y cada subclase responde distinto.
 */
public abstract class Socio extends Persona {

    private static final long serialVersionUID = 1L;

    private final String numeroSocio;
    private final LocalDate fechaAlta;
    private boolean activo;
    /** Asociación 1 a * con Prestamo: el historial completo del socio. */
    private final List<Prestamo> prestamos = new ArrayList<>();

    protected Socio(String numeroSocio, String dni, String nombre, String apellido,
                    String email, String telefono, LocalDate fechaAlta) {
        super(dni, nombre, apellido, email, telefono);
        this.numeroSocio = numeroSocio;
        this.fechaAlta = fechaAlta;
        this.activo = true;
    }

    // ---- Comportamiento que cada categoría redefine ----

    public abstract int getMaxPrestamos();

    public abstract int getDiasPrestamo();

    /** Porcentaje de descuento sobre las multas (0.0 a 1.0). */
    public abstract double getDescuentoMulta();

    public abstract String getTipo();

    // ---- Reglas de negocio comunes ----

    /**
     * Verifica si el socio puede retirar un nuevo material.
     * @param hoy fecha actual del sistema (para detectar préstamos vencidos)
     */
    public void verificarHabilitado(LocalDate hoy) throws OperacionInvalidaException {
        if (!activo) {
            throw new OperacionInvalidaException("El socio " + getNombreCompleto() + " está dado de baja.");
        }
        if (tieneMultasPendientes()) {
            throw new OperacionInvalidaException("El socio tiene multas impagas por "
                    + Formato.moneda(getDeudaPendiente()) + ". Debe abonarlas antes de retirar material.");
        }
        if (tienePrestamosVencidos(hoy)) {
            throw new OperacionInvalidaException("El socio tiene préstamos vencidos sin devolver.");
        }
        if (getPrestamosActivos().size() >= getMaxPrestamos()) {
            throw new OperacionInvalidaException("El socio alcanzó su límite de " + getMaxPrestamos()
                    + " préstamos simultáneos (" + getTipo() + ").");
        }
    }

    /** Devuelve "Habilitado", "Suspendido" o "Baja" según el estado actual. */
    public String getEstado(LocalDate hoy) {
        if (!activo) {
            return "Baja";
        }
        if (tieneMultasPendientes() || tienePrestamosVencidos(hoy)) {
            return "Suspendido";
        }
        return "Habilitado";
    }

    public List<Prestamo> getPrestamosActivos() {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo p : prestamos) {
            if (!p.estaDevuelto()) {
                activos.add(p);
            }
        }
        return activos;
    }

    public boolean tienePrestamosVencidos(LocalDate hoy) {
        for (Prestamo p : prestamos) {
            if (p.getEstado(hoy) == EstadoPrestamo.VENCIDO) {
                return true;
            }
        }
        return false;
    }

    public boolean tieneMultasPendientes() {
        return getDeudaPendiente() > 0;
    }

    public double getDeudaPendiente() {
        double total = 0;
        for (Prestamo p : prestamos) {
            Multa m = p.getMulta();
            if (m != null && !m.estaPagada()) {
                total += m.getMonto();
            }
        }
        return total;
    }

    void agregarPrestamo(Prestamo prestamo) {
        prestamos.add(prestamo);
    }

    public List<Prestamo> getHistorial() {
        return Collections.unmodifiableList(prestamos);
    }

    @Override
    public String getId() {
        return numeroSocio;
    }

    @Override
    public String getRol() {
        return "Socio " + getTipo();
    }

    public String getNumeroSocio() {
        return numeroSocio;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return numeroSocio + " - " + getNombreCompleto() + " (" + getTipo() + ")";
    }
}
