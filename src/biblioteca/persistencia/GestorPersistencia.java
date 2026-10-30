package biblioteca.persistencia;

import biblioteca.modelo.MaterialBibliografico;
import biblioteca.modelo.Multa;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.servicio.Biblioteca;

import java.io.*;

// ¿QUÉ HACE ESTA CLASE?
// "Persistencia" = que los datos sigan existiendo después de cerrar el programa.
// Sin esto, todo lo que cargues se pierde al salir, porque los objetos viven en la memoria RAM.
//
// ¿CÓMO LO HACE? Con SERIALIZACIÓN:
//   - Guardar: ObjectOutputStream convierte el objeto Biblioteca (¡con TODO lo que tiene adentro:
//     listas, socios, libros, préstamos...!) en bytes y los escribe en un archivo.
//   - Cargar:  ObjectInputStream lee esos bytes y reconstruye los mismos objetos.
// Por eso todas las clases del modelo tienen "implements Serializable": si una no lo tuviera,
// Java no podría guardarla y daría error.
//
// Todos los métodos son "static": se usan sin crear un objeto, ej: GestorPersistencia.guardar(biblio);
public class GestorPersistencia {

    // Nombre del archivo donde se guardan los datos (queda en la carpeta desde donde se ejecuta)
    private static final String ARCHIVO_DATOS = "biblioteca_datos.dat";

    // Guarda todo el objeto Biblioteca en el disco.
    // "try ( ... )" se llama try-with-resources: cierra el archivo solo al terminar, aunque haya error.
    public static void guardar(Biblioteca biblioteca) {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DATOS))) {
            salida.writeObject(biblioteca);
        } catch (IOException e) {
            System.err.println("No se pudo guardar el archivo de datos: " + e.getMessage());
        }
    }

    // Carga la Biblioteca al abrir el programa. Si no existe el archivo, carga los Datos de Ejemplo.
    public static Biblioteca cargar() {
        File archivo = new File(ARCHIVO_DATOS);
        if (archivo.exists()) {
            try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
                // readObject() devuelve un Object genérico; con "(Biblioteca)" le decimos a Java
                // qué tipo de objeto es en realidad (esto se llama "casteo").
                Biblioteca biblioCargada = (Biblioteca) entrada.readObject();
                biblioCargada.sincronizarContadores(); // Asegura que los números automáticos sigan desde el último
                return biblioCargada;
            } catch (Exception e) {
                // Pasa, por ejemplo, si el archivo lo guardó una versión vieja del programa
                // cuyas clases tenían otros atributos.
                System.err.println("El archivo guardado era de una versión anterior. Se cargarán datos de ejemplo.");
            }
        }
        // Si es la primera vez que se abre o hubo error, creamos la biblioteca con datos de ejemplo
        Biblioteca nueva = DatosDeEjemplo.crearBibliotecaDeEjemplo();
        guardar(nueva);
        return nueva;
    }

    // --- FUNCIONES PARA TUS BOTONES DE LA BARRA SUPERIOR ---

    // 1. Reiniciar a los valores de ejemplo originales
    public static Biblioteca reiniciarConDatosDeEjemplo() {
        Biblioteca nueva = DatosDeEjemplo.crearBibliotecaDeEjemplo();
        guardar(nueva);
        return nueva;
    }

    // 2. Empezar de cero (borra todo y deja la biblioteca limpia para cargar datos propios)
    public static void reiniciarDesdeCero(Biblioteca bibliotecaActual) {
        bibliotecaActual.vaciarTodo();
        guardar(bibliotecaActual);
    }

    // --- EXPORTAR COMPROBANTES (.TXT) Y REPORTES PARA EXCEL (.CSV) ---
    // A diferencia de guardar(), esto escribe TEXTO que una persona puede leer
    // (o abrir en Excel). Un CSV es texto con columnas separadas por ";".

    // Guarda un texto (como el ticket de préstamo) en un archivo .txt elegido por el usuario
    public static void guardarTextoEnArchivo(File archivoDestino, String contenido) throws IOException {
        try (PrintWriter escritor = new PrintWriter(new OutputStreamWriter(new FileOutputStream(archivoDestino), "UTF-8"))) {
            escritor.print(contenido);
        }
    }

    // Genera un reporte completo en formato CSV (compatible con Microsoft Excel)
    public static void exportarReporteCSV(File archivoDestino, Biblioteca biblioteca) throws IOException {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(archivoDestino), "UTF-8"))) {
            pw.println("REPORTE GENERAL DEL SISTEMA DE BIBLIOTECA");
            pw.println();

            pw.println("--- CATALOGO DE MATERIALES ---");
            pw.println("ID;Tipo;Titulo;Autor;Anio;Genero;Ubicacion;Total Ejemplares;Disponibles");
            for (MaterialBibliografico m : biblioteca.getCatalogo()) {
                pw.println(m.getIdMaterial() + ";" + m.getTipoMaterial() + ";" + m.getTitulo() + ";"
                        + m.getAutor().getNombreCompleto() + ";" + m.getAnioPublicacion() + ";"
                        + m.getGenero() + ";" + m.getUbicacionEstanteria() + ";"
                        + m.getCantidadTotalEjemplares() + ";" + m.getCantidadDisponibles());
            }
            pw.println();

            pw.println("--- LISTADO DE SOCIOS ---");
            pw.println("Nro Socio;Tipo;Nombre;DNI;Telefono;Email;Prestamos Activos;Multas Pendientes");
            for (Socio s : biblioteca.getSocios()) {
                pw.println(s.getNroSocio() + ";" + s.getTipoSocio() + ";" + s.getNombre() + ";"
                        + s.getDni() + ";" + s.getTelefono() + ";" + s.getEmail() + ";"
                        + biblioteca.contarPrestamosActivosDeSocio(s) + ";"
                        + (biblioteca.tieneMultasPendientes(s) ? "SI" : "NO"));
            }
            pw.println();

            pw.println("--- HISTORIAL DE PRESTAMOS ---");
            pw.println("ID Prestamo;Socio;Material;Ejemplar;Atendido Por;Fecha Prestamo;Vencimiento;Estado");
            for (Prestamo p : biblioteca.getPrestamos()) {
                String emp = "Sistema";
                if (p.getEmpleadoResponsable() != null) {
                    emp = p.getEmpleadoResponsable().getNombre();
                }
                pw.println(p.getIdPrestamo() + ";" + p.getSocio().getNombre() + ";"
                        + p.getEjemplar().getMaterial().getTitulo() + ";" + p.getEjemplar().getCodigoInventario() + ";"
                        + emp + ";" + p.getFechaPrestamo() + ";" + p.getFechaVencimiento() + ";"
                        + p.getEstado().getDescripcion());
            }
            pw.println();

            pw.println("--- REGISTRO DE MULTAS ---");
            pw.println("ID Multa;Socio;Material;Dias Atraso;Monto;Estado");
            for (Multa mu : biblioteca.getMultas()) {
                pw.println(mu.getIdMulta() + ";" + mu.getSocio().getNombre() + ";"
                        + mu.getPrestamoAsociado().getEjemplar().getMaterial().getTitulo() + ";"
                        + mu.getDiasAtraso() + ";$" + mu.getMontoTotal() + ";"
                        + (mu.isPagada() ? "Pagada" : "Pendiente"));
            }
        }
    }
}