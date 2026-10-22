package biblioteca.persistencia;

import biblioteca.excepciones.OperacionInvalidaException;
import biblioteca.modelo.Bibliotecario;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Revista;
import biblioteca.modelo.Socio;
import biblioteca.servicio.Biblioteca;

/**
 * Carga un conjunto de datos realista para la demostración: catálogo, socios de las tres
 * categorías, préstamos en curso, préstamos vencidos y multas (pagas e impagas).
 * Usa la misma API pública que la interfaz gráfica, así que también sirve como prueba.
 */
public final class DatosDeEjemplo {

    private DatosDeEjemplo() {
    }

    public static Biblioteca crear() {
        Biblioteca b = new Biblioteca("Biblioteca Central");
        try {
            cargar(b);
        } catch (OperacionInvalidaException e) {
            throw new IllegalStateException("Datos de ejemplo inválidos: " + e.getMessage(), e);
        }
        return b;
    }

    private static void cargar(Biblioteca b) throws OperacionInvalidaException {
        b.registrarBibliotecario(new Bibliotecario("B-01", "30111222", "Laura", "Gómez",
                "lgomez@biblioteca.local", "387-4111222", "Mañana"));
        b.registrarBibliotecario(new Bibliotecario("B-02", "28999111", "Martín", "Ruiz",
                "mruiz@biblioteca.local", "387-4999111", "Tarde"));

        // ---- Catálogo ----
        Libro ficciones = b.registrarLibro("Ficciones", 1944, "Literatura argentina", "978-950-07-3001-1",
                "Sur", "Jorge Luis Borges", 3, "Estante A1");
        b.registrarLibro("El Aleph", 1949, "Literatura argentina", "978-950-07-3002-8",
                "Losada", "Jorge Luis Borges", 2, "Estante A1");
        Libro rayuela = b.registrarLibro("Rayuela", 1963, "Literatura argentina", "978-950-07-3003-5",
                "Sudamericana", "Julio Cortázar", 2, "Estante A2");
        Libro tunel = b.registrarLibro("El túnel", 1948, "Literatura argentina", "978-950-07-3004-2",
                "Sur", "Ernesto Sábato", 2, "Estante A2");
        b.registrarLibro("Martín Fierro", 1872, "Poesía gauchesca", "978-950-07-3005-9",
                "Colihue", "José Hernández", 2, "Estante A3");
        Libro cienAnios = b.registrarLibro("Cien años de soledad", 1967, "Literatura latinoamericana",
                "978-950-07-3006-6", "Sudamericana", "Gabriel García Márquez", 3, "Estante B1");
        b.registrarLibro("1984", 1949, "Novela distópica", "978-950-07-3007-3",
                "Debolsillo", "George Orwell", 2, "Estante B2");
        b.registrarLibro("El principito", 1943, "Literatura infantil", "978-950-07-3008-0",
                "Emecé", "Antoine de Saint-Exupéry", 4, "Estante C1");
        Libro cleanCode = b.registrarLibro("Código limpio", 2008, "Programación", "978-84-415-3210-6",
                "Anaya", "Robert C. Martin", 2, "Estante D1");
        Libro patrones = b.registrarLibro("Patrones de diseño", 1994, "Programación", "978-84-7829-059-8",
                "Addison-Wesley", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", 2, "Estante D1");
        Libro effective = b.registrarLibro("Effective Java", 2018, "Programación", "978-0-13-468599-1",
                "Addison-Wesley", "Joshua Bloch", 1, "Estante D2");
        b.registrarLibro("Head First Java", 2022, "Programación", "978-1-4919-1077-1",
                "O'Reilly", "Kathy Sierra, Bert Bates, Trisha Gee", 2, "Estante D2");
        Libro algoritmos = b.registrarLibro("Introducción a los algoritmos", 2009, "Programación", "978-0-262-03384-8",
                "MIT Press", "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein",
                1, "Estante D3");
        Revista ieee = b.registrarRevista("IEEE Software", 2026, "Ingeniería de software", "0740-7459",
                5, "Bimestral", 2, "Hemeroteca");
        b.registrarRevista("Muy Interesante", 2026, "Divulgación científica", "0327-3504",
                478, "Mensual", 2, "Hemeroteca");
        b.registrarRevista("National Geographic", 2026, "Divulgación científica", "0027-9358",
                10, "Mensual", 1, "Hemeroteca");

        // ---- Socios (dados de alta en distintas fechas) ----
        b.avanzarDias(-400);
        Socio ana = b.registrarEstudiante("42111333", "Ana", "Pereyra", "ana.pereyra@mail.com",
                "387-5111333", "Ingeniería en Informática", "INF-1021");
        Socio tomas = b.registrarEstudiante("43222444", "Tomás", "Villalba", "tvillalba@mail.com",
                "387-5222444", "Ingeniería en Informática", "INF-1187");
        Socio lucia = b.registrarEstudiante("41999888", "Lucía", "Correa", "lucia.correa@mail.com",
                "387-5999888", "Abogacía", "DER-0544");
        b.avanzarDias(150);
        Socio ramon = b.registrarDocente("25444555", "Ramón", "Aguirre", "raguirre@universidad.edu",
                "387-4444555", "Informática");
        Socio silvia = b.registrarDocente("27555666", "Silvia", "Méndez", "smendez@universidad.edu",
                "387-4555666", "Matemática");
        b.avanzarDias(120);
        Socio jorge = b.registrarGeneral("33666777", "Jorge", "Torres", "jtorres@mail.com", "387-6666777");
        b.avanzarDias(60);
        b.registrarGeneral("35777888", "Carla", "Ibáñez", "carla.ibanez@mail.com", "387-6777888");

        // ---- Historial: se "viaja en el tiempo" para generar préstamos pasados ----
        b.volverAHoy();
        b.avanzarDias(-40);                 // día -40
        Prestamo p1 = b.prestar(ana, ficciones);
        Prestamo p2 = b.prestar(ramon, patrones);
        Prestamo p3 = b.prestar(silvia, cienAnios);
        Prestamo p4 = b.prestar(jorge, tunel);

        b.avanzarDias(5);                   // día -35
        b.devolver(p1);                     // estudiante (7 días): en término
        b.devolver(p3);                     // docente (14 días): en término

        b.avanzarDias(13);                  // día -22
        b.devolver(p2);                     // docente: 4 días de atraso → multa con 25% de descuento
        b.devolver(p4);                     // general: 8 días de atraso → multa sin descuento
        b.pagarMulta(p2.getMulta());        // el docente paga; el socio general queda debiendo

        b.avanzarDias(4);                   // día -18
        b.prestar(lucia, cienAnios);        // estudiante, vence en 7 días → hoy figura VENCIDO
        Prestamo p5 = b.prestar(tomas, effective);

        b.avanzarDias(12);                  // día -6
        b.devolver(p5);                     // 5 días de atraso → multa con 50% de descuento, impaga
        Prestamo p6 = b.prestar(ana, cleanCode);
        b.prestar(ana, algoritmos);         // único ejemplar → queda "Sin stock"
        b.prestar(ramon, rayuela);
        b.prestar(ramon, patrones);

        b.avanzarDias(3);                   // día -3
        b.prestar(silvia, ieee);            // revista: plazo de 3 días, vence hoy
        b.renovar(p6);                      // la estudiante renueva una vez

        b.volverAHoy();
    }
}
