package biblioteca.modelo;

import java.io.Serializable;
import java.time.LocalDate;

// ¿QUÉ ES UNA MULTA?
// Se genera cuando un socio devuelve un ejemplar DESPUÉS de la fecha de vencimiento.
// El monto NO lo calcula esta clase: lo calcula la PoliticaMulta elegida (ver paquete "multas")
// y se lo pasamos ya calculado en el constructor.
public class Multa implements Serializable {

    // Contador compartido: la primera multa será la #1, la segunda la #2...
    private static int contadorMultas = 0;

    private int idMulta;
    private Prestamo prestamoAsociado; // El préstamo que se devolvió tarde
    private Socio socio;               // Quién debe pagarla
    private long diasAtraso;
    private double montoTotal;
    private LocalDate fechaGeneracion;
    private LocalDate fechaPago;       // null mientras no se pague
    private boolean pagada;

    public Multa(Prestamo prestamoAsociado, long diasAtraso, double montoTotal) {
        contadorMultas++;
        this.idMulta = contadorMultas;
        this.prestamoAsociado = prestamoAsociado;
        this.socio = prestamoAsociado.getSocio();
        this.diasAtraso = diasAtraso;
        this.montoTotal = montoTotal;
        this.fechaGeneracion = LocalDate.now();
        this.fechaPago = null;
        this.pagada = false;
    }

    public static void reiniciarContador() {
        contadorMultas = 0;
    }

    public static void actualizarContadorSiEsMayor(int idExistente) {
        if (idExistente > contadorMultas) {
            contadorMultas = idExistente;
        }
    }

    // Cuando tocás el botón "Registrar Pago" en el Panel de Multas:
    public void pagar() {
        this.pagada = true;
        this.fechaPago = LocalDate.now();
    }

    // Getters y Setters (incluye setMontoTotal por si el administrador quiere editar/perdonar parte de una multa)
    public int getIdMulta() { return idMulta; }
    public Prestamo getPrestamoAsociado() { return prestamoAsociado; }
    public Socio getSocio() { return socio; }
    public long getDiasAtraso() { return diasAtraso; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public LocalDate getFechaPago() { return fechaPago; }
    public boolean isPagada() { return pagada; }
}