package biblioteca.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Guardará todas las reglas editables de la biblioteca
public class ConfiguracionBiblioteca implements Serializable {

    // 1. Reglas de Multas (usamos "double" porque el dinero puede tener centavos/decimales)
    private double multaPorDia;
    private double topeMaximoMulta;

    // 2. Beneficios de Socio Estudiante
    private int diasPrestamoEstudiante;
    private int maxLibrosEstudiante;

    // 3. Beneficios de Socio Docente
    private int diasPrestamoDocente;
    private int maxLibrosDocente;

    // 4. Beneficios de Socio General
    private int diasPrestamoGeneral;
    private int maxLibrosGeneral;

    // 5. Listas para las "ventanitas con flechita" (JComboBox) que vos podés ampliar en la app
    private List<String> generosLibros;
    private List<String> cargosEmpleado;
    private List<String> turnosEmpleado;

    // Constructor: Carga los valores iniciales por defecto
    public ConfiguracionBiblioteca() {
        this.multaPorDia = 500.0;       // $500 por cada día de atraso
        this.topeMaximoMulta = 15000.0; // Multa máxima de $15.000

        this.diasPrestamoEstudiante = 7;
        this.maxLibrosEstudiante = 3;

        this.diasPrestamoDocente = 15;
        this.maxLibrosDocente = 5;

        this.diasPrestamoGeneral = 5;
        this.maxLibrosGeneral = 2;

        // Inicializamos las listas dinámicas (ArrayList)
        this.generosLibros = new ArrayList<>();
        generosLibros.add("Novela");
        generosLibros.add("Ciencia Ficción");
        generosLibros.add("Informática / Programación");
        generosLibros.add("Historia");
        generosLibros.add("Ciencias Exactas");
        generosLibros.add("Fantasía");

        this.cargosEmpleado = new ArrayList<>();
        cargosEmpleado.add("Bibliotecario");
        cargosEmpleado.add("Administrador");
        cargosEmpleado.add("Atención al Público");
        cargosEmpleado.add("Catalogador");

        this.turnosEmpleado = new ArrayList<>();
        turnosEmpleado.add("Mañana");
        turnosEmpleado.add("Tarde");
        turnosEmpleado.add("Noche");
    }

    // Método para agregar un género nuevo desde tu pantalla de Administración
    public void agregarGenero(String nuevoGenero) {
        if (nuevoGenero != null && !nuevoGenero.trim().isEmpty() && !generosLibros.contains(nuevoGenero.trim())) {
            generosLibros.add(nuevoGenero.trim());
        }
    }

    // Método para borrar un género si te equivocaste
    public void eliminarGenero(String genero) {
        generosLibros.remove(genero);
    }

    // Método para agregar un nuevo cargo de empleado
    public void agregarCargo(String nuevoCargo) {
        if (nuevoCargo != null && !nuevoCargo.trim().isEmpty() && !cargosEmpleado.contains(nuevoCargo.trim())) {
            cargosEmpleado.add(nuevoCargo.trim());
        }
    }

    // --- GETTERS Y SETTERS (Permiten leer y editar todos los valores en la pantalla de Administración) ---
    public double getMultaPorDia() { return multaPorDia; }
    public void setMultaPorDia(double multaPorDia) { this.multaPorDia = multaPorDia; }

    public double getTopeMaximoMulta() { return topeMaximoMulta; }
    public void setTopeMaximoMulta(double topeMaximoMulta) { this.topeMaximoMulta = topeMaximoMulta; }

    public int getDiasPrestamoEstudiante() { return diasPrestamoEstudiante; }
    public void setDiasPrestamoEstudiante(int dias) { this.diasPrestamoEstudiante = dias; }

    public int getMaxLibrosEstudiante() { return maxLibrosEstudiante; }
    public void setMaxLibrosEstudiante(int max) { this.maxLibrosEstudiante = max; }

    public int getDiasPrestamoDocente() { return diasPrestamoDocente; }
    public void setDiasPrestamoDocente(int dias) { this.diasPrestamoDocente = dias; }

    public int getMaxLibrosDocente() { return maxLibrosDocente; }
    public void setMaxLibrosDocente(int max) { this.maxLibrosDocente = max; }

    public int getDiasPrestamoGeneral() { return diasPrestamoGeneral; }
    public void setDiasPrestamoGeneral(int dias) { this.diasPrestamoGeneral = dias; }

    public int getMaxLibrosGeneral() { return maxLibrosGeneral; }
    public void setMaxLibrosGeneral(int max) { this.maxLibrosGeneral = max; }

    public List<String> getGenerosLibros() { return generosLibros; }
    public List<String> getCargosEmpleado() { return cargosEmpleado; }
    public List<String> getTurnosEmpleado() { return turnosEmpleado; }
}