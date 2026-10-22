package biblioteca.persistencia;

import biblioteca.servicio.Biblioteca;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Guarda y recupera el estado completo de la biblioteca usando serialización de objetos de Java.
 * Se escribe primero en un archivo temporal y después se reemplaza el original,
 * así un corte a mitad de escritura no corrompe los datos.
 */
public class GestorPersistencia {

    private final Path archivo;

    public GestorPersistencia(Path archivo) {
        this.archivo = archivo;
    }

    /** Archivo por defecto: biblioteca.dat en la carpeta desde donde se ejecuta el programa. */
    public static GestorPersistencia porDefecto() {
        return new GestorPersistencia(Paths.get("biblioteca.dat"));
    }

    public boolean existeArchivo() {
        return Files.exists(archivo);
    }

    public void guardar(Biblioteca biblioteca) throws IOException {
        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        try (OutputStream out = Files.newOutputStream(temporal);
             ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(biblioteca);
        }
        Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
    }

    public Biblioteca cargar() throws IOException {
        try (InputStream in = Files.newInputStream(archivo);
             ObjectInputStream ois = new ObjectInputStream(in)) {
            return (Biblioteca) ois.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("El archivo de datos no es compatible: " + e.getMessage(), e);
        }
    }

    public Path getArchivo() {
        return archivo;
    }
}
