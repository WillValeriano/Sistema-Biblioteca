package biblioteca.gui;

import biblioteca.excepciones.OperacionInvalidaException;

import javax.swing.JOptionPane;
import java.awt.Component;

/** Cuadros de diálogo estándar y ejecución segura de operaciones de negocio. */
public final class Mensajes {

    private Mensajes() {
    }

    /** Operación de negocio que puede ser rechazada. */
    @FunctionalInterface
    public interface Operacion {
        void ejecutar() throws OperacionInvalidaException;
    }

    /**
     * Ejecuta la operación y, si la biblioteca la rechaza, muestra el motivo al usuario.
     * @return true si se ejecutó correctamente
     */
    public static boolean intentar(Component padre, Operacion operacion) {
        try {
            operacion.ejecutar();
            return true;
        } catch (OperacionInvalidaException e) {
            error(padre, e.getMessage());
            return false;
        }
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operación no permitida", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component padre, String titulo, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component padre, String titulo, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, titulo, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
