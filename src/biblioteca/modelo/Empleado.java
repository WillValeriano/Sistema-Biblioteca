package biblioteca.modelo;

// "extends Persona" significa que Empleado HEREDA dni, nombre, telefono y email de Persona.
public class Empleado extends Persona {

    // STATIC: Esta variable es el contador automático compartido por todos los empleados.
    // Arranca en 100, así el primer empleado será el Legajo 101, el segundo el 102, etc.
    private static int contadorLegajos = 100;
    
    // Atributos exclusivos de un empleado de la biblioteca:
    private int legajo;
    private String cargo; // Ej: "Bibliotecario", "Administrador", "Atención al Público"
    private String turno; // Ej: "Mañana", "Tarde", "Noche"

    // CONSTRUCTOR 1 (AUTOMÁTICO): 
    // Fijate que acá NO te pide el legajo entre los paréntesis. 
    // Lo usamos cuando vos registrás un empleado nuevo desde la ventana de la aplicación.
    public Empleado(String dni, String nombre, String telefono, String email, String cargo, String turno) {
        super(dni, nombre, telefono, email);
        contadorLegajos++; // Suma 1 al contador general (101, 102, 103...)
        this.legajo = contadorLegajos; // Se lo asigna automáticamente a este empleado
        this.cargo = cargo;
        this.turno = turno;
    }

    // Método para reiniciar el contador a 100 cuando toques el botón "Empezar desde cero" arriba
    public static void reiniciarContador() {
        contadorLegajos = 100;
    }
    
    // Método para actualizar el contador cuando cargamos empleados guardados en el disco
    public static void actualizarContadorSiEsMayor(int legajoExistente) {
        if (legajoExistente > contadorLegajos) {
            contadorLegajos = legajoExistente;
        }
    }

    // Cumplimos con el método abstracto de Persona:
    @Override
    public String getRol() {
        return "Empleado (" + cargo + ")";
    }

    // Getters y Setters para poder ver y editar al empleado en la tabla de la aplicación:
    // (no hay un setLegajo porque el legajo es automático y no se edita)
    public int getLegajo() { return legajo; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    @Override
    public String toString() {
        return "Legajo #" + legajo + " - " + nombre + " [" + cargo + "]";
    }
}