package biblioteca.gui;

import biblioteca.modelo.EstadoPrestamo;
import biblioteca.modelo.Prestamo;
import biblioteca.servicio.Biblioteca;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// ¿QUÉ ES ESTE PANEL?
// La pantalla de inicio: 4 tarjetas con números (materiales, socios, préstamos, multas)
// y un gráfico de barras con el estado de los préstamos.
//
// "extends JPanel" = ES UN panel de Swing (un rectángulo donde se ponen componentes).
// "implements Actualizable" = promete tener actualizarDatos() para refrescar los números.
//
// Cómo se arma una pantalla en Swing (idea general):
//   1. Se crean paneles (JPanel) y se les elige un "layout" (cómo acomodan lo que tienen adentro).
//      BorderLayout -> 5 zonas: NORTH, SOUTH, EAST, WEST, CENTER
//      GridLayout   -> una grilla de celdas iguales
//   2. Se crean los componentes (JLabel = texto, JButton = botón, etc.).
//   3. Se agregan con panel.add(componente, zona).
public class PanelInicio extends JPanel implements Actualizable {

    private Biblioteca biblioteca;
    private TarjetaEstadistica tarjetaLibros;
    private TarjetaEstadistica tarjetaSocios;
    private TarjetaEstadistica tarjetaPrestamos;
    private TarjetaEstadistica tarjetaMultas;
    private GraficoBarras graficoPrestamos;

    public PanelInicio(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        setLayout(new BorderLayout(0, 20));
        setBackground(Estilo.COLOR_FONDO);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. TÍTULO SUPERIOR DE BIENVENIDA
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Panel de Control General");
        lblTitulo.setFont(Estilo.FUENTE_TITULO);
        lblTitulo.setForeground(Estilo.COLOR_TEXTO);
        
        JLabel lblSub = new JLabel("Resumen operativo del sistema de biblioteca en tiempo real.");
        lblSub.setFont(Estilo.FUENTE_NORMAL);
        lblSub.setForeground(Estilo.COLOR_NEUTRO);

        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        panelSuperior.add(lblSub, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        // 2. GRILLA DE 4 TARJETAS ESTADÍSTICAS SUPERIORES
        JPanel panelTarjetas = new JPanel(new GridLayout(1, 4, 15, 0));
        panelTarjetas.setOpaque(false);

        tarjetaLibros = new TarjetaEstadistica("Materiales", "0", "En el catálogo", Estilo.COLOR_PRIMARIO);
        tarjetaSocios = new TarjetaEstadistica("Socios", "0", "Registrados", Estilo.COLOR_EXITO);
        tarjetaPrestamos = new TarjetaEstadistica("Préstamos", "0", "Activos / Vencidos", Estilo.COLOR_ADVERTENCIA);
        tarjetaMultas = new TarjetaEstadistica("Multas", "$ 0", "Pendientes de pago", Estilo.COLOR_PELIGRO);

        panelTarjetas.add(tarjetaLibros);
        panelTarjetas.add(tarjetaSocios);
        panelTarjetas.add(tarjetaPrestamos);
        panelTarjetas.add(tarjetaMultas);

        // 3. PANEL CENTRAL (Contiene el gráfico de barras y un panel de accesos directos)
        JPanel panelCentral = new JPanel(new BorderLayout(20, 0));
        panelCentral.setOpaque(false);

        graficoPrestamos = new GraficoBarras("Estado Actual de Préstamos");
        panelCentral.add(graficoPrestamos, BorderLayout.WEST);

        // Panel de actividad reciente o bienvenida estética a la derecha
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(Estilo.COLOR_TARJETA);
        panelDerecho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.COLOR_BORDE, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblInfoTitulo = new JLabel("Bienvenido al Sistema");
        lblInfoTitulo.setFont(Estilo.FUENTE_SUBTITULO);
        lblInfoTitulo.setForeground(Estilo.COLOR_TEXTO);
        lblInfoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtInfo = new JTextArea(
                "Desde este sistema integral orientado a objetos podés administrar:\n\n" +
                "• Catálogo de Libros, Revistas y Tesis con portadas.\n" +
                "• Registro de Socios (Estudiantes, Docentes, General).\n" +
                "• Control de Préstamos, Devoluciones y Renovaciones.\n" +
                "• Gestión de Empleados y multas configurables.\n" +
                "• Opciones de respaldo, exportación CSV y reinicio total."
        );
        txtInfo.setFont(Estilo.FUENTE_NORMAL);
        txtInfo.setForeground(Estilo.COLOR_NEUTRO);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setOpaque(false);
        txtInfo.setEditable(false);
        txtInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelDerecho.add(lblInfoTitulo);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 10)));
        panelDerecho.add(txtInfo);

        panelCentral.add(panelDerecho, BorderLayout.CENTER);

        // Agregamos las secciones al panel principal
        JPanel panelContenido = new JPanel(new BorderLayout(0, 20));
        panelContenido.setOpaque(false);
        panelContenido.add(panelTarjetas, BorderLayout.NORTH);
        panelContenido.add(panelCentral, BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);

        // Cargamos los números iniciales
        actualizarDatos();
    }

    // Vuelve a contar todo en la Biblioteca y actualiza lo que se ve en pantalla.
    @Override
    public void actualizarDatos() {
        int totalMateriales = biblioteca.getCatalogo().size();
        int totalSocios = biblioteca.getSocios().size();

        // Contamos cuántos préstamos hay en cada estado
        int activos = 0;
        int vencidos = 0;
        for (Prestamo p : biblioteca.getPrestamos()) {
            if (p.getEstado() == EstadoPrestamo.ACTIVO) {
                activos++;
            }
            if (p.getEstado() == EstadoPrestamo.VENCIDO) {
                vencidos++;
            }
        }
        int totalPrestamosActivos = activos + vencidos;

        double totalMultas = biblioteca.calcularTotalMultasPendientes();

        // Actualizamos las tarjetas
        tarjetaLibros.actualizar(String.valueOf(totalMateriales), "Títulos totales");
        tarjetaSocios.actualizar(String.valueOf(totalSocios), "Activos habilitados");
        tarjetaPrestamos.actualizar(String.valueOf(totalPrestamosActivos), vencidos + " vencidos");
        tarjetaMultas.actualizar(Estilo.formatearMoneda(totalMultas), "Por cobrar");

        // Actualizamos el gráfico de barras
        graficoPrestamos.limpiarDatos();
        graficoPrestamos.agregarBarra("En Término / Activos", activos, Estilo.COLOR_EXITO);
        graficoPrestamos.agregarBarra("Vencidos", vencidos, Estilo.COLOR_PELIGRO);
        graficoPrestamos.agregarBarra("Devueltos", biblioteca.getPrestamos().size() - totalPrestamosActivos, Estilo.COLOR_NEUTRO);
    }
}