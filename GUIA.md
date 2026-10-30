# Guía para entender el Sistema de Biblioteca desde cero

Esta guía es un **resumen corto**: qué hace cada parte del proyecto y en qué orden conviene leerlo.
Todos los archivos `.java` tienen además comentarios que explican el código.

> ¿Nunca programaste en Java? Empezá por [`EXPLICACION_DESDE_CERO.md`](EXPLICACION_DESDE_CERO.md),
> que enseña lo necesario de Java y recorre el proyecto completo.
> Los cambios hechos al proyecto están explicados uno por uno en [`informes/`](informes/).

---

## 1. ¿Qué hace el programa?

Administra una biblioteca:

- Tiene un **catálogo** de materiales (libros, revistas y tesis), cada uno con varias **copias físicas** (ejemplares).
- Tiene **socios** (estudiantes, docentes y generales) que se llevan materiales **prestados**.
- Si un socio devuelve tarde, se le cobra una **multa**.
- Si no quedan copias, el socio puede hacer una **reserva** (lista de espera).
- Todo se **guarda en un archivo** para que no se pierda al cerrar el programa.

---

## 2. Cómo está organizado (los paquetes)

Un *paquete* es una carpeta que agrupa clases relacionadas.

```
src/biblioteca/
├── Main.java          → Punto de entrada: abre la ventana
├── modelo/            → LAS COSAS del mundo real (Libro, Socio, Préstamo...)
├── servicio/          → EL CEREBRO: la clase Biblioteca, que aplica las reglas
├── multas/            → Las formas de calcular cuánto cobrar de multa
├── persistencia/      → Guardar/cargar del disco y datos de ejemplo
├── excepciones/       → El error que se lanza cuando no se cumple una regla
└── gui/               → Las pantallas (lo que se ve)
```

La idea clave es la **separación en capas**:

```
   gui (pantallas)  ──pide operaciones a──►  servicio.Biblioteca  ──usa──►  modelo
         ▲                                          │
         └──── muestra el error si falla ◄── OperacionInvalidaException
```

La pantalla **nunca** decide las reglas: le pide a `Biblioteca` que haga algo y, si no se puede,
`Biblioteca` le avisa con una excepción y el motivo.

---

## 3. En qué orden leer el código

| Paso | Archivo | Qué vas a aprender |
|---|---|---|
| 1 | `modelo/Persona.java` | Clase abstracta, atributos `protected`, getters/setters |
| 2 | `modelo/Socio.java` y `SocioEstudiante.java` | Herencia (`extends`), métodos abstractos, polimorfismo |
| 3 | `modelo/Autor.java` | Contadores `static` para IDs automáticos |
| 4 | `modelo/MaterialBibliografico.java` y `Libro.java` | Herencia, `super(...)`, `@Override` |
| 5 | `modelo/Prestable.java` y `Ejemplar.java` | Interfaces (`implements`), composición |
| 6 | `modelo/EstadoEjemplar.java` | Qué es un `enum` |
| 7 | `modelo/Prestamo.java` | Fechas con `LocalDate`, `this(...)`, estado calculado |
| 8 | `multas/PoliticaMulta.java` y sus dos clases | Interfaz como “estrategia” intercambiable |
| 9 | `excepciones/OperacionInvalidaException.java` | Excepciones propias, `throw` / `try-catch` |
| 10 | `servicio/Biblioteca.java` | Las reglas del negocio — **leé `realizarPrestamo`** |
| 11 | `persistencia/DatosDeEjemplo.java` | Cómo se crean y conectan todos los objetos |
| 12 | `persistencia/GestorPersistencia.java` | Serialización: guardar objetos en un archivo |
| 13 | `Main.java` y `gui/PanelInicio.java` | Cómo se arma una ventana con Swing |

---

## 4. Las clases del modelo y cómo se relacionan

```
                Persona (abstracta)
                 ├── Empleado
                 └── Socio (abstracta)
                      ├── SocioEstudiante
                      ├── SocioDocente
                      └── SocioGeneral

     MaterialBibliografico (abstracta) ◆──────── Ejemplar  (implements Prestable)
      ├── Libro                     tiene 1..*    (copia física)
      ├── Revista
      └── Tesis          ◇── Autor (agregación)

     Prestamo:  Socio + Ejemplar + Empleado + fechas
     Multa:     Prestamo + monto
     Reserva:   Socio + MaterialBibliografico
```

- **◆ Composición** (Material → Ejemplar): las copias *nacen dentro* del material, en su constructor. Si se borra el material, sus copias desaparecen con él.
- **◇ Agregación** (Material → Autor): el autor existe por su cuenta; si se borra un libro, el autor sigue estando.

**¿Material o Ejemplar?** El *material* es la obra (“Rayuela”). El *ejemplar* es cada copia en el estante (EJ-1004, EJ-1005…). Lo que se presta es el **ejemplar**.

---

## 5. Los 4 pilares de POO, con ejemplos de ESTE proyecto

| Concepto | Dónde verlo |
|---|---|
| **Encapsulamiento** — los atributos son `private`/`protected` y se accede con getters/setters | Cualquier clase del modelo, ej. `Persona` |
| **Herencia** — una clase hija reutiliza lo de la padre (`extends`) | `Libro extends MaterialBibliografico`, `SocioDocente extends Socio` |
| **Abstracción** — clases que no se pueden instanciar, solo sirven de molde | `Persona`, `Socio`, `MaterialBibliografico` (son `abstract`) |
| **Polimorfismo** — la misma llamada hace cosas distintas según el objeto | `socio.getCantidadMaximaLibros(config)` → 3, 5 o 2 según el tipo de socio. `politicaMulta.calcularMonto(...)` → fija o progresiva |

---

## 6. Recorrido de una operación: prestar un libro

Qué pasa cuando se llama a `biblioteca.realizarPrestamo(socio, material, empleado)`:

1. ¿El socio está activo? Si no → `throw new OperacionInvalidaException(...)`.
2. ¿Tiene multas sin pagar? Si sí → excepción.
3. ¿Ya tiene su máximo de libros? El máximo lo responde **cada tipo de socio** (polimorfismo). Si llegó → excepción.
4. ¿Queda alguna copia? `material.obtenerPrimerEjemplarDisponible()`. Si devuelve `null` → excepción.
5. Todo OK → `new Prestamo(...)`. El constructor del préstamo pone el ejemplar en `PRESTADO`.
6. Si el socio tenía una reserva de ese material, se marca como cumplida.
7. Se anota en el historial.

Y al **devolver** (`registrarDevolucion`): se marca la fecha de devolución, el ejemplar vuelve a `DISPONIBLE` y,
si hubo atraso, la `politicaMulta` calcula el monto y se crea una `Multa`.

---

## 7. Los IDs automáticos (contadores `static`)

Cada clase con número automático tiene un contador `static` (uno solo, compartido por todos los objetos):

```java
private static int contadorPrestamos = 0;

public Prestamo(...) {
    contadorPrestamos++;                 // 0 → 1
    this.idPrestamo = contadorPrestamos; // este préstamo es el #1
}
```

Los `static` **no se guardan en el archivo**. Por eso, al cargar los datos,
`Biblioteca.sincronizarContadores()` los pone en el número más alto que ya existe, para no repetir IDs.

| Clase | Primer número |
|---|---|
| Socio | 1001 |
| Empleado (legajo) | 101 |
| MaterialBibliografico | 101 |
| Ejemplar | EJ-1001 |
| Autor, Préstamo, Multa, Reserva | 1 |

---

## 8. Cómo compilar y ejecutar

Hace falta tener instalado un JDK (Java 17 o más nuevo). Desde la carpeta del proyecto:

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin biblioteca.Main
```

En Windows (PowerShell):

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp bin biblioteca.Main
```

La primera vez se crea `biblioteca_datos.dat` con los datos de ejemplo. Si lo borrás, se vuelven a generar.

---

## 9. Glosario rápido

| Palabra | Significado |
|---|---|
| `abstract` | Clase “molde”: no se puede hacer `new` de ella; o método sin código que las hijas deben escribir |
| `extends` | Heredar de una clase |
| `implements` | Cumplir el contrato de una interfaz |
| `interface` | Lista de métodos que una clase promete tener, sin decir cómo |
| `@Override` | “Estoy reescribiendo un método heredado” |
| `super(...)` | Llamar al constructor de la clase padre |
| `this(...)` | Llamar a otro constructor de la misma clase |
| `static` | Pertenece a la clase, no a cada objeto (hay uno solo) |
| `final` | No se puede cambiar después |
| `enum` | Lista cerrada de valores posibles |
| `throw` / `throws` | Lanzar una excepción / avisar que un método puede lanzarla |
| `try` / `catch` | Intentar algo y atrapar el error si ocurre |
| `Serializable` | Permite guardar el objeto en un archivo |
| `List<Socio>` | Lista que solo puede contener objetos `Socio` (genéricos) |
| `null` | “No hay nada”, ninguna referencia a un objeto |
