package biblioteca.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Reserva implements Serializable {

    private static int contadorReservas = 1;

    private int idReserva;
    private Socio socio;
    private MaterialBibliografico material;
    private LocalDate fechaReserva;
    private boolean activa; // Pasa a false cuando ya se le entregó el libro o se canceló

    public Reserva(Socio socio, MaterialBibliografico material) {
        this.idReserva = contadorReservas++;
        this.socio = socio;
        this.material = material;
        this.fechaReserva = LocalDate.now();
        this.activa = true;
    }

    public static void reiniciarContador() {
        contadorReservas = 1;
    }

    public void marcarComoCumplida() {
        this.activa = false;
    }

    public int getIdReserva() { return idReserva; }
    public Socio getSocio() { return socio; }
    public MaterialBibliografico getMaterial() { return material; }
    public LocalDate getFechaReserva() { return fechaReserva; }
    public boolean isActiva() { return activa; }
}