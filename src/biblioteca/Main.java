package biblioteca;

import biblioteca.gui.Estilo;
import biblioteca.gui.Mensajes;
import biblioteca.gui.VentanaPrincipal;
import biblioteca.persistencia.DatosDeEjemplo;
import biblioteca.persistencia.GestorPersistencia;
import biblioteca.servicio.Biblioteca;

import javax.swing.SwingUtilities;
import java.io.IOException;

/**
 * Punto de entrada. Carga los datos guardados (biblioteca.dat) o, si no existen,
 * crea la biblioteca con datos de ejemplo. Toda la interfaz se construye en el
 * hilo de eventos de Swing (Event Dispatch Thread).
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Estilo.instalar();
            GestorPersistencia persistencia = GestorPersistencia.porDefecto();
            Biblioteca biblioteca;
            if (persistencia.existeArchivo()) {
                try {
                    biblioteca = persistencia.cargar();
                } catch (IOException e) {
                    Mensajes.error(null, "No se pudieron leer los datos guardados (" + e.getMessage()
                            + ").\nSe cargarán los datos de ejemplo.");
                    biblioteca = DatosDeEjemplo.crear();
                }
            } else {
                biblioteca = DatosDeEjemplo.crear();
            }
            VentanaPrincipal.abrir(biblioteca, persistencia);
        });
    }
}
