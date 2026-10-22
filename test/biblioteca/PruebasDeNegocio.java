package biblioteca;

import biblioteca.servicio.Biblioteca;
import biblioteca.persistencia.*;
import biblioteca.modelo.*;
import biblioteca.multas.*;
import biblioteca.excepciones.*;
/**
 * Pruebas de las reglas de negocio sin interfaz gráfica.
 * Ejecutar: java -cp out biblioteca.PruebasDeNegocio
 */
public class PruebasDeNegocio {
  static int ok, fail;
  interface Op { void run() throws Exception; }
  static void rechaza(String n, Op o){ try{o.run(); fail++; System.out.println("FALLA (no rechazó): "+n);}catch(OperacionInvalidaException e){ok++; System.out.println("ok  "+n+" -> "+e.getMessage());}catch(Exception e){fail++;System.out.println("FALLA "+n+" "+e);} }
  static void check(String n, boolean c){ if(c){ok++;System.out.println("ok  "+n);} else {fail++;System.out.println("FALLA "+n);} }
  public static void main(String[] a) throws Exception {
    Biblioteca b = DatosDeEjemplo.crear();
    Socio ana=b.buscarSocio("S-0001").get(), lucia=b.buscarSocio("S-0003").get(), carla=b.buscarSocio("S-0007").get(), ramon=b.buscarSocio("S-0004").get();
    MaterialBibliografico algoritmos = b.buscarMateriales("algoritmos").get(0);
    rechaza("sin stock", () -> b.prestar(carla, algoritmos));
    rechaza("socio con vencido", () -> b.prestar(lucia, b.buscarMateriales("1984").get(0)));
    rechaza("eliminar material prestado", () -> b.eliminarMaterial(algoritmos));
    rechaza("baja con préstamos", () -> b.cambiarEstadoSocio(ramon));
    rechaza("DNI duplicado", () -> b.registrarGeneral("42111333","X","Y","",""));
    rechaza("DNI inválido", () -> b.registrarGeneral("12.345","X","Y","",""));
    rechaza("email inválido", () -> b.registrarGeneral("12345678","X","Y","no-mail",""));
    rechaza("libro sin autor", () -> b.registrarLibro("T",2000,"c","","", " , ",1,""));
    Prestamo pa = ana.getPrestamosActivos().get(0);
    rechaza("renovar dos veces", () -> b.renovar(pa));
    Prestamo pc = b.prestar(carla, b.buscarMateriales("1984").get(0));
    check("general 10 días", pc.getFechaVencimiento().equals(b.hoy().plusDays(10)));
    Prestamo pr = b.prestar(carla, b.buscarMateriales("Muy Interesante").get(0));
    check("revista limitada a 3 días", pr.getFechaVencimiento().equals(b.hoy().plusDays(3)));
    rechaza("límite general (2)", () -> b.prestar(carla, b.buscarMateriales("Martín Fierro").get(0)));
    b.avanzarDias(15);
    rechaza("renovar vencido", () -> b.renovar(pc));
    b.setPoliticaMulta(new MultaProgresiva(500, 0.5));
    Multa m = b.devolver(pc);
    check("multa progresiva 5 días = 2500", m != null && m.getMonto()==2500 && m.getDiasAtraso()==5);
    rechaza("devolver dos veces", () -> b.devolver(pc));
    b.pagarMulta(m);
    rechaza("pagar dos veces", () -> b.pagarMulta(m));
    check("búsqueda sin tildes", b.buscarMateriales("tunel").size()==1 && b.buscarMateriales("cortazar").size()==1);
    check("autor compartido (agregación)", ((Libro)b.buscarMateriales("Ficciones").get(0)).getAutores().get(0) == ((Libro)b.buscarMateriales("Aleph").get(0)).getAutores().get(0));
    GestorPersistencia g = new GestorPersistencia(java.nio.file.Files.createTempFile("biblioteca", ".dat")); g.guardar(b); Biblioteca c = g.cargar();
    check("persistencia conserva identidad de objetos", c.listarPrestamos().get(0).getSocio() == c.buscarSocio(c.listarPrestamos().get(0).getSocio().getId()).get());
    System.out.println("\n"+ok+" OK, "+fail+" fallas");
    if (fail > 0) System.exit(1);
  }}
