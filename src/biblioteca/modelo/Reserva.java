package biblioteca.modelo;

import java.io.Serializable;
import java.time.LocalDate;

// ¿QUÉ ES UNA RESERVA?
// Cuando un socio quiere un material pero no queda ninguna copia disponible,
// se anota en una "lista de espera". Cuando se le presta el material, la reserva se marca como cumplida.
public class Reserva implements Serializable {

    // Contador compartido: la primera reserva será la #1, la segunda la #2...
    private static int contadorReservas = 0;

    private int idReserva;
    private Socio socio;
    private MaterialBibliografico material; // Se reserva la OBRA, no una copia en particular
    private LocalDate fechaReserva;
    private boolean activa; // Pasa a false cuando ya se le entregó el libro

    public Reserva(Socio socio, MaterialBibliografico material) {
        contadorReservas++;
        this.idReserva = contadorReservas;
        this.socio = socio;
        this.material = material;
        this.fechaReserva = LocalDate.now();
        this.activa = true;
    }

    public static void reiniciarContador() {
        contadorReservas = 0;
    }

    public static void actualizarContadorSiEsMayor(int idExistente) {
        if (idExistente > contadorReservas) {
            contadorReservas = idExistente;
        }
    }

    // Devuelve true si esta reserva sigue pendiente y es de ESE socio para ESE material
    public boolean esActivaDe(Socio otroSocio, MaterialBibliografico otroMaterial) {
        return activa
                && socio.getNroSocio() == otroSocio.getNroSocio()
                && material.getIdMaterial() == otroMaterial.getIdMaterial();
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