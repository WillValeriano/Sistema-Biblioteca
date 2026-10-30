package biblioteca.persistencia;

import biblioteca.modelo.*;
import biblioteca.servicio.Biblioteca;

import java.time.LocalDate;

public class DatosDeEjemplo {

    public static Biblioteca crearBibliotecaDeEjemplo() {
        Biblioteca biblio = new Biblioteca();
        biblio.vaciarTodo(); // Asegura que los contadores arranquen limpios desde el principio

        try {
            // 1. CARGAMOS EMPLEADOS DE EJEMPLO
            Empleado emp1 = new Empleado("35111222", "Laura Fernández", "3874112233", "laura@biblioteca.edu.ar", "Bibliotecario", "Mañana");
            Empleado emp2 = new Empleado("38444555", "Marcos Salvatierra", "3875998877", "marcos@biblioteca.edu.ar", "Atención al Público", "Tarde");
            Empleado emp3 = new Empleado("31777888", "Valeria Gómez", "3876543210", "valeria@biblioteca.edu.ar", "Administrador", "Mañana");
            biblio.registrarEmpleado(emp1);
            biblio.registrarEmpleado(emp2);
            biblio.registrarEmpleado(emp3);

            // 2. CARGAMOS AUTORES
            Autor borges = biblio.buscarOCrearAutor("Jorge Luis Borges", "Argentina");
            Autor cortazar = biblio.buscarOCrearAutor("Julio Cortázar", "Argentina");
            Autor garciaMarquez = biblio.buscarOCrearAutor("Gabriel García Márquez", "Colombia");
            Autor deitel = biblio.buscarOCrearAutor("Paul Deitel & Harvey Deitel", "Estados Unidos");
            Autor pressman = biblio.buscarOCrearAutor("Roger S. Pressman", "Estados Unidos");
            Autor nationalGeo = biblio.buscarOCrearAutor("Editorial National Geographic", "Internacional");
            Autor ieee = biblio.buscarOCrearAutor("IEEE Computer Society", "Internacional");
            Autor ruiz = biblio.buscarOCrearAutor("Matías Nicolás Ruiz", "Argentina");

            // 3. CARGAMOS LIBROS, REVISTAS Y TESIS
            Libro lib1 = new Libro("Ficciones", borges, 1944, "Fantasía", "Pasillo A - Estante 1",
                    "Obra cumbre de relatos breves que exploran laberintos, espejos y el infinito.", "", 3,
                    "978-987-566-647-4", "Debolsillo", 224);

            Libro lib2 = new Libro("Rayuela", cortazar, 1963, "Novela", "Pasillo A - Estante 2",
                    "Novela emblemática del boom latinoamericano que puede leerse en múltiples órdenes.", "", 2,
                    "978-84-204-3132-1", "Alfaguara", 736);

            Libro lib3 = new Libro("Cien años de soledad", garciaMarquez, 1967, "Novela", "Pasillo A - Estante 3",
                    "La historia de la familia Buendía a lo largo de siete generaciones en el pueblo de Macondo.", "", 1,
                    "978-950-07-3984-9", "Sudamericana", 496);

            Libro lib4 = new Libro("Cómo programar en Java", deitel, 2021, "Informática / Programación", "Pasillo C - Estante 1",
                    "Guía completa de programación orientada a objetos, estructuras de datos, Swing y UML en Java.", "", 4,
                    "978-607-32-3802-1", "Pearson Educación", 1050);

            Libro lib5 = new Libro("Ingeniería del Software: Un enfoque práctico", pressman, 2019, "Informática / Programación", "Pasillo C - Estante 2",
                    "Referencia clásica sobre diseño de sistemas, arquitectura de software, pruebas y modelado UML.", "", 2,
                    "978-1-4562-6080-4", "McGraw-Hill", 700);

            Revista rev1 = new Revista("National Geographic en Español", nationalGeo, 2024, "Historia", "Hemeroteca - Nivel 1",
                    "Edición especial sobre descubrimientos arqueológicos en los Andes.", "", 2,
                    "1665-7764", 245, "Mensual");

            Revista rev2 = new Revista("IEEE Software Magazine", ieee, 2025, "Informática / Programación", "Hemeroteca - Nivel 2",
                    "Artículos de investigación sobre calidad de software y desarrollo moderno.", "", 2,
                    "0740-7459", 112, "Mensual");

            Tesis tes1 = new Tesis("Arquitectura de Microservicios y Patrones OO en Sistemas Bancarios", ruiz, 2023,
                    "Informática / Programación", "Sector Académico - Box 4",
                    "Trabajo final de graduación enfocado en buenas prácticas de diseño orientado a objetos.", "", 1,
                    "Ing. Carlos López", "Facultad de Ingeniería", "Ingeniería en Informática");

            // Dejamos un ejemplar de "Cómo programar en Java" en estado EN_REPARACION para lucirlo en la tabla
            lib4.getEjemplares().get(3).setEstado(EstadoEjemplar.EN_REPARACION);
            lib4.getEjemplares().get(3).setObservacion("En encuadernación por tapas sueltas");

            biblio.agregarMaterial(lib1);
            biblio.agregarMaterial(lib2);
            biblio.agregarMaterial(lib3);
            biblio.agregarMaterial(lib4);
            biblio.agregarMaterial(lib5);
            biblio.agregarMaterial(rev1);
            biblio.agregarMaterial(rev2);
            biblio.agregarMaterial(tes1);

            // 4. CARGAMOS SOCIOS (Estudiantes, Docentes y Generales)
            SocioEstudiante soc1 = new SocioEstudiante("44123456", "Sofía Martínez", "3875112233", "sofia.m@alu.edu.ar", "Ingeniería en Informática");
            SocioEstudiante soc2 = new SocioEstudiante("45987654", "Lucas Morales", "3875445566", "lucas.m@alu.edu.ar", "Licenciatura en Sistemas");
            SocioDocente soc3 = new SocioDocente("26333444", "Dr. Roberto Sánchez", "3874889900", "rsanchez@doc.edu.ar", "Departamento de Informática");
            SocioDocente soc4 = new SocioDocente("29555666", "Mg. Elena Paz", "3874223344", "epaz@doc.edu.ar", "Departamento de Ciencias Básicas");
            SocioGeneral soc5 = new SocioGeneral("33888999", "Andrés Villalba", "3876119988", "andres.v@gmail.com", "Investigador Independiente");

            biblio.registrarSocio(soc1);
            biblio.registrarSocio(soc2);
            biblio.registrarSocio(soc3);
            biblio.registrarSocio(soc4);
            biblio.registrarSocio(soc5);

            // 5. CARGAMOS PRÉSTAMOS (Activos, Vencidos y Devueltos con Multa)
            LocalDate hoy = LocalDate.now();

            // Préstamo 1: Activo en término (Sofía se llevó Cómo programar en Java)
            Prestamo p1 = new Prestamo(soc1, lib4.obtenerPrimerEjemplarDisponible(), emp1, hoy.minusDays(2), hoy.plusDays(5));
            biblio.getPrestamos().add(p1);

            // Préstamo 2: Activo en término (Dr. Sánchez se llevó Cien años de soledad - ¡queda en 0 disponibles!)
            Prestamo p2 = new Prestamo(soc3, lib3.obtenerPrimerEjemplarDisponible(), emp2, hoy.minusDays(4), hoy.plusDays(11));
            biblio.getPrestamos().add(p2);

            // Préstamo 3: VENCIDO sin devolver todavía (Lucas se llevó Rayuela hace 14 días y venció hace 7 días)
            Prestamo p3 = new Prestamo(soc2, lib2.obtenerPrimerEjemplarDisponible(), emp1, hoy.minusDays(14), hoy.minusDays(7));
            biblio.getPrestamos().add(p3);

            // Préstamo 4: Devuelto tarde -> Genera una MULTA PENDIENTE para Andrés Villalba
            Prestamo p4 = new Prestamo(soc5, lib1.obtenerPrimerEjemplarDisponible(), emp2, hoy.minusDays(12), hoy.minusDays(6));
            p4.registrarDevolucion(); // Lo devuelve hoy con 6 días de atraso
            biblio.getPrestamos().add(p4);
            double montoMulta4 = biblio.getPoliticaMulta().calcularMonto(6, biblio.getConfiguracion());
            Multa m1 = new Multa(p4, 6, montoMulta4);
            biblio.getMultas().add(m1);

            // Préstamo 5: Devuelto tarde hace días -> Genera una MULTA YA PAGADA para Mg. Elena Paz
            Prestamo p5 = new Prestamo(soc4, rev1.obtenerPrimerEjemplarDisponible(), emp1, hoy.minusDays(20), hoy.minusDays(3));
            p5.registrarDevolucion();
            biblio.getPrestamos().add(p5);
            double montoMulta5 = biblio.getPoliticaMulta().calcularMonto(3, biblio.getConfiguracion());
            Multa m2 = new Multa(p5, 3, montoMulta5);
            m2.pagar();
            biblio.getMultas().add(m2);

            // 6. CARGAMOS UNA RESERVA DE EJEMPLO (Como "Cien años de soledad" quedó con 0 disponibles, Sofía lo reserva)
            biblio.registrarReserva(soc1, lib3);

            biblio.registrarActividad("Datos de ejemplo cargados correctamente.");

        } catch (Exception e) {
            System.err.println("Error al inicializar datos de ejemplo: " + e.getMessage());
        }

        return biblio;
    }
}