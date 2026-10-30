// 1. La primera línea SIEMPRE dice en qué carpeta (paquete) está guardado el archivo:
package biblioteca.modelo;

import java.io.Serializable;

// "Serializable" es un permiso nativo de Java que le dice a la computadora:
// "Permití que este objeto se pueda guardar en un archivo en el disco duro 
// para que no se borre cuando cierro el programa".
public abstract class Persona implements Serializable {
    
    // Atributos protegidos (Encapsulamiento): 
    // Son privados para el exterior, pero visibles para las clases hijas (Socio y Empleado).
    protected String dni;
    protected String nombre;
    protected String telefono;
    protected String email;

    // Constructor: Se ejecuta cuando creamos una Persona nueva.
    public Persona(String dni, String nombre, String telefono, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    // Método abstracto (Polimorfismo): 
    // Obliga a que cada clase hija diga qué rol cumple en la biblioteca ("Empleado", "Socio Estudiante", etc.)
    public abstract String getRol();

    // Getters y Setters: Métodos para leer (get) y editar (set) los datos desde tu pantalla de edición.
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}