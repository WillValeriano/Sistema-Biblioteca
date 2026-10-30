package biblioteca.servicio;

import biblioteca.excepciones.OperacionInvalidaException;
import biblioteca.modelo.*;
import biblioteca.multas.MultaFijaDiaria;
import biblioteca.multas.PoliticaMulta;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// ¿QUÉ ES LA CLASE BIBLIOTECA?
// Es el "CEREBRO" del sistema. Guarda TODAS las listas (libros, socios, préstamos...)
// y tiene los métodos que aplican las REGLAS del negocio, por ejemplo:
//   - "no se le presta a un socio con multas impagas"
//   - "si se devuelve tarde, se genera una multa"
//
// La pantalla (paquete gui) NUNCA modifica las listas directamente: siempre le pide
// a la Biblioteca que haga la operación. Si la regla no se cumple, la Biblioteca
// lanza una OperacionInvalidaException con el motivo, y la pantalla lo muestra.
//
// Cómo está organizada (buscá estos títulos más abajo):
//   1. Autores y catálogo      4. Préstamos, renovaciones y devoluciones
//   2. Socios                  5. Reservas
//   3. Empleados               6. Multas y comprobantes
public class Biblioteca implements Serializable {

    // COMPOSICIÓN: la configuración nace y vive dentro de la Biblioteca
    private ConfiguracionBiblioteca configuracion;
    // Cómo se calculan las multas. Arranca como "Fija Diaria" y se puede cambiar.
    private PoliticaMulta politicaMulta;

    // Listas principales del sistema
    private List<MaterialBibliografico> catalogo;
    private List<Autor> autores;
    private List<Socio> socios;
    private List<Empleado> empleados;
    private List<Prestamo> prestamos;
    private List<Multa> multas;
    private List<Reserva> reservas;
    private List<String> historialActividad; // Bitácora: qué pasó y a qué hora

    public Biblioteca() {
        this.configuracion = new ConfiguracionBiblioteca();
        this.politicaMulta = new MultaFijaDiaria();
        this.catalogo = new ArrayList<>();
        this.autores = new ArrayList<>();
        this.socios = new ArrayList<>();
        this.empleados = new ArrayList<>();
        this.prestamos = new ArrayList<>();
        this.multas = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.historialActividad = new ArrayList<>();
        registrarActividad("Sistema de Biblioteca iniciado.");
    }

    // Registra cada evento con hora exacta en la bitácora de auditoría
    public void registrarActividad(String descripcion) {
        String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        historialActividad.add(0, "[" + hora + "] " + descripcion); // Lo agrega arriba de todo
    }

    // --- OPCIÓN DE LA BARRA SUPERIOR: EMPEZAR DESDE CERO ---
    public void vaciarTodo() {
        this.catalogo.clear();
        this.autores.clear();
        this.socios.clear();
        this.empleados.clear();
        this.prestamos.clear();
        this.multas.clear();
        this.reservas.clear();
        this.historialActividad.clear();
        this.configuracion = new ConfiguracionBiblioteca();

        // Reiniciamos todos los contadores estáticos a sus números iniciales
        Empleado.reiniciarContador();
        Socio.reiniciarContador();
        Autor.reiniciarContador();
        MaterialBibliografico.reiniciarContador();
        Ejemplar.reiniciarContador();
        Prestamo.reiniciarContador();
        Multa.reiniciarContador();
        Reserva.reiniciarContador();

        registrarActividad("Se vació la base de datos para comenzar desde cero.");
    }

    // ¿POR QUÉ HACE FALTA ESTO?
    // Los contadores "static" (que generan los números automáticos) NO se guardan en el archivo.
    // Al abrir el programa vuelven a su valor inicial. Si no los corregimos, el próximo socio
    // nuevo podría recibir un número que ya existe. Este método recorre lo cargado y deja cada
    // contador en el número más alto encontrado, para que el siguiente siga desde ahí.
    public void sincronizarContadores() {
        for (Empleado e : empleados) {
            Empleado.actualizarContadorSiEsMayor(e.getLegajo());
        }
        for (Socio s : socios) {
            Socio.actualizarContadorSiEsMayor(s.getNroSocio());
        }
        for (Autor a : autores) {
            Autor.actualizarContadorSiEsMayor(a.getIdAutor());
        }
        for (MaterialBibliografico m : catalogo) {
            MaterialBibliografico.actualizarContadorSiEsMayor(m.getIdMaterial());
            for (Ejemplar ej : m.getEjemplares()) {
                // El código es "EJ-7": le sacamos el "EJ-" y convertimos "7" en el número 7
                int numero = Integer.parseInt(ej.getCodigoInventario().replace("EJ-", ""));
                Ejemplar.actualizarContadorSiEsMayor(numero);
            }
        }
        for (Prestamo p : prestamos) {
            Prestamo.actualizarContadorSiEsMayor(p.getIdPrestamo());
        }
        for (Multa mu : multas) {
            Multa.actualizarContadorSiEsMayor(mu.getIdMulta());
        }
        for (Reserva r : reservas) {
            Reserva.actualizarContadorSiEsMayor(r.getIdReserva());
        }
    }

    // --- 1. GESTIÓN DE AUTORES Y CATÁLOGO ---

    // Si ya existe un autor con ese nombre lo devuelve; si no, lo crea.
    // Así no quedan autores repetidos.
    public Autor buscarOCrearAutor(String nombreCompleto, String nacionalidad) {
        for (Autor a : autores) {
            if (a.getNombreCompleto().equalsIgnoreCase(nombreCompleto.trim())) {
                return a;
            }
        }
        Autor nuevo = new Autor(nombreCompleto.trim(), nacionalidad.trim());
        autores.add(nuevo);
        return nuevo;
    }

    public void agregarMaterial(MaterialBibliografico material) {
        catalogo.add(material);
        registrarActividad("Alta de material: " + material.getTitulo() + " (" + material.getTipoMaterial() + ")");
    }

    // REGLA: no se puede borrar un material si alguna de sus copias está prestada.
    // "throws OperacionInvalidaException" avisa que este método puede lanzar ese error.
    public void eliminarMaterial(MaterialBibliografico material) throws OperacionInvalidaException {
        for (Ejemplar ej : material.getEjemplares()) {
            if (ej.getEstado() == EstadoEjemplar.PRESTADO) {
                throw new OperacionInvalidaException("No se puede eliminar '" + material.getTitulo() + "' porque tiene ejemplares prestados actualmente.");
            }
        }
        catalogo.remove(material);
        registrarActividad("Baja de material: " + material.getTitulo());
    }

    // --- 2. GESTIÓN DE SOCIOS ---

    // REGLA: no puede haber dos socios con el mismo DNI.
    public void registrarSocio(Socio nuevoSocio) throws OperacionInvalidaException {
        for (Socio s : socios) {
            if (s.getDni().equalsIgnoreCase(nuevoSocio.getDni().trim())) {
                throw new OperacionInvalidaException("Ya existe un socio registrado con el DNI " + nuevoSocio.getDni());
            }
        }
        socios.add(nuevoSocio);
        registrarActividad("Nuevo socio registrado: " + nuevoSocio.getNombre() + " (Socio #" + nuevoSocio.getNroSocio() + ")");
    }

    // REGLA: un socio solo se puede dar de baja si no debe libros ni multas.
    public void eliminarSocio(Socio socio) throws OperacionInvalidaException {
        if (contarPrestamosActivosDeSocio(socio) > 0) {
            throw new OperacionInvalidaException("El socio tiene préstamos activos sin devolver. No puede darse de baja.");
        }
        if (tieneMultasPendientes(socio)) {
            throw new OperacionInvalidaException("El socio tiene multas impagas. Debe abonarlas antes de darse de baja.");
        }
        socios.remove(socio);
        registrarActividad("Baja de socio: " + socio.getNombre());
    }

    // --- 3. GESTIÓN DE EMPLEADOS ---

    // REGLA: no puede haber dos empleados con el mismo DNI.
    public void registrarEmpleado(Empleado nuevoEmpleado) throws OperacionInvalidaException {
        for (Empleado e : empleados) {
            if (e.getDni().equalsIgnoreCase(nuevoEmpleado.getDni().trim())) {
                throw new OperacionInvalidaException("Ya existe un empleado con el DNI " + nuevoEmpleado.getDni());
            }
        }
        empleados.add(nuevoEmpleado);
        registrarActividad("Nuevo empleado registrado: " + nuevoEmpleado.getNombre() + " (Legajo #" + nuevoEmpleado.getLegajo() + ")");
    }

    public void eliminarEmpleado(Empleado empleado) {
        empleados.remove(empleado);
        registrarActividad("Baja de empleado: " + empleado.getNombre());
    }

    // --- 4. GESTIÓN DE PRÉSTAMOS, RENOVACIONES Y DEVOLUCIONES ---

    // Cuántos materiales tiene el socio en su poder ahora (activos + vencidos sin devolver)
    public int contarPrestamosActivosDeSocio(Socio socio) {
        int contador = 0;
        for (Prestamo p : prestamos) {
            if (p.getSocio().getNroSocio() == socio.getNroSocio() && p.getEstado() != EstadoPrestamo.DEVUELTO) {
                contador++;
            }
        }
        return contador;
    }

    public boolean tieneMultasPendientes(Socio socio) {
        for (Multa m : multas) {
            if (m.getSocio().getNroSocio() == socio.getNroSocio() && !m.isPagada()) {
                return true;
            }
        }
        return false;
    }

    // LA OPERACIÓN MÁS IMPORTANTE DEL SISTEMA.
    // Antes de prestar revisa 4 reglas, en orden. Si alguna falla, lanza la excepción y el
    // método TERMINA ahí (no se crea ningún préstamo):
    //   1. El socio tiene que estar activo.
    //   2. No puede tener multas sin pagar.
    //   3. No puede superar su límite de materiales (depende del tipo de socio).
    //   4. Tiene que quedar al menos una copia disponible.
    // Si pasa todas, crea el Préstamo y lo agrega a la lista.
    public Prestamo realizarPrestamo(Socio socio, MaterialBibliografico material, Empleado empleado) throws OperacionInvalidaException {
        // Regla 1
        if (!socio.isActivo()) {
            throw new OperacionInvalidaException("El socio " + socio.getNombre() + " se encuentra suspendido/inactivo.");
        }
        // Regla 2
        if (tieneMultasPendientes(socio)) {
            throw new OperacionInvalidaException("El socio tiene multas pendientes de pago. Debe regularizar su situación en la pestaña Multas.");
        }
        // Regla 3 (polimorfismo: cada tipo de socio responde su propio límite)
        int prestamosActuales = contarPrestamosActivosDeSocio(socio);
        int limiteMaximo = socio.getCantidadMaximaLibros(configuracion);
        if (prestamosActuales >= limiteMaximo) {
            throw new OperacionInvalidaException("El socio ya alcanzó su límite máximo permitido (" + limiteMaximo + " materiales para " + socio.getTipoSocio() + ").");
        }
        // Regla 4
        Ejemplar ejemplarLibre = material.obtenerPrimerEjemplarDisponible();
        if (ejemplarLibre == null) {
            throw new OperacionInvalidaException("No hay ejemplares disponibles de '" + material.getTitulo() + "'. Podés registrar una Reserva para este socio.");
        }

        // Todo OK: creamos el préstamo con los días que le corresponden a este tipo de socio
        int diasCorrespondientes = socio.getDiasMaximoPrestamo(configuracion);
        Prestamo nuevoPrestamo = new Prestamo(socio, ejemplarLibre, empleado, diasCorrespondientes);
        prestamos.add(nuevoPrestamo);

        // Si el socio estaba en lista de espera por este material, su reserva ya se cumplió
        for (Reserva r : reservas) {
            if (r.esActivaDe(socio, material)) {
                r.marcarComoCumplida();
            }
        }

        registrarActividad("Préstamo #" + nuevoPrestamo.getIdPrestamo() + ": '" + material.getTitulo() + "' a " + socio.getNombre());
        return nuevoPrestamo;
    }

    // Registra que el socio devolvió el material.
    // Si lo devolvió tarde, genera una Multa y la devuelve; si lo devolvió a tiempo, devuelve null.
    // El monto lo calcula la politicaMulta actual (Fija o Progresiva), sin que acá importe cuál es.
    public Multa registrarDevolucion(Prestamo prestamo) throws OperacionInvalidaException {
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new OperacionInvalidaException("Este préstamo ya fue devuelto anteriormente.");
        }

        prestamo.registrarDevolucion();
        long diasAtraso = prestamo.getDiasAtraso();
        Multa multaGenerada = null;

        if (diasAtraso > 0) {
            double monto = politicaMulta.calcularMonto(diasAtraso, configuracion);
            multaGenerada = new Multa(prestamo, diasAtraso, monto);
            multas.add(multaGenerada);
            registrarActividad("Devolución con atraso (" + diasAtraso + " días) en Préstamo #" + prestamo.getIdPrestamo() + ". Multa generada: $" + monto);
        } else {
            registrarActividad("Devolución en término del Préstamo #" + prestamo.getIdPrestamo() + " ('" + prestamo.getEjemplar().getMaterial().getTitulo() + "').");
        }

        return multaGenerada;
    }

    // Extiende el plazo de un préstamo. REGLAS: no puede estar devuelto ni vencido,
    // el socio no puede tener multas y se puede renovar como máximo 2 veces.
    public void renovarPrestamo(Prestamo prestamo) throws OperacionInvalidaException {
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new OperacionInvalidaException("No se puede renovar un préstamo que ya fue devuelto.");
        }
        if (prestamo.getEstado() == EstadoPrestamo.VENCIDO) {
            throw new OperacionInvalidaException("No se puede renovar un préstamo que ya está vencido. Debe devolverse primero.");
        }
        if (tieneMultasPendientes(prestamo.getSocio())) {
            throw new OperacionInvalidaException("El socio tiene multas impagas; no puede renovar préstamos.");
        }
        if (prestamo.getCantidadRenovaciones() >= 2) {
            throw new OperacionInvalidaException("Este préstamo ya alcanzó el máximo de 2 renovaciones permitidas.");
        }

        int diasExtra = prestamo.getSocio().getDiasMaximoPrestamo(configuracion);
        prestamo.renovarPrestamo(diasExtra);
        registrarActividad("Renovación de Préstamo #" + prestamo.getIdPrestamo() + " por " + diasExtra + " días adicionales.");
    }

    // --- 5. GESTIÓN DE RESERVAS ---

    // Anota al socio en la lista de espera de un material.
    // REGLAS: solo si no quedan copias disponibles, y sin reservas repetidas.
    public Reserva registrarReserva(Socio socio, MaterialBibliografico material) throws OperacionInvalidaException {
        if (material.getCantidadDisponibles() > 0) {
            throw new OperacionInvalidaException("El material tiene ejemplares disponibles actualmente. Puede realizar el préstamo directamente.");
        }
        for (Reserva r : reservas) {
            if (r.esActivaDe(socio, material)) {
                throw new OperacionInvalidaException("El socio ya tiene una reserva activa para este material.");
            }
        }
        Reserva nueva = new Reserva(socio, material);
        reservas.add(nueva);
        registrarActividad("Reserva registrada: '" + material.getTitulo() + "' para " + socio.getNombre());
        return nueva;
    }

    // Busca si algún socio está esperando el libro que recién se devolvió
    public Reserva buscarReservaPendienteParaMaterial(MaterialBibliografico material) {
        for (Reserva r : reservas) {
            if (r.isActiva() && r.getMaterial().getIdMaterial() == material.getIdMaterial()) {
                return r;
            }
        }
        return null;
    }

    // --- 6. GESTIÓN DE MULTAS Y COMPROBANTES ---
    public void pagarMulta(Multa multa) throws OperacionInvalidaException {
        if (multa.isPagada()) {
            throw new OperacionInvalidaException("Esta multa ya se encuentra pagada.");
        }
        multa.pagar();
        registrarActividad("Pago de Multa #" + multa.getIdMulta() + " ($" + multa.getMontoTotal() + ") abonado por " + multa.getSocio().getNombre());
    }

    public double calcularTotalMultasPendientes() {
        double suma = 0;
        for (Multa m : multas) {
            if (!m.isPagada()) {
                suma += m.getMontoTotal();
            }
        }
        return suma;
    }

    public double calcularTotalMultasCobradas() {
        double suma = 0;
        for (Multa m : multas) {
            if (m.isPagada()) {
                suma += m.getMontoTotal();
            }
        }
        return suma;
    }

    // Arma el texto del "ticket" de un préstamo, listo para mostrar o guardar en un .txt
    public String generarComprobantePrestamo(Prestamo p) {
        String atendidoPor;
        if (p.getEmpleadoResponsable() != null) {
            atendidoPor = p.getEmpleadoResponsable().getNombre() + " (Leg. #" + p.getEmpleadoResponsable().getLegajo() + ")";
        } else {
            atendidoPor = "Administración General";
        }

        return "================================================\n" +
               "       SISTEMA DE GESTIÓN DE BIBLIOTECA         \n" +
               "           COMPROBANTE DE PRÉSTAMO              \n" +
               "================================================\n" +
               " Nro. de Operación : #" + p.getIdPrestamo() + "\n" +
               " Fecha de Emisión  : " + p.getFechaPrestamo() + "\n" +
               " Fecha Vencimiento : " + p.getFechaVencimiento() + "\n" +
               "------------------------------------------------\n" +
               " DATOS DEL SOCIO:\n" +
               " Nombre   : " + p.getSocio().getNombre() + "\n" +
               " Nro Socio: #" + p.getSocio().getNroSocio() + " (" + p.getSocio().getTipoSocio() + ")\n" +
               " DNI      : " + p.getSocio().getDni() + "\n" +
               "------------------------------------------------\n" +
               " MATERIAL RETIRADO:\n" +
               " Título   : " + p.getEjemplar().getMaterial().getTitulo() + "\n" +
               " Código   : " + p.getEjemplar().getCodigoInventario() + "\n" +
               " Ubicación: " + p.getEjemplar().getMaterial().getUbicacionEstanteria() + "\n" +
               "------------------------------------------------\n" +
               " Atendido por: " + atendidoPor + "\n" +
               "================================================\n";
    }

    // --- GETTERS Y SETTERS GENERALES ---
    public ConfiguracionBiblioteca getConfiguracion() { return configuracion; }
    public PoliticaMulta getPoliticaMulta() { return politicaMulta; }
    public void setPoliticaMulta(PoliticaMulta politicaMulta) {
        this.politicaMulta = politicaMulta;
        registrarActividad("Política de multas cambiada a: " + politicaMulta.getNombrePolitica());
    }

    public List<MaterialBibliografico> getCatalogo() { return catalogo; }
    public List<Autor> getAutores() { return autores; }
    public List<Socio> getSocios() { return socios; }
    public List<Empleado> getEmpleados() { return empleados; }
    public List<Prestamo> getPrestamos() { return prestamos; }
    public List<Multa> getMultas() { return multas; }
    public List<Reserva> getReservas() { return reservas; }
    public List<String> getHistorialActividad() { return historialActividad; }
}