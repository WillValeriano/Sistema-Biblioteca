# El Sistema de Biblioteca explicado desde cero

> Este documento está escrito para alguien que **nunca programó en Java**.
> No hace falta saber nada antes de empezar. Cada palabra rara se explica la primera vez que aparece,
> y todos los ejemplos están sacados del código real de este proyecto.
>
> Cómo leerlo:
> - **Parte 1** enseña lo mínimo de Java para poder leer el proyecto.
> - **Parte 2** recorre el proyecto archivo por archivo.
> - **Parte 3** cuenta, paso a paso, qué pasa cuando el programa hace algo (abrir, prestar, devolver).
> - Al final hay un **glosario** para buscar palabras rápido.
>
> Los informes de cada cambio que se le hace al proyecto están en la carpeta [`informes/`](informes/).

---

# PARTE 1 — Java desde cero

## 1.1 ¿Qué es un programa?

Un programa es una **lista de instrucciones** que la computadora sigue al pie de la letra.
En Java esas instrucciones se escriben en archivos de texto que terminan en `.java`.
A ese texto se le llama **código fuente**.

La computadora no entiende directamente el texto `.java`. Hay dos pasos:

1. **Compilar** (`javac`): un programa llamado *compilador* lee tus `.java`, revisa que estén bien escritos
   y los traduce a archivos `.class`, que la máquina de Java sí entiende.
   Si escribiste algo mal (te faltó un `;`, usaste una palabra que no existe), el compilador **no te deja seguir**
   y te dice en qué línea está el error.
2. **Ejecutar** (`java`): la *máquina virtual de Java* lee los `.class` y hace lo que dicen.

```
  Biblioteca.java  ──javac──►  Biblioteca.class  ──java──►  el programa funcionando
  (texto que escribís)         (traducido)                  (la ventana que ves)
```

## 1.2 Comentarios: texto que la computadora ignora

Todo lo que está después de `//` en una línea es un **comentario**: una nota para las personas.
La computadora lo saltea por completo.

```java
// Esto es un comentario. Java no lo lee.
int edad = 20; // También se puede poner al final de una línea.
```

Por eso el código del proyecto está lleno de líneas con `//`: son explicaciones.

## 1.3 Clases y objetos: el molde y las galletitas

Esta es **la idea más importante de Java**.

- Una **clase** es un **molde**. Describe cómo es una cosa: qué datos tiene y qué sabe hacer.
- Un **objeto** es **una cosa concreta hecha con ese molde**.

Ejemplo del proyecto: existe la clase `Libro`. Es el molde que dice
"un libro tiene título, autor, año, ISBN, editorial…".
Después, con ese molde, se fabrican objetos: el libro *Rayuela*, el libro *Ficciones*, etc.
Hay **una** clase `Libro` y **muchos** objetos libro.

```
   Clase Libro (molde)                  Objetos (galletitas)
  ┌──────────────────────┐          ┌───────────────────────┐
  │ titulo               │   ──►    │ titulo = "Rayuela"     │
  │ anioPublicacion      │          │ anioPublicacion = 1963 │
  │ isbn                 │          └───────────────────────┘
  └──────────────────────┘          ┌───────────────────────┐
                            ──►     │ titulo = "Ficciones"   │
                                    │ anioPublicacion = 1944 │
                                    └───────────────────────┘
```

Una clase se escribe así:

```java
public class Autor {
    // acá adentro va todo lo que describe a un Autor
}
```

- `public` = cualquier otra parte del programa puede usar esta clase.
- `class` = "acá empieza una clase".
- `Autor` = el nombre de la clase. Por costumbre empieza con mayúscula.
- Las llaves `{ }` marcan dónde empieza y dónde termina. **Todo lo que está entre las llaves pertenece a la clase.**

> **Regla de Java:** una clase `public` tiene que estar en un archivo que se llame **exactamente igual**.
> La clase `Autor` va en `Autor.java`. Por eso hubo que renombrar `MaterialBibliográfico.java` (con tilde)
> a `MaterialBibliografico.java`: la clase se llamaba sin tilde y el compilador daba error.

## 1.4 Atributos: los datos que guarda cada objeto

Los **atributos** son las "casillas" donde cada objeto guarda sus datos. Ejemplo real, de `Autor.java`:

```java
public class Autor {
    private int idAutor;
    private String nombreCompleto;
    private String nacionalidad;
}
```

Cada línea dice: **quién puede verlo**, **qué tipo de dato es** y **cómo se llama**.

```
   private     String      nombreCompleto ;
   ───┬───     ──┬───      ──────┬─────── ┬
   quién lo    tipo de     nombre de la   fin de la
   puede ver   dato        casilla        instrucción
```

El **`;`** (punto y coma) marca el final de cada instrucción, como el punto al final de una oración.

### Los tipos de dato que aparecen en el proyecto

| Tipo | Qué guarda | Ejemplo en el proyecto |
|---|---|---|
| `int` | Un número entero (sin coma) | `int numeroPaginas = 736;` |
| `long` | Un número entero que puede ser muy grande | `long diasAtraso` |
| `double` | Un número con decimales | `double multaPorDia = 500.0;` |
| `boolean` | Solo `true` (verdadero) o `false` (falso) | `boolean pagada = false;` |
| `String` | Texto, siempre entre comillas dobles | `String titulo = "Rayuela";` |
| `LocalDate` | Una fecha (día, mes, año) | `LocalDate fechaPrestamo` |
| `List<Socio>` | Una lista de objetos `Socio` | `List<Socio> socios` |
| Otra clase | Un objeto de esa clase | `Autor autor;` (guarda un autor) |

## 1.5 Variables, el signo `=` y el signo `==`

Una **variable** es una casilla con nombre donde se guarda un valor. Los atributos son variables que
pertenecen al objeto; también hay variables que solo existen un ratito dentro de un método.

- `=` (un igual) significa **"guardá esto acá"**. No es "es igual a".
  `contador = 0;` → "en la casilla `contador` guardá un 0".
- `==` (dos iguales) significa **"¿son iguales?"** y da `true` o `false`.
  `estado == EstadoEjemplar.DISPONIBLE` → "¿el estado es DISPONIBLE?".
- `!=` significa **"¿son distintos?"**.
- `!` delante de algo significa **"NO"**. `!socio.isActivo()` → "el socio NO está activo".
- `&&` significa **"Y"** (las dos cosas tienen que ser verdad). `||` significa **"O"** (alcanza con una).
- `contador++;` es un atajo de `contador = contador + 1;` (sumarle 1).
- `+` entre textos los **pega**: `"Socio #" + 1001` da `"Socio #1001"`.

## 1.6 Métodos: lo que un objeto sabe hacer

Un **método** es un bloque de instrucciones con nombre. Es una "acción" que el objeto sabe hacer.
Ejemplo real, de `Multa.java`:

```java
public void pagar() {
    this.pagada = true;
    this.fechaPago = LocalDate.now();
}
```

```
   public    void     pagar   ( )      { ... }
   ──┬───    ──┬─     ──┬──   ─┬─      ───┬───
   quién    qué       nombre  datos    las instrucciones
   lo usa   devuelve          que      que se ejecutan
                              recibe   al llamarlo
```

- `void` significa **"no devuelve nada"**: el método hace algo y listo.
- Si el método **devuelve** un resultado, en lugar de `void` va el tipo del resultado, y adentro usa `return`:

```java
// De Prestamo.java: devuelve un número (los días de atraso)
public long getDiasAtraso() {
    ...
    return 0;   // "return" = "terminá acá y entregá este valor"
}
```

- Entre los paréntesis van los **parámetros**: datos que el método necesita para trabajar.

```java
// De Prestamo.java: necesita saber cuántos días agregar
public void renovarPrestamo(int diasExtra) {
    this.fechaVencimiento = this.fechaVencimiento.plusDays(diasExtra);
    this.cantidadRenovaciones++;
}
```

**Llamar** (usar) un método se hace con un punto: `objeto.metodo(datos)`.

```java
multa.pagar();              // "multa, ejecutá tu método pagar"
prestamo.renovarPrestamo(7); // "préstamo, renovate 7 días más"
```

## 1.7 El constructor y `new`: cómo se fabrica un objeto

Para fabricar un objeto con el molde se usa la palabra **`new`**:

```java
Autor borges = new Autor("Jorge Luis Borges", "Argentina");
```

Esto se lee: "creá un objeto `Autor` nuevo, con estos datos, y guardalo en la variable `borges`".

Al hacer `new`, Java ejecuta un método especial de la clase llamado **constructor**.
El constructor **se llama igual que la clase** y **no tiene tipo de retorno**. Su trabajo es llenar los atributos
del objeto recién nacido. Ejemplo real de `Autor.java`:

```java
public Autor(String nombreCompleto, String nacionalidad) {
    contadorAutores++;
    this.idAutor = contadorAutores;
    this.nombreCompleto = nombreCompleto;
    this.nacionalidad = nacionalidad;
}
```

### ¿Qué es `this`?

`this` significa **"este objeto, el que estoy construyendo o usando ahora"**.
Hace falta porque el parámetro y el atributo se llaman igual (`nombreCompleto`):

- `this.nombreCompleto` → la casilla **del objeto**.
- `nombreCompleto` (solo) → el dato **que llegó por parámetro**.

Entonces `this.nombreCompleto = nombreCompleto;` significa
"guardá en la casilla del objeto el nombre que me pasaron".

## 1.8 Decisiones con `if` / `else`

`if` ("si") ejecuta un bloque **solo si** una condición es verdadera. `else` ("si no") es lo que pasa en caso contrario.
Ejemplo real de `Prestamo.java`:

```java
public EstadoPrestamo getEstado() {
    if (fechaDevolucionReal != null) {      // si ya tiene fecha de devolución...
        return EstadoPrestamo.DEVUELTO;     // ...está devuelto
    }
    if (LocalDate.now().isAfter(fechaVencimiento)) { // si hoy es después del vencimiento...
        return EstadoPrestamo.VENCIDO;               // ...está vencido
    }
    return EstadoPrestamo.ACTIVO;           // si no pasó nada de lo anterior, está activo
}
```

Como cada `return` **termina el método**, apenas una condición se cumple ya no se revisan las siguientes.

## 1.9 Listas y el ciclo `for`

Una **lista** guarda muchos objetos en orden. En el proyecto se usa `List` y `ArrayList`:

```java
private List<Socio> socios;        // una lista que SOLO puede tener Socios
this.socios = new ArrayList<>();   // se crea vacía
```

- `List<Socio>` → "lista de Socios". Lo que va entre `< >` dice qué tipo de cosas guarda.
- `ArrayList` es la forma concreta de lista que se usa (la más común).

Operaciones que aparecen en el proyecto:

| Código | Qué hace |
|---|---|
| `socios.add(s)` | Agrega `s` al final de la lista |
| `socios.remove(s)` | Saca `s` de la lista |
| `socios.size()` | Cuántos elementos tiene |
| `socios.get(0)` | Devuelve el elemento en la posición 0 (¡se cuenta desde 0!) |
| `socios.contains(x)` | `true` si `x` está en la lista |
| `socios.clear()` | Vacía la lista |

Para **recorrer** una lista (mirar sus elementos uno por uno) se usa `for`:

```java
// De MaterialBibliografico.java: cuenta cuántas copias están disponibles
int contador = 0;
for (Ejemplar ej : ejemplares) {   // "para cada ejemplar 'ej' de la lista 'ejemplares'..."
    if (ej.estaDisponible()) {      // "...si está disponible..."
        contador++;                 // "...sumá 1"
    }
}
return contador;
```

## 1.10 `null`: "no hay nada"

`null` significa que una variable **no apunta a ningún objeto**. Se usa para decir "todavía no" o "no se encontró".

- En `Prestamo`, `fechaDevolucionReal` vale `null` hasta que el socio devuelve el libro.
- `obtenerPrimerEjemplarDisponible()` devuelve `null` si no queda ninguna copia libre.

Por eso el código pregunta muchas veces `if (algo == null)` o `if (algo != null)`.

## 1.11 `private`, `protected`, `public`: quién puede tocar qué

| Palabra | Quién puede usarlo |
|---|---|
| `public` | Cualquiera, desde cualquier parte del programa |
| `private` | Solo el código de **esa misma clase** |
| `protected` | Esa clase y sus **clases hijas** (ver herencia) |

Los atributos casi siempre son `private`: nadie de afuera puede cambiarlos "a mano".
Para leerlos o cambiarlos se usan métodos especiales llamados **getters** y **setters**:

```java
// De Persona.java
public String getNombre() { return nombre; }                      // getter: LEER el dato
public void setNombre(String nombre) { this.nombre = nombre; }    // setter: CAMBIAR el dato
```

A esta idea de "esconder los datos y dar acceso controlado" se le llama **encapsulamiento**.
Ventaja: si un dato no debe cambiar nunca (como el número de legajo), simplemente no se escribe su setter.

## 1.12 `static`: algo que es UNO SOLO para toda la clase

Normalmente cada objeto tiene **sus propias** casillas: cada autor tiene su propio nombre.
Una variable `static` es distinta: hay **una sola** compartida por **todos** los objetos de la clase.

El proyecto lo usa para los **números automáticos** (IDs):

```java
// De Autor.java
private static int contadorAutores = 0;   // UNO SOLO para todos los autores

public Autor(String nombreCompleto, String nacionalidad) {
    contadorAutores++;                     // 0 → 1 (el siguiente autor lo verá en 1)
    this.idAutor = contadorAutores;        // este autor queda con id 1
    ...
}
```

Primer autor creado: el contador pasa de 0 a 1, su id es 1. Segundo autor: el contador pasa de 1 a 2, su id es 2.
Como el contador es compartido, cada autor nuevo "ve" cuánto quedó del anterior.

Un **método** `static` se usa sin necesitar un objeto, con el nombre de la clase:
`Autor.reiniciarContador();` o `GestorPersistencia.guardar(biblioteca);`.

`final` significa **"no se puede cambiar nunca"**. `static final` se usa para constantes, como los colores en `Estilo`.

## 1.13 Herencia: `extends`

A veces varias clases comparten muchas cosas. En lugar de escribir todo repetido, se escribe lo común
en una **clase padre** y las **clases hijas** lo **heredan** con `extends`.

En el proyecto: un `Empleado` y un `Socio` tienen DNI, nombre, teléfono y email. Eso se escribe una sola vez en `Persona`:

```java
public abstract class Persona { ... dni, nombre, telefono, email ... }

public class Empleado extends Persona { ... legajo, cargo, turno ... }
//                    ───────┬──────
//          "Empleado ES UNA Persona": tiene todo lo de Persona + lo suyo
```

### `super(...)`: llamar al constructor del padre

El hijo tiene que pedirle al padre que llene la parte heredada. Eso se hace con `super(...)`,
**siempre en la primera línea** del constructor:

```java
// De Empleado.java
public Empleado(String dni, String nombre, String telefono, String email, String cargo, String turno) {
    super(dni, nombre, telefono, email);  // "Persona, llená vos dni, nombre, teléfono y email"
    contadorLegajos++;
    this.legajo = contadorLegajos;
    this.cargo = cargo;                   // lo propio del empleado lo llena él
    this.turno = turno;
}
```

### `abstract`: clases que son solo un molde de moldes

Una clase `abstract` **no se puede usar con `new`**. Existe solo para que otras hereden de ella.
No tiene sentido crear "una Persona" a secas en este sistema: siempre es un Empleado o un Socio.

Un **método** `abstract` es un método **sin instrucciones**, solo con su "firma". Obliga a cada hija a escribirlo:

```java
// De Persona.java
public abstract String getRol();   // sin llaves, sin código: cada hija lo escribe a su manera
```

### `@Override`: "estoy escribiendo mi versión"

Cuando una hija escribe un método que vino del padre, se pone `@Override` arriba.
Es un aviso para el compilador: si te equivocás en el nombre, te avisa.

```java
// De Empleado.java
@Override
public String getRol() {
    return "Empleado (" + cargo + ")";
}
```

## 1.14 Interfaces: `implements`

Una **interfaz** es un **contrato**: una lista de métodos que una clase **promete tener**, sin decir cómo los hace.

```java
// De Prestable.java
public interface Prestable {
    boolean estaDisponible();
    void prestar();
    void devolver();
}
```

Una clase **firma el contrato** con `implements` y queda **obligada** a escribir esos tres métodos
(si falta uno, no compila):

```java
public class Ejemplar implements Prestable, Serializable { ... }
```

**Diferencia con la herencia:** con `extends` se hereda código ya hecho; con `implements` solo se hereda
la obligación. Una clase puede tener un solo padre (`extends`) pero cumplir varias interfaces (`implements A, B`).

## 1.15 Polimorfismo: misma pregunta, distinta respuesta

"Polimorfismo" significa "muchas formas". Es cuando se llama al **mismo método** y cada objeto responde
**a su manera**, sin que el que pregunta tenga que saber de qué tipo es.

Ejemplo real: la `Biblioteca` necesita saber cuántos libros puede llevarse un socio:

```java
int limiteMaximo = socio.getCantidadMaximaLibros(configuracion);
```

- Si `socio` es un `SocioEstudiante`, responde 3.
- Si es un `SocioDocente`, responde 5.
- Si es un `SocioGeneral`, responde 2.

La Biblioteca **no pregunta** "¿sos estudiante?", "¿sos docente?". Solo llama al método y cada objeto sabe qué contestar.
Si mañana se agrega un `SocioJubilado`, la Biblioteca no cambia: solo se escribe la clase nueva.

## 1.16 `enum`: una lista cerrada de opciones

Un `enum` es un tipo de dato que **solo puede tomar ciertos valores fijos**.

```java
// De EstadoPrestamo.java
public enum EstadoPrestamo {
    ACTIVO("Activo"),
    DEVUELTO("Devuelto"),
    VENCIDO("Vencido");
    ...
}
```

Un préstamo solo puede estar `ACTIVO`, `DEVUELTO` o `VENCIDO`. No se puede escribir `"Vensido"` por error:
el compilador no lo aceptaría. Se compara con `==`: `if (p.getEstado() == EstadoPrestamo.VENCIDO)`.
El texto entre paréntesis (`"Activo"`) es una versión linda para mostrar en pantalla.

## 1.17 Excepciones: avisar que algo salió mal

Una **excepción** es la forma que tiene Java de decir "no puedo seguir, pasó algo".

- **`throw`** ("lanzar"): crea el aviso de error y **corta el método ahí mismo**.
- **`throws`** en la firma de un método: avisa que ese método **puede** lanzar ese error.
- **`try` / `catch`** ("intentar" / "atrapar"): quien llama al método intenta hacerlo y, si salta el error, lo atrapa y decide qué hacer.

```java
// En Biblioteca.java (el que lanza)
public Prestamo realizarPrestamo(...) throws OperacionInvalidaException {
    if (tieneMultasPendientes(socio)) {
        throw new OperacionInvalidaException("El socio tiene multas pendientes de pago...");
        // acá el método TERMINA; lo de abajo no se ejecuta
    }
    ...
}

// En quien lo usa (el que atrapa)
try {
    biblioteca.realizarPrestamo(socio, libro, empleado);
    // si no hubo error, sigue por acá
} catch (OperacionInvalidaException e) {
    // si hubo error, salta acá; e.getMessage() tiene el texto del motivo
    Estilo.mostrarError(this, e.getMessage());
}
```

## 1.18 Paquetes e `import`

Un **paquete** es una carpeta que agrupa clases. La primera línea de cada archivo dice en qué paquete está:

```java
package biblioteca.modelo;   // este archivo está en la carpeta src/biblioteca/modelo
```

Para usar una clase de **otro** paquete hay que avisarlo con `import` al principio del archivo:

```java
import biblioteca.modelo.Socio;      // traigo la clase Socio
import biblioteca.modelo.*;          // el * = traigo TODAS las clases de ese paquete
import java.util.ArrayList;          // también se importan clases que vienen con Java
```

## 1.19 `Serializable`: poder guardar un objeto en un archivo

Los objetos viven en la **memoria RAM**: cuando el programa se cierra, desaparecen.
`Serializable` es una interfaz especial (sin métodos) que le da **permiso** a Java para convertir
un objeto en bytes y escribirlo en un archivo, y después reconstruirlo. Por eso todas las clases del
modelo dicen `implements Serializable`.

---

# PARTE 2 — El proyecto archivo por archivo

## 2.1 Mapa general

```
SistemaBiblioteca/
├── src/biblioteca/
│   ├── Main.java                 → Donde arranca el programa
│   ├── modelo/                   → Las "cosas" de una biblioteca (19 clases)
│   ├── servicio/Biblioteca.java  → El "cerebro": las reglas
│   ├── multas/                   → Las formas de calcular multas
│   ├── persistencia/             → Guardar, cargar y datos de ejemplo
│   ├── excepciones/              → El tipo de error del sistema
│   └── gui/                      → Lo que se ve en pantalla
├── GUIA.md                       → Resumen corto del proyecto
├── EXPLICACION_DESDE_CERO.md     → Este documento
└── informes/                     → Un informe por cada cambio
```

La idea que organiza todo es **separar responsabilidades**:

```
  gui (pantallas)  ─── le pide cosas a ───►  Biblioteca (reglas)  ─── usa ───►  modelo (datos)
        ▲                                          │
        └─────── si no se puede, le avisa con ─────┘
                 una OperacionInvalidaException
```

La pantalla **nunca** decide si algo está permitido. Le pide a `Biblioteca`, y `Biblioteca` contesta
"listo" o lanza una excepción explicando por qué no.

## 2.2 Paquete `modelo`: las cosas de la biblioteca

### Las personas

```
            Persona  (abstracta)
            dni, nombre, telefono, email
             │
     ┌───────┴────────┐
  Empleado          Socio  (abstracta)
  legajo, cargo,    nroSocio, activo
  turno              │
          ┌──────────┼───────────┐
   SocioEstudiante SocioDocente SocioGeneral
   carrera         departamento ocupacion
```

**`Persona.java`** — Clase `abstract` con lo que tiene cualquier persona: `dni`, `nombre`, `telefono`, `email`.
Los atributos son `protected` para que las hijas puedan usarlos directamente.
Tiene el método abstracto `getRol()`: cada hija dice qué rol cumple.

**`Empleado.java`** — `extends Persona`. Agrega `legajo` (número automático desde 101), `cargo` y `turno`.
No tiene `setLegajo`, a propósito: el legajo no se puede cambiar.

**`Socio.java`** — `extends Persona`, también `abstract`. Agrega `nroSocio` (automático desde 1001)
y `activo` (si está habilitado). Declara 4 métodos abstractos que cada tipo de socio responde distinto:

| Método | Estudiante | Docente | General |
|---|---|---|---|
| `getDiasMaximoPrestamo(config)` | 7 días | 15 días | 5 días |
| `getCantidadMaximaLibros(config)` | 3 | 5 | 2 |
| `getTipoSocio()` | "Estudiante" | "Docente" | "General" |
| `getDetalleExtra()` | "Carrera: …" | "Depto: …" | "Ocupación: …" |

Los números **no están escritos en cada clase**: cada una se los pide a `ConfiguracionBiblioteca`.
Por ejemplo, en `SocioEstudiante`:

```java
@Override
public int getDiasMaximoPrestamo(ConfiguracionBiblioteca config) {
    return config.getDiasPrestamoEstudiante();   // "configuración, ¿cuántos días le tocan a un estudiante?"
}
```

**`SocioEstudiante.java`, `SocioDocente.java`, `SocioGeneral.java`** — Las tres son iguales en forma:
heredan de `Socio`, agregan un dato propio (carrera, departamento u ocupación) y responden los 4 métodos de la tabla.

### Los materiales y sus copias

```
   MaterialBibliografico (abstracta) ◇────── Autor
   idMaterial, titulo, anio, genero,          (agregación: el autor
   ubicacion, sinopsis, rutaImagen             existe por su cuenta)
      │                 ◆
      │                 └────── Ejemplar  (composición: las copias
   ┌──┼───────┐                  1 o más   nacen DENTRO del material)
 Libro Revista Tesis
```

**`Autor.java`** — Nombre, nacionalidad y un id automático (desde 1).

**`MaterialBibliografico.java`** — Clase `abstract`. Representa **la obra** (por ejemplo, "Rayuela"),
no una copia física. Tiene título, autor, año, género, ubicación en la estantería, sinopsis, ruta de la imagen
de portada y **una lista de ejemplares**. Su id es automático desde 101.

Lo más interesante es su constructor: recibe cuántas copias tiene y **las fabrica él mismo**:

```java
this.ejemplares = new ArrayList<>();                 // lista vacía
for (int i = 0; i < cantidadEjemplaresIniciales; i++) {
    this.ejemplares.add(new Ejemplar(this));         // crea una copia y la agrega
}
```

Este `for` es distinto al de la sección 1.9: es un `for` **con contador**. Se lee:
"arrancá con `i` en 0; mientras `i` sea menor que la cantidad, repetí; al final de cada vuelta sumale 1 a `i`".
Si la cantidad es 3, se repite 3 veces (i = 0, 1, 2) y se crean 3 ejemplares.
`new Ejemplar(this)` le pasa a la copia **el material actual** (`this`), para que la copia sepa a qué obra pertenece.

Métodos útiles:
- `getCantidadDisponibles()` → cuenta las copias libres.
- `obtenerPrimerEjemplarDisponible()` → devuelve la primera copia libre, o `null` si no hay.
- `agregarNuevoEjemplar()` → suma una copia más.
- `getTipoMaterial()` y `getIdentificadorUnico()` → abstractos, cada hija responde.

**`Libro.java`, `Revista.java`, `Tesis.java`** — Heredan de `MaterialBibliografico` y agregan lo suyo:

| Clase | Datos propios | `getIdentificadorUnico()` devuelve |
|---|---|---|
| `Libro` | isbn, editorial, numeroPaginas | "ISBN: … \| Edit: …" |
| `Revista` | issn, numeroEdicion, periodicidad | "ISSN: … \| Ed. #… (Mensual)" |
| `Tesis` | directorTesis, facultad, gradoAcademico | "Ingeniería - Facultad… (Dir: …)" |

**`Prestable.java`** — La interfaz-contrato: `estaDisponible()`, `prestar()`, `devolver()`.

**`Ejemplar.java`** — **Una copia física**. Si hay 3 copias de "Rayuela", hay 1 `Libro` y 3 `Ejemplar`.
**Lo que se presta es el ejemplar**, no la obra. Tiene:
- `codigoInventario`: "EJ-1001", "EJ-1002"… (automático).
- `material`: a qué obra pertenece.
- `estado`: un `EstadoEjemplar`.
- `observacion`: una nota, por ejemplo "Tapa rayada".

Cumple el contrato `Prestable`:

```java
public boolean estaDisponible() { return this.estado == EstadoEjemplar.DISPONIBLE; }
public void prestar()  { if (estaDisponible()) { this.estado = EstadoEjemplar.PRESTADO; } }
public void devolver() { this.estado = EstadoEjemplar.DISPONIBLE; }
```

**`EstadoEjemplar.java`** — `enum` con: `DISPONIBLE`, `PRESTADO`, `EN_REPARACION`, `EXTRAVIADO`.

### Préstamos, multas y reservas

**`Prestamo.java`** — Registra que **un socio** se llevó **un ejemplar**, **quién lo atendió** y **las fechas**.

| Atributo | Qué guarda |
|---|---|
| `idPrestamo` | Número automático (desde 1) |
| `socio` | Quién se lo llevó |
| `ejemplar` | Qué copia se llevó |
| `empleadoResponsable` | Quién lo atendió (puede ser `null`) |
| `fechaPrestamo` | Cuándo se lo llevó |
| `fechaVencimiento` | Hasta cuándo puede tenerlo |
| `fechaDevolucionReal` | Cuándo lo devolvió (`null` mientras no lo devuelva) |
| `cantidadRenovaciones` | Cuántas veces se extendió el plazo |

Tiene **dos constructores** (Java permite varios si reciben datos distintos):

```java
// Constructor 1: el préstamo empieza hoy y dura "diasPrestamo" días
public Prestamo(Socio socio, Ejemplar ejemplar, Empleado empleadoResponsable, int diasPrestamo) {
    this(socio, ejemplar, empleadoResponsable, LocalDate.now(), LocalDate.now().plusDays(diasPrestamo));
}

// Constructor 2: las fechas se eligen a mano
public Prestamo(Socio socio, Ejemplar ejemplar, Empleado empleadoResponsable,
                LocalDate fechaPrestamo, LocalDate fechaVencimiento) {
    contadorPrestamos++;
    this.idPrestamo = contadorPrestamos;
    ... (guarda cada dato) ...
    this.ejemplar.prestar();   // la copia pasa a PRESTADO
}
```

`this(...)` (con paréntesis) significa **"llamá a mi otro constructor"**. El constructor 1 calcula las fechas
(`LocalDate.now()` = hoy; `.plusDays(7)` = dentro de 7 días) y le pasa todo al constructor 2,
así el código para guardar los datos está escrito una sola vez.

El **estado no se guarda**: se **calcula** cada vez en `getEstado()` (está explicado en la sección 1.8).
Así nunca puede quedar desactualizado: un préstamo pasa de ACTIVO a VENCIDO solo, cuando cambia el día.

`getDiasAtraso()` cuenta los días entre el vencimiento y la devolución (o hoy, si todavía no lo devolvió).
Si no hay atraso, devuelve 0.

**`EstadoPrestamo.java`** — `enum` con `ACTIVO`, `DEVUELTO`, `VENCIDO`.

**`Multa.java`** — Se crea cuando alguien devuelve tarde. Guarda el préstamo, el socio, los días de atraso,
el monto, la fecha en que se generó, si está pagada y cuándo se pagó. `pagar()` la marca como pagada con la fecha de hoy.
**El monto no lo calcula la multa**: llega ya calculado (ver paquete `multas`).

**`Reserva.java`** — Lista de espera. Si un socio quiere un material sin copias libres, queda anotado.
`activa` pasa a `false` cuando finalmente se le presta. El método `esActivaDe(socio, material)` responde
"¿esta reserva sigue pendiente y es de este socio para este material?".

**`ConfiguracionBiblioteca.java`** — Todas las **reglas que se pueden cambiar**, juntas en un solo lugar:

| Regla | Valor inicial |
|---|---|
| Multa por día de atraso | $500 |
| Tope máximo de una multa | $15.000 |
| Estudiante: días / máximo de libros | 7 días / 3 |
| Docente: días / máximo de libros | 15 días / 5 |
| General: días / máximo de libros | 5 días / 2 |

Además tiene listas de géneros de libros, cargos y turnos de empleados, para las listas desplegables.

## 2.3 Paquete `multas`: cómo se calcula cuánto cobrar

**`PoliticaMulta.java`** — Interfaz con `calcularMonto(diasAtraso, config)`, `getNombrePolitica()` y `getExplicacion(config)`.

Hay dos clases que la cumplen:

**`MultaFijaDiaria.java`** — días × precio por día, sin pasarse del tope.
Ejemplo: 6 días × $500 = **$3.000**.

**`MultaProgresiva.java`** — los primeros 5 días a precio normal; desde el 6° cada día cuesta 50% más (×1.5).
Ejemplo con 8 días: (5 × 500) + (3 × 500 × 1.5) = 2.500 + 2.250 = **$4.750**.

¿Para qué una interfaz? La `Biblioteca` guarda un atributo `PoliticaMulta politicaMulta` y llama
`politicaMulta.calcularMonto(...)` sin saber cuál de las dos es. Para cambiar la forma de cobrar,
se cambia el objeto con `setPoliticaMulta(new MultaProgresiva())` y **no se toca nada más**.
(A esta idea se le llama *patrón Strategy*.)

## 2.4 Paquete `excepciones`

**`OperacionInvalidaException.java`** — Es el único tipo de error propio del sistema.
`extends Exception` = es una excepción de Java. Solo tiene un constructor que recibe el mensaje y se lo pasa al padre con `super(mensaje)`.
Ver sección 1.17 para entender cómo se lanza y se atrapa.

## 2.5 Paquete `servicio`: `Biblioteca.java`, el cerebro

Es la clase más importante. Guarda **todas las listas** y tiene **todas las reglas**.

```java
private ConfiguracionBiblioteca configuracion;   // las reglas editables
private PoliticaMulta politicaMulta;             // cómo se cobran multas
private List<MaterialBibliografico> catalogo;    // todos los materiales
private List<Autor> autores;
private List<Socio> socios;
private List<Empleado> empleados;
private List<Prestamo> prestamos;
private List<Multa> multas;
private List<Reserva> reservas;
private List<String> historialActividad;         // bitácora: "[30/10/2026 14:05] Préstamo #6..."
```

Sus métodos están agrupados en 6 secciones:

| Sección | Métodos | Reglas que aplica |
|---|---|---|
| 1. Autores y catálogo | `buscarOCrearAutor`, `agregarMaterial`, `eliminarMaterial` | No se borra un material con copias prestadas. No se repiten autores. |
| 2. Socios | `registrarSocio`, `eliminarSocio` | DNI no repetido. Solo se da de baja si no debe libros ni multas. |
| 3. Empleados | `registrarEmpleado`, `eliminarEmpleado` | DNI no repetido. |
| 4. Préstamos | `realizarPrestamo`, `registrarDevolucion`, `renovarPrestamo`, `contarPrestamosActivosDeSocio`, `tieneMultasPendientes` | Ver Parte 3. Máximo 2 renovaciones. |
| 5. Reservas | `registrarReserva`, `buscarReservaPendienteParaMaterial` | Solo si no hay copias libres. Sin reservas repetidas. |
| 6. Multas | `pagarMulta`, `calcularTotalMultasPendientes`, `calcularTotalMultasCobradas`, `generarComprobantePrestamo` | No se paga dos veces la misma multa. |

Y dos métodos de mantenimiento:
- `vaciarTodo()` → borra todas las listas y pone los contadores de IDs en su valor inicial.
- `sincronizarContadores()` → al cargar desde el disco, corrige los contadores (ver 2.6).

Cada operación importante llama a `registrarActividad("...")`, que agrega una línea con fecha y hora
**al principio** del historial (`historialActividad.add(0, ...)`: el 0 significa "en la posición 0", o sea arriba de todo).

## 2.6 Paquete `persistencia`: guardar y cargar

**`GestorPersistencia.java`** — Todos sus métodos son `static`.

- `guardar(biblioteca)` → escribe **todo el objeto Biblioteca** (con todas sus listas y todos los objetos que contienen) en el archivo `biblioteca_datos.dat`. Usa `ObjectOutputStream`, la herramienta de Java para serializar.
- `cargar()` → si el archivo existe, lo lee con `ObjectInputStream` y reconstruye la Biblioteca. Si no existe (primera vez) o falla, crea una con datos de ejemplo.
- `exportarReporteCSV(...)` → escribe un archivo de texto con columnas separadas por `;` que se abre en Excel.
- `guardarTextoEnArchivo(...)` → guarda un texto (por ejemplo, el comprobante de un préstamo) en un `.txt`.

Usa `try ( ... ) { ... }` (con paréntesis después del `try`): se llama *try-with-resources* y
**cierra el archivo automáticamente** al terminar, aunque haya un error.

> **¿Por qué existe `sincronizarContadores()`?** Los contadores `static` **no se guardan** en el archivo.
> Al abrir el programa vuelven a su valor inicial (por ejemplo, préstamos a 0). Si ya había 5 préstamos guardados
> y no se corrigiera el contador, el próximo préstamo nuevo sería otra vez el #1. `sincronizarContadores()` recorre
> todo lo cargado y deja cada contador en el número más alto que existe, para que el siguiente siga desde ahí.

**`DatosDeEjemplo.java`** — Crea una biblioteca ya cargada para que el programa no arranque vacío:
3 empleados, 8 autores, 8 materiales (5 libros, 2 revistas, 1 tesis), 5 socios, 5 préstamos
(2 activos, 1 vencido, 2 devueltos tarde), 2 multas (una pendiente y una pagada) y 1 reserva.
**Es el mejor archivo para empezar a leer**: muestra cómo se crea y se conecta cada objeto.

## 2.7 Paquete `gui`: lo que se ve

GUI = *Graphical User Interface* (interfaz gráfica). Usa **Swing**, la biblioteca de Java para hacer ventanas.

Piezas de Swing que aparecen:

| Pieza | Qué es |
|---|---|
| `JFrame` | Una ventana |
| `JPanel` | Un rectángulo donde se acomodan otras piezas |
| `JLabel` | Un texto (o imagen) fijo |
| `JButton` | Un botón |
| `JTextField` | Un campo para escribir |
| `JTable` | Una tabla |
| `BorderLayout` | Acomoda en 5 zonas: arriba (NORTH), abajo (SOUTH), izquierda (WEST), derecha (EAST) y centro (CENTER) |
| `GridLayout` | Acomoda en una grilla de casillas iguales |

**`Estilo.java`** — Caja de herramientas visual: colores (`COLOR_PRIMARIO`, `COLOR_PELIGRO`…), fuentes,
y métodos para crear botones, campos y tablas con el mismo aspecto. También formatea dinero (`3000.0` → `$ 3.000,00`)
y muestra carteles de aviso.

**`Actualizable.java`** — Interfaz con un solo método: `actualizarDatos()`. La cumplen los paneles que muestran
datos, para que la ventana pueda decirles "refrescate" cuando algo cambia.

**`TarjetaEstadistica.java`** — Un `JPanel` con un título, un número grande y un subtítulo (por ejemplo "SOCIOS / 5 / Registrados").

**`GraficoBarras.java`** — Un `JPanel` que **dibuja a mano** un gráfico de barras. Swing llama solo a su método
`paintComponent(Graphics g)` cada vez que hay que dibujarlo; ahí se dibujan rectángulos (`fillRoundRect`) y textos (`drawString`).
Cada barra mide en proporción al valor más grande (una regla de tres).

**`PanelInicio.java`** — La pantalla principal: título arriba, 4 tarjetas (materiales, socios, préstamos, multas),
el gráfico a la izquierda y un texto de bienvenida a la derecha. Su `actualizarDatos()` cuenta todo en la Biblioteca
y actualiza tarjetas y gráfico.

## 2.8 `Main.java`: donde arranca todo

Cuando se ejecuta el programa, Java busca un método con esta forma exacta y empieza por ahí:

```java
public static void main(String[] args) { ... }
```

Lo que hace `Main`:

```java
SwingUtilities.invokeLater(() -> {
    Biblioteca biblioteca = GestorPersistencia.cargar();     // 1. cargar los datos

    JFrame ventana = new JFrame("Sistema de Biblioteca");    // 2. crear la ventana
    ventana.setSize(1000, 620);                              //    tamaño en píxeles
    ventana.setLocationRelativeTo(null);                     //    centrada
    ventana.add(new PanelInicio(biblioteca));                //    con el panel adentro

    ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    ventana.addWindowListener(new WindowAdapter() {          // 3. al cerrar la ventana...
        @Override
        public void windowClosing(WindowEvent e) {
            GestorPersistencia.guardar(biblioteca);          //    ...guardar
            System.exit(0);                                  //    ...y terminar el programa
        }
    });

    ventana.setVisible(true);                                // 4. mostrarla
});
```

- `() -> { ... }` se llama **lambda**: es una forma corta de escribir "un pedazo de código para ejecutar después".
  `invokeLater` lo ejecuta en el momento correcto para Swing (Swing exige crear las ventanas en su propio "hilo").
- `addWindowListener(...)` le dice a la ventana: "cuando pase algo (como que la cierren), avisale a este objeto".
- `new WindowAdapter() { ... }` es una **clase anónima**: una clase sin nombre creada en el momento,
  solo para escribir qué hacer en `windowClosing`.

---

# PARTE 3 — Qué pasa, paso a paso

## 3.1 Al abrir el programa

1. Java ejecuta `Main.main(...)`.
2. `GestorPersistencia.cargar()`:
   - ¿Existe `biblioteca_datos.dat`? → lo lee y llama a `sincronizarContadores()`.
   - ¿No existe? → `DatosDeEjemplo.crearBibliotecaDeEjemplo()` y lo guarda.
3. Se crea la ventana con el `PanelInicio`.
4. El constructor de `PanelInicio` arma tarjetas y gráfico, y llama a `actualizarDatos()` para llenarlos con los números reales.
5. Se muestra la ventana.

## 3.2 Al prestar un libro: `biblioteca.realizarPrestamo(socio, material, empleado)`

```
 ¿El socio está activo? ──── no ───► throw "se encuentra suspendido"      (FIN)
        │ sí
 ¿Tiene multas sin pagar? ── sí ───► throw "tiene multas pendientes"      (FIN)
        │ no
 ¿Llegó a su máximo? ─────── sí ───► throw "alcanzó su límite máximo"     (FIN)
   (el máximo lo contesta cada tipo de socio: polimorfismo)
        │ no
 ¿Queda alguna copia libre? ─ no ──► throw "no hay ejemplares disponibles" (FIN)
        │ sí
 new Prestamo(...)  → la copia pasa a PRESTADO
 se agrega a la lista de préstamos
 si el socio tenía reserva de ese material → se marca cumplida
 se anota en el historial
 se devuelve el préstamo creado
```

## 3.3 Al devolver: `biblioteca.registrarDevolucion(prestamo)`

1. Si ya estaba devuelto → `throw` "Este préstamo ya fue devuelto".
2. `prestamo.registrarDevolucion()` → guarda la fecha de hoy y la copia vuelve a DISPONIBLE.
3. Calcula `diasAtraso`.
4. Si hay atraso → `politicaMulta.calcularMonto(...)` calcula el monto, se crea una `Multa`, se agrega a la lista y se devuelve.
5. Si no hay atraso → se devuelve `null` ("no hubo multa").

## 3.4 Al cerrar la ventana

Se ejecuta `windowClosing` → `GestorPersistencia.guardar(biblioteca)` → todo queda en `biblioteca_datos.dat`.

---

# Glosario

| Palabra | Significado simple |
|---|---|
| **clase** | Molde que describe cómo es una cosa |
| **objeto** | Una cosa concreta hecha con el molde |
| **atributo** | Un dato que guarda cada objeto |
| **método** | Una acción que el objeto sabe hacer |
| **parámetro** | Un dato que se le pasa a un método entre paréntesis |
| **`return`** | Termina el método y entrega un resultado |
| **`void`** | "Este método no devuelve nada" |
| **constructor** | Método especial que se ejecuta al hacer `new`; llena los atributos |
| **`new`** | Fabrica un objeto nuevo |
| **`this`** | "Este objeto" |
| **`this(...)`** | Llamar a otro constructor de la misma clase |
| **`super(...)`** | Llamar al constructor de la clase padre |
| **`=`** | Guardar un valor |
| **`==` / `!=`** | ¿Son iguales? / ¿Son distintos? |
| **`!`** | "No" |
| **`&&` / `\|\|`** | "Y" / "O" |
| **`++`** | Sumar 1 |
| **`if` / `else`** | Si… / Si no… |
| **`for`** | Repetir (para cada elemento, o un número de veces) |
| **`null`** | "No hay nada" |
| **`List` / `ArrayList`** | Lista de objetos |
| **`public` / `private` / `protected`** | Quién puede usar algo: todos / solo la clase / la clase y sus hijas |
| **getter / setter** | Método para leer / cambiar un atributo privado |
| **encapsulamiento** | Esconder los datos y dar acceso controlado |
| **`static`** | Uno solo para toda la clase (no uno por objeto) |
| **`final`** | No se puede cambiar |
| **`extends`** | Heredar de una clase |
| **herencia** | Una clase hija recibe todo lo de su clase padre |
| **`abstract`** | Clase que no se puede crear con `new` / método sin código que las hijas deben escribir |
| **`@Override`** | "Estoy escribiendo mi versión de un método heredado" |
| **`interface`** | Contrato: lista de métodos que una clase promete tener |
| **`implements`** | Firmar (cumplir) una interfaz |
| **polimorfismo** | El mismo método responde distinto según el objeto |
| **`enum`** | Lista cerrada de valores posibles |
| **excepción** | Aviso de que algo salió mal |
| **`throw` / `throws`** | Lanzar una excepción / avisar que un método puede lanzarla |
| **`try` / `catch`** | Intentar algo / atrapar el error si ocurre |
| **`package`** | Carpeta donde está la clase |
| **`import`** | Traer una clase de otro paquete |
| **`Serializable`** | Permiso para guardar el objeto en un archivo |
| **composición** | Una parte que nace y muere con el todo (Material → Ejemplar) |
| **agregación** | Una parte que existe por su cuenta (Material → Autor) |
| **lambda (`() -> {}`)** | Pedazo de código que se pasa para ejecutar después |
| **clase anónima** | Clase sin nombre creada en el momento para cambiar un método |
| **compilar** | Traducir los `.java` a `.class` (`javac`) |
| **Swing** | La biblioteca de Java para hacer ventanas |
