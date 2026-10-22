package biblioteca.servicio;

import biblioteca.excepciones.OperacionInvalidaException;
import biblioteca.modelo.Autor;
import biblioteca.modelo.Bibliotecario;
import biblioteca.modelo.Buscable;
import biblioteca.modelo.Ejemplar;
import biblioteca.modelo.EstadoPrestamo;
import biblioteca.modelo.Libro;
import biblioteca.modelo.MaterialBibliografico;
import biblioteca.modelo.Multa;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Revista;
import biblioteca.modelo.Socio;
import biblioteca.modelo.SocioDocente;
import biblioteca.modelo.SocioEstudiante;
import biblioteca.modelo.SocioGeneral;
import biblioteca.multas.MultaFijaDiaria;
import biblioteca.multas.PoliticaMulta;
import biblioteca.persistencia.Repositorio;
import biblioteca.persistencia.RepositorioMemoria;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Fachada del sistema (patrón Facade): punto único de entrada para la interfaz gráfica.
 * Coordina repositorios, aplica las reglas de negocio y avisa a los observadores de cada cambio.
 *
 * Compone sus repositorios: se crean y mueren con la biblioteca.
 */
public class Biblioteca implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final Repositorio<MaterialBibliografico> materiales = new RepositorioMemoria<>();
    private final Repositorio<Socio> socios = new RepositorioMemoria<>();
    private final Repositorio<Prestamo> prestamos = new RepositorioMemoria<>();
    private final Repositorio<Autor> autores = new RepositorioMemoria<>();
    private final Repositorio<Bibliotecario> bibliotecarios = new RepositorioMemoria<>();

    private PoliticaMulta politicaMulta = new MultaFijaDiaria(500);
    private Bibliotecario bibliotecarioActual;
    /** Permite "simular" el paso del tiempo durante la demostración. */
    private int desplazamientoDias;

    private int contadorLibros;
    private int contadorRevistas;
    private int contadorSocios;
    private int contadorPrestamos;
    private int contadorMultas;
    private int contadorAutores;

    private transient List<BibliotecaListener> listeners;

    public Biblioteca(String nombre) {
        this.nombre = nombre;
    }

    // =====================================================================
    // Observadores
    // =====================================================================

    private List<BibliotecaListener> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        return listeners;
    }

    public void agregarListener(BibliotecaListener listener) {
        getListeners().add(listener);
    }

    private void notificar(String cambio) {
        for (BibliotecaListener l : new ArrayList<>(getListeners())) {
            l.bibliotecaCambio(cambio);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        listeners = new ArrayList<>();
    }

    // =====================================================================
    // Fecha del sistema
    // =====================================================================

    public LocalDate hoy() {
        return LocalDate.now().plusDays(desplazamientoDias);
    }

    public void avanzarDias(int dias) {
        desplazamientoDias += dias;
        notificar("Fecha del sistema: " + biblioteca.modelo.Formato.fecha(hoy()));
    }

    public void volverAHoy() {
        desplazamientoDias = 0;
        notificar("Fecha del sistema restablecida a hoy");
    }

    public int getDesplazamientoDias() {
        return desplazamientoDias;
    }

    // =====================================================================
    // Bibliotecarios y política de multas
    // =====================================================================

    public void registrarBibliotecario(Bibliotecario b) {
        bibliotecarios.guardar(b);
        if (bibliotecarioActual == null) {
            bibliotecarioActual = b;
        }
    }

    public List<Bibliotecario> listarBibliotecarios() {
        return bibliotecarios.listar();
    }

    public Bibliotecario getBibliotecarioActual() {
        return bibliotecarioActual;
    }

    public void setBibliotecarioActual(Bibliotecario b) {
        this.bibliotecarioActual = b;
        notificar("Atiende: " + b.getNombre() + " " + b.getApellido());
    }

    public PoliticaMulta getPoliticaMulta() {
        return politicaMulta;
    }

    public void setPoliticaMulta(PoliticaMulta politica) {
        this.politicaMulta = politica;
        notificar("Política de multas: " + politica.getNombre() + " (" + politica.getDescripcion() + ")");
    }

    // =====================================================================
    // Catálogo
    // =====================================================================

    public Libro registrarLibro(String titulo, int anio, String categoria, String isbn, String editorial,
                                String autoresSeparadosPorComa, int cantidadEjemplares, String ubicacion)
            throws OperacionInvalidaException {
        validarMaterial(titulo, anio, cantidadEjemplares);
        List<Autor> listaAutores = new ArrayList<>();
        for (String nombreAutor : autoresSeparadosPorComa.split(",")) {
            if (!nombreAutor.isBlank()) {
                listaAutores.add(obtenerOCrearAutor(nombreAutor.trim()));
            }
        }
        if (listaAutores.isEmpty()) {
            throw new OperacionInvalidaException("El libro debe tener al menos un autor.");
        }
        String codigo = String.format("L-%03d", ++contadorLibros);
        Libro libro = new Libro(codigo, titulo.trim(), anio, categoria.trim(), isbn.trim(), editorial.trim(), listaAutores);
        crearEjemplares(libro, cantidadEjemplares, ubicacion);
        materiales.guardar(libro);
        notificar("Libro registrado: " + libro.getTitulo());
        return libro;
    }

    public Revista registrarRevista(String titulo, int anio, String categoria, String issn, int numero,
                                    String periodicidad, int cantidadEjemplares, String ubicacion)
            throws OperacionInvalidaException {
        validarMaterial(titulo, anio, cantidadEjemplares);
        if (numero <= 0) {
            throw new OperacionInvalidaException("El número de la revista debe ser positivo.");
        }
        String codigo = String.format("R-%03d", ++contadorRevistas);
        Revista revista = new Revista(codigo, titulo.trim(), anio, categoria.trim(), issn.trim(), numero, periodicidad);
        crearEjemplares(revista, cantidadEjemplares, ubicacion);
        materiales.guardar(revista);
        notificar("Revista registrada: " + revista.getTitulo());
        return revista;
    }

    private void validarMaterial(String titulo, int anio, int ejemplares) throws OperacionInvalidaException {
        if (titulo == null || titulo.isBlank()) {
            throw new OperacionInvalidaException("El título es obligatorio.");
        }
        if (anio < 1450 || anio > hoy().getYear() + 1) {
            throw new OperacionInvalidaException("El año de publicación no es válido.");
        }
        if (ejemplares < 1 || ejemplares > 50) {
            throw new OperacionInvalidaException("La cantidad de ejemplares debe estar entre 1 y 50.");
        }
    }

    private void crearEjemplares(MaterialBibliografico material, int cantidad, String ubicacion) {
        for (int i = 0; i < cantidad; i++) {
            material.agregarEjemplar(ubicacion);
        }
    }

    public Ejemplar agregarEjemplar(MaterialBibliografico material, String ubicacion) {
        Ejemplar e = material.agregarEjemplar(ubicacion);
        notificar("Nuevo ejemplar " + e.getCodigo() + " de " + material.getTitulo());
        return e;
    }

    public void cambiarReparacion(Ejemplar ejemplar) throws OperacionInvalidaException {
        ejemplar.cambiarReparacion();
        notificar("Ejemplar " + ejemplar.getCodigo() + ": " + ejemplar.getEstado());
    }

    public void eliminarMaterial(MaterialBibliografico material) throws OperacionInvalidaException {
        if (material.tieneEjemplaresPrestados()) {
            throw new OperacionInvalidaException("No se puede eliminar: hay ejemplares de \""
                    + material.getTitulo() + "\" prestados.");
        }
        boolean figuraEnHistorial = !prestamos.filtrar(p -> p.getEjemplar().getMaterial() == material).isEmpty();
        if (figuraEnHistorial) {
            throw new OperacionInvalidaException("No se puede eliminar: el material figura en el historial de préstamos.");
        }
        materiales.eliminar(material.getId());
        notificar("Material eliminado: " + material.getTitulo());
    }

    public Autor obtenerOCrearAutor(String nombreAutor) {
        String buscado = Buscable.normalizar(nombreAutor);
        for (Autor a : autores.listar()) {
            if (Buscable.normalizar(a.getNombre()).equals(buscado)) {
                return a;
            }
        }
        Autor nuevo = new Autor(String.format("A-%03d", ++contadorAutores), nombreAutor, "");
        autores.guardar(nuevo);
        return nuevo;
    }

    public List<MaterialBibliografico> listarMateriales() {
        return materiales.listar();
    }

    public List<MaterialBibliografico> buscarMateriales(String texto) {
        return materiales.filtrar(m -> m.coincideCon(texto));
    }

    public List<MaterialBibliografico> materialesConDisponibles() {
        return materiales.filtrar(m -> m.getCantidadDisponibles() > 0);
    }

    public List<Autor> listarAutores() {
        return autores.listar();
    }

    // =====================================================================
    // Socios
    // =====================================================================

    public SocioEstudiante registrarEstudiante(String dni, String nombre, String apellido, String email,
                                               String telefono, String carrera, String legajo)
            throws OperacionInvalidaException {
        validarPersona(dni, nombre, apellido, email);
        SocioEstudiante s = new SocioEstudiante(nuevoNumeroSocio(), dni.trim(), nombre.trim(), apellido.trim(),
                email.trim(), telefono.trim(), hoy(), carrera.trim(), legajo.trim());
        return guardarSocio(s);
    }

    public SocioDocente registrarDocente(String dni, String nombre, String apellido, String email,
                                         String telefono, String departamento)
            throws OperacionInvalidaException {
        validarPersona(dni, nombre, apellido, email);
        SocioDocente s = new SocioDocente(nuevoNumeroSocio(), dni.trim(), nombre.trim(), apellido.trim(),
                email.trim(), telefono.trim(), hoy(), departamento.trim());
        return guardarSocio(s);
    }

    public SocioGeneral registrarGeneral(String dni, String nombre, String apellido, String email, String telefono)
            throws OperacionInvalidaException {
        validarPersona(dni, nombre, apellido, email);
        SocioGeneral s = new SocioGeneral(nuevoNumeroSocio(), dni.trim(), nombre.trim(), apellido.trim(),
                email.trim(), telefono.trim(), hoy());
        return guardarSocio(s);
    }

    private <S extends Socio> S guardarSocio(S socio) {
        socios.guardar(socio);
        notificar("Socio registrado: " + socio.getNombreCompleto() + " (" + socio.getTipo() + ")");
        return socio;
    }

    private String nuevoNumeroSocio() {
        return String.format("S-%04d", ++contadorSocios);
    }

    private void validarPersona(String dni, String nombre, String apellido, String email)
            throws OperacionInvalidaException {
        if (dni == null || !dni.trim().matches("\\d{7,8}")) {
            throw new OperacionInvalidaException("El DNI debe tener 7 u 8 dígitos, sin puntos.");
        }
        if (!socios.filtrar(s -> s.getDni().equals(dni.trim())).isEmpty()) {
            throw new OperacionInvalidaException("Ya existe un socio con el DNI " + dni.trim() + ".");
        }
        validarNombreYEmail(nombre, apellido, email);
    }

    private void validarNombreYEmail(String nombre, String apellido, String email) throws OperacionInvalidaException {
        if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()) {
            throw new OperacionInvalidaException("Nombre y apellido son obligatorios.");
        }
        if (email != null && !email.isBlank() && !email.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new OperacionInvalidaException("El email no tiene un formato válido.");
        }
    }

    public void actualizarDatosSocio(Socio socio, String nombre, String apellido, String email, String telefono)
            throws OperacionInvalidaException {
        validarNombreYEmail(nombre, apellido, email);
        socio.setNombre(nombre.trim());
        socio.setApellido(apellido.trim());
        socio.setEmail(email.trim());
        socio.setTelefono(telefono.trim());
        notificar("Datos actualizados: " + socio.getNombreCompleto());
    }

    public void cambiarEstadoSocio(Socio socio) throws OperacionInvalidaException {
        if (socio.isActivo() && !socio.getPrestamosActivos().isEmpty()) {
            throw new OperacionInvalidaException("No se puede dar de baja: el socio tiene préstamos sin devolver.");
        }
        socio.setActivo(!socio.isActivo());
        notificar((socio.isActivo() ? "Socio reactivado: " : "Socio dado de baja: ") + socio.getNombreCompleto());
    }

    public List<Socio> listarSocios() {
        return socios.listar();
    }

    public Optional<Socio> buscarSocio(String numero) {
        return socios.buscarPorId(numero);
    }

    // =====================================================================
    // Préstamos y devoluciones
    // =====================================================================

    /** Presta el primer ejemplar disponible del material elegido. */
    public Prestamo prestar(Socio socio, MaterialBibliografico material) throws OperacionInvalidaException {
        Ejemplar ejemplar = material.primerEjemplarDisponible()
                .orElseThrow(() -> new OperacionInvalidaException(
                        "No hay ejemplares disponibles de \"" + material.getTitulo() + "\"."));
        return prestar(socio, ejemplar);
    }

    public Prestamo prestar(Socio socio, Ejemplar ejemplar) throws OperacionInvalidaException {
        if (bibliotecarioActual == null) {
            throw new OperacionInvalidaException("Debe seleccionarse el bibliotecario que atiende.");
        }
        String id = String.format("P-%04d", contadorPrestamos + 1);
        Prestamo prestamo = new Prestamo(id, socio, ejemplar, bibliotecarioActual, hoy());
        contadorPrestamos++;
        prestamos.guardar(prestamo);
        notificar("Préstamo " + id + ": \"" + ejemplar.getMaterial().getTitulo() + "\" a "
                + socio.getNombreCompleto() + ", vence el " + biblioteca.modelo.Formato.fecha(prestamo.getFechaVencimiento()));
        return prestamo;
    }

    /** @return la multa generada por atraso, o null si se devolvió en término */
    public Multa devolver(Prestamo prestamo) throws OperacionInvalidaException {
        String idMulta = String.format("M-%04d", contadorMultas + 1);
        Multa multa = prestamo.registrarDevolucion(idMulta, hoy(), politicaMulta);
        if (multa != null) {
            contadorMultas++;
            notificar("Devolución con " + multa.getDiasAtraso() + " día(s) de atraso: multa de "
                    + biblioteca.modelo.Formato.moneda(multa.getMonto()));
        } else {
            notificar("Devolución en término: " + prestamo.getEjemplar().getMaterial().getTitulo());
        }
        return multa;
    }

    public void renovar(Prestamo prestamo) throws OperacionInvalidaException {
        prestamo.renovar(hoy());
        notificar("Préstamo " + prestamo.getId() + " renovado hasta el "
                + biblioteca.modelo.Formato.fecha(prestamo.getFechaVencimiento()));
    }

    public List<Prestamo> listarPrestamos() {
        return prestamos.listar();
    }

    public List<Prestamo> prestamosPorEstado(EstadoPrestamo estado) {
        LocalDate hoy = hoy();
        return prestamos.filtrar(p -> p.getEstado(hoy) == estado);
    }

    // =====================================================================
    // Multas
    // =====================================================================

    public List<Multa> listarMultas() {
        List<Multa> multas = new ArrayList<>();
        for (Prestamo p : prestamos.listar()) {
            if (p.getMulta() != null) {
                multas.add(p.getMulta());
            }
        }
        return multas;
    }

    public List<Multa> multasPendientes() {
        List<Multa> pendientes = new ArrayList<>();
        for (Multa m : listarMultas()) {
            if (!m.estaPagada()) {
                pendientes.add(m);
            }
        }
        return pendientes;
    }

    public void pagarMulta(Multa multa) throws OperacionInvalidaException {
        multa.pagar(hoy());
        notificar("Multa " + multa.getId() + " abonada (" + biblioteca.modelo.Formato.moneda(multa.getMonto()) + ")");
    }

    public double getDeudaTotalPendiente() {
        double total = 0;
        for (Multa m : multasPendientes()) {
            total += m.getMonto();
        }
        return total;
    }

    // =====================================================================
    // Estadísticas
    // =====================================================================

    public List<MaterialBibliografico> masPrestados(int cantidad) {
        List<MaterialBibliografico> lista = materiales.filtrar(m -> m.getVecesPrestado() > 0);
        lista.sort(Comparator.comparingInt(MaterialBibliografico::getVecesPrestado).reversed()
                .thenComparing(MaterialBibliografico::getTitulo));
        return lista.subList(0, Math.min(cantidad, lista.size()));
    }

    public int getTotalEjemplares() {
        int total = 0;
        for (MaterialBibliografico m : materiales.listar()) {
            total += m.getEjemplares().size();
        }
        return total;
    }

    public String getNombre() {
        return nombre;
    }
}
