package biblioteca.servicio;

/**
 * Observador de la biblioteca (patrón Observer). La interfaz gráfica y el autoguardado
 * se suscriben para enterarse de cada cambio sin que la lógica de negocio conozca a Swing.
 */
@FunctionalInterface
public interface BibliotecaListener {

    void bibliotecaCambio(String descripcionDelCambio);
}
