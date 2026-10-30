package biblioteca;

import biblioteca.gui.Estilo;
import biblioteca.gui.PanelInicio;
import biblioteca.persistencia.GestorPersistencia;
import biblioteca.servicio.Biblioteca;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// PUNTO DE ENTRADA DEL PROGRAMA.
// Cuando ejecutás el proyecto, Java busca el método "main" y empieza por acá.
//
// Qué hace, en orden:
//   1. Carga la Biblioteca desde el disco (o crea una con datos de ejemplo si es la primera vez).
//   2. Crea la ventana (JFrame) y le pone adentro el Panel de Inicio.
//   3. Cuando cerrás la ventana, guarda todo en el disco.
public class Main {

    public static void main(String[] args) {
        // Swing pide que las ventanas se creen en su propio "hilo" de dibujo.
        // invokeLater(...) ejecuta el código que le pasamos en ese hilo.
        SwingUtilities.invokeLater(() -> {
            Biblioteca biblioteca = GestorPersistencia.cargar();

            JFrame ventana = new JFrame("Sistema de Biblioteca");
            ventana.setSize(1000, 620);
            ventana.setLocationRelativeTo(null); // null = centrada en la pantalla
            ventana.getContentPane().setBackground(Estilo.COLOR_FONDO);
            ventana.add(new PanelInicio(biblioteca));

            // Al cerrar la ventana: primero guardamos, después terminamos el programa
            ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            ventana.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    GestorPersistencia.guardar(biblioteca);
                    System.exit(0);
                }
            });

            ventana.setVisible(true);
        });
    }
}
