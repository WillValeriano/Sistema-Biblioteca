package biblioteca.gui;

// Interfaz que implementan los paneles de la ventana para refrescar sus tablas y gráficos.
// Cuando algo cambia (ej: se registró un préstamo), la ventana llama a actualizarDatos()
// en cada panel, sin importar de qué tipo sea cada uno.
public interface Actualizable {
    void actualizarDatos();
}