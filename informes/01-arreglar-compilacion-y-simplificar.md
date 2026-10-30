# Informe 01 — Arreglar la compilación, poder ejecutar el programa y simplificar el código

| | |
|---|---|
| **Fecha** | 30/10/2026 |
| **Commit** | `38b88b9` — "Hacer el código más fácil de entender y arreglar la compilación" |
| **Rama de GitHub** | `version-local` |
| **Para quién es** | Cualquier persona, aunque no sepa nada de Java |

> Este informe se puede leer **solo**, sin haber leído nada antes.
> Primero hay una sección con lo mínimo de Java necesario para entenderlo.
> Después se explica cada cambio: **qué había antes, qué hay ahora y por qué**.

---

## Resumen en 30 segundos

Antes de este cambio, el proyecto **no se podía ni compilar ni abrir**. Ahora:

1. **Compila.** Se arreglaron dos errores: un archivo con tilde en el nombre y un archivo vacío.
2. **Se puede abrir.** Se agregó `Main.java`, el archivo por donde arranca el programa.
3. **Se arregló un error escondido.** Al reabrir el programa, algunos números de préstamo y multa se repetían.
4. **Es más fácil de leer.** Se simplificaron algunas partes y se agregaron explicaciones en todos los archivos.

---

## Parte A — Lo mínimo de Java para entender este informe

### ¿Qué es compilar?

En Java se escribe el programa en archivos de texto que terminan en `.java`. La computadora no los entiende tal cual:
primero un programa llamado **compilador** (`javac`) los revisa y los traduce a archivos `.class`.
Si hay **cualquier** error de escritura, el compilador **se niega** a traducir y el programa no puede abrirse.

### Clase y objeto

- Una **clase** es un **molde**. Ejemplo: la clase `Prestamo` describe qué datos tiene un préstamo (quién, qué libro, fechas).
- Un **objeto** es **una cosa concreta** hecha con ese molde. Ejemplo: "el préstamo #3, de Lucas, del libro Rayuela".
- Cada clase se escribe en su propio archivo. **Regla de Java:** el archivo tiene que llamarse **exactamente igual** que la clase.
  La clase `Prestamo` va en `Prestamo.java`.

### Atributos

Son los datos que guarda cada objeto. Se escriben así:

```java
private int idPrestamo;
```

Se lee: "cada préstamo tiene un dato **privado** (solo lo toca su propia clase), de tipo **número entero** (`int`), llamado `idPrestamo`".
El `;` marca el fin de la instrucción, como un punto al final de una oración.

### Métodos

Son las acciones que sabe hacer un objeto. Ejemplo:

```java
public void pagar() {
    this.pagada = true;
}
```

- `public` = cualquiera puede usarlo.
- `void` = no devuelve ningún resultado.
- `pagar` = el nombre.
- Lo que está entre `{ }` son las instrucciones que se ejecutan.

Si un método **devuelve** un resultado, en lugar de `void` se pone el tipo del resultado y se usa `return` para entregarlo.

### Constructor y `new`

Para crear un objeto se escribe `new`:

```java
Prestamo p = new Prestamo(socio, ejemplar, empleado, 7);
```

Al hacer `new`, Java ejecuta un método especial llamado **constructor**: tiene **el mismo nombre que la clase**
y su trabajo es llenar los datos del objeto nuevo.

### `this`

Dentro de una clase, `this` significa **"este objeto"**. `this.idPrestamo` es "el `idPrestamo` de este préstamo".

### `=`, `==` y `++`

- `x = 5;` → "guardá 5 en `x`".
- `x == 5` → "¿`x` es igual a 5?" (da verdadero o falso).
- `x++;` → "sumale 1 a `x`".

### `if` / `else`

```java
if (condicion) {
    // se hace si la condición es verdadera
} else {
    // se hace si es falsa
}
```

### `for` (repetir para cada elemento de una lista)

```java
for (Reserva r : reservas) {
    // esto se repite una vez por cada reserva "r" de la lista "reservas"
}
```

### `static`: algo que es uno solo para toda la clase

Normalmente cada objeto tiene sus propios datos (cada préstamo tiene su propia fecha).
Una variable `static` es **una sola, compartida por todos los objetos de la clase**.
El proyecto la usa como **contador** para dar números automáticos: préstamo #1, #2, #3…

### `null`

Significa **"no hay nada"**. Por ejemplo, la fecha de devolución vale `null` mientras el libro no se devolvió.

---

## Parte B — Los cambios, uno por uno

### Cambio 1 — Renombrar `MaterialBibliográfico.java` → `MaterialBibliografico.java`

**Qué había antes:** el archivo se llamaba `MaterialBibliográfico.java` (con tilde en la "a"), pero adentro la clase se llamaba:

```java
public abstract class MaterialBibliografico { ... }   // sin tilde
```

**Qué problema causaba:** la regla de Java dice que una clase `public` debe estar en un archivo con **exactamente** el mismo nombre.
"Bibliográfico" y "Bibliografico" son distintos para la computadora. El compilador daba este error:

```
error: class MaterialBibliografico is public, should be declared in a file named MaterialBibliografico.java
```

**Qué hay ahora:** el archivo se llama `MaterialBibliografico.java`, sin tilde. El contenido no cambió.

> **Consejo:** en nombres de archivos y clases conviene **no usar tildes ni ñ**, para evitar este tipo de problemas.

---

### Cambio 2 — `Ejemplar.java` estaba vacío

**Qué había antes:** el archivo existía pero **no tenía nada escrito** (0 bytes).

**Qué problema causaba:** muchas otras clases usan `Ejemplar` (los materiales tienen ejemplares, los préstamos prestan ejemplares).
Como la clase no existía, el compilador daba **18 errores** del tipo `cannot find symbol` ("no encuentro esto").

**Qué hay ahora:** `Ejemplar.java` tiene la clase completa (la escribió el autor del proyecto durante este cambio).
Un **ejemplar** es **una copia física** de un material: si hay 3 copias del libro "Rayuela", hay 1 libro y 3 ejemplares.

Sus datos:

```java
private static int contadorEjemplares = 1000;   // contador compartido: EJ-1001, EJ-1002...
private String codigoInventario;                // ej: "EJ-1001"
private MaterialBibliografico material;         // a qué libro/revista/tesis pertenece
private EstadoEjemplar estado;                  // DISPONIBLE, PRESTADO, EN_REPARACION o EXTRAVIADO
private String observacion;                     // una nota, ej: "Tapa rayada"
```

Y cumple el "contrato" `Prestable` (una **interfaz**: una lista de métodos que la clase se compromete a tener):

```java
public boolean estaDisponible() { return this.estado == EstadoEjemplar.DISPONIBLE; }  // ¿está libre?
public void prestar()  { if (estaDisponible()) { this.estado = EstadoEjemplar.PRESTADO; } } // marcarla prestada
public void devolver() { this.estado = EstadoEjemplar.DISPONIBLE; }                     // marcarla libre
```

---

### Cambio 3 — Nuevo archivo `Main.java`: el punto de arranque

**Qué había antes:** no existía. Ningún archivo tenía el método `main`.

**Qué problema causaba:** cuando se ejecuta un programa Java, la computadora busca un método con esta forma **exacta**:

```java
public static void main(String[] args)
```

y empieza a ejecutar desde ahí. Sin `main`, **no hay por dónde empezar** y el programa no se puede abrir.

**Qué hay ahora:** `src/biblioteca/Main.java`:

```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        Biblioteca biblioteca = GestorPersistencia.cargar();     // 1
        JFrame ventana = new JFrame("Sistema de Biblioteca");    // 2
        ventana.setSize(1000, 620);
        ventana.setLocationRelativeTo(null);
        ventana.getContentPane().setBackground(Estilo.COLOR_FONDO);
        ventana.add(new PanelInicio(biblioteca));

        ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // 3
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GestorPersistencia.guardar(biblioteca);
                System.exit(0);
            }
        });

        ventana.setVisible(true);                                // 4
    });
}
```

Explicación línea por línea:

| Línea | Qué hace |
|---|---|
| `SwingUtilities.invokeLater(() -> { ... });` | Swing (la parte de Java que hace ventanas) exige que las ventanas se creen en su propio "hilo" (su propia fila de tareas). `invokeLater` pone el código de adentro en esa fila. `() -> { ... }` se llama **lambda**: es una forma corta de escribir "este pedazo de código, para ejecutar después". |
| `GestorPersistencia.cargar()` | **Paso 1.** Carga los datos guardados del archivo `biblioteca_datos.dat`. Si no existe (primera vez), crea datos de ejemplo. |
| `new JFrame("Sistema de Biblioteca")` | **Paso 2.** Crea una ventana (`JFrame` = ventana) con ese título. |
| `setSize(1000, 620)` | La hace de 1000 píxeles de ancho y 620 de alto. |
| `setLocationRelativeTo(null)` | La centra en la pantalla. |
| `getContentPane().setBackground(...)` | Le pone el color de fondo del programa. |
| `add(new PanelInicio(biblioteca))` | Pone adentro de la ventana el Panel de Inicio (las tarjetas y el gráfico). |
| `setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)` | **Paso 3.** "Cuando aprieten la X, no cierres solo: dejame decidir qué hacer." |
| `addWindowListener(new WindowAdapter() { ... })` | "Cuando pase algo con la ventana, avisale a este objeto." `new WindowAdapter() { ... }` es una **clase anónima**: una clase sin nombre, creada en el momento solo para escribir qué hacer. |
| `windowClosing(...)` → `guardar(...)` y `System.exit(0)` | Al cerrar: **primero guarda** todo en el disco y **después** termina el programa. Así no se pierde nada. |
| `setVisible(true)` | **Paso 4.** Muestra la ventana. Hasta este momento existía pero estaba invisible. |

---

### Cambio 4 — Arreglar números repetidos en préstamos y multas (un error escondido)

Este es el cambio más importante para entender, porque había un **error de lógica**: el programa compilaba bien,
pero hacía algo incorrecto.

#### Cómo se generan los números automáticos

Cada clase que tiene número (préstamo #1, #2…) usa un **contador `static`**: una sola variable compartida por todos los objetos de esa clase.

#### Cómo estaba antes (en `Prestamo`, `Multa`, `Autor` y `Reserva`)

```java
private static int contadorPrestamos = 1;

public Prestamo(...) {
    this.idPrestamo = contadorPrestamos++;
}
```

`contadorPrestamos++` escrito **después** de usarlo significa: "primero usá el valor actual, **después** sumale 1".
Entonces el contador siempre guardaba **el número del PRÓXIMO préstamo**, no el del último.

| Préstamo creado | Recibe el número | El contador queda en |
|---|---|---|
| 1° | 1 | 2 |
| 2° | 2 | 3 |
| 5° | 5 | 6 |

Mientras tanto, otras clases (`Socio`, `Empleado`, `MaterialBibliografico`) lo hacían al revés: el contador guardaba
**el número del ÚLTIMO creado**. Había dos estilos mezclados.

#### El problema: al volver a abrir el programa

Los contadores `static` **no se guardan en el archivo**. Al abrir el programa de nuevo, el contador de préstamos vuelve a 1.
Para corregirlo existe el método `sincronizarContadores()`, que mira todos los préstamos cargados y pone el contador en el **número más alto que encuentra**:

```java
// lo que hace, en esencia:
if (idExistente > contadorPrestamos) {
    contadorPrestamos = idExistente;   // si el más alto es el #5, el contador queda en 5
}
```

Esto funciona bien para el estilo "el contador guarda el ÚLTIMO". Pero `Prestamo` usaba el estilo "el contador guarda el PRÓXIMO":
el contador quedaba en 5, y el siguiente préstamo nuevo **recibía el 5 otra vez**. ¡Dos préstamos con el número #5!
Lo mismo pasaba con las multas.

#### Cómo está ahora

**Todas** las clases usan el mismo estilo: el contador guarda **el número del último creado**.

```java
private static int contadorPrestamos = 0;      // todavía no se creó ninguno

public Prestamo(...) {
    contadorPrestamos++;                       // primero sumo 1 (0 → 1)
    this.idPrestamo = contadorPrestamos;       // y ese es el número de este préstamo
}

public static void reiniciarContador() {
    contadorPrestamos = 0;                     // "empezar de cero" vuelve a 0, no a 1
}
```

| Préstamo creado | El contador pasa a | Recibe el número |
|---|---|---|
| 1° | 0 → 1 | 1 |
| 2° | 1 → 2 | 2 |
| Se cierra y reabre el programa con 5 préstamos | `sincronizarContadores()` lo pone en 5 | — |
| 6° (nuevo) | 5 → 6 | **6** ✓ |

Clases modificadas: `Prestamo`, `Multa`, `Autor` y `Reserva`.
Además, `Autor` y `Reserva` no tenían el método `actualizarContadorSiEsMayor` y `sincronizarContadores()` no los revisaba:
se les agregó, así ahora **todas** las clases con número se corrigen al cargar.

**Cómo se comprobó:** se cargaron los datos de ejemplo (5 préstamos), se guardaron, se reinició el contador como si se reabriera el programa,
se volvió a cargar y se creó un préstamo nuevo. Recibió el **#6**. Con el código anterior hubiera recibido el #5 repetido.

---

### Cambio 5 — `Prestamo` tenía el mismo código escrito dos veces

**Qué había antes:** `Prestamo` tiene **dos constructores** (Java permite varios si reciben datos distintos):
uno para préstamos de hoy y otro para inventar préstamos del pasado (los usan los datos de ejemplo).
Los dos repetían las mismas 9 líneas:

```java
// Constructor 1
public Prestamo(Socio socio, Ejemplar ejemplar, Empleado empleadoResponsable, int diasPrestamo) {
    this.idPrestamo = contadorPrestamos++;
    this.socio = socio;
    this.ejemplar = ejemplar;
    this.empleadoResponsable = empleadoResponsable;
    this.fechaPrestamo = LocalDate.now();
    this.fechaVencimiento = this.fechaPrestamo.plusDays(diasPrestamo);
    this.fechaDevolucionReal = null;
    this.cantidadRenovaciones = 0;
    this.ejemplar.prestar();
}

// Constructor 2: casi lo mismo, cambian solo las fechas
public Prestamo(Socio socio, Ejemplar ejemplar, Empleado empleadoResponsable,
                LocalDate fechaPrestamo, LocalDate fechaVencimiento) {
    this.idPrestamo = contadorPrestamos++;
    this.socio = socio;
    ... (lo mismo otra vez) ...
}
```

**Qué problema causaba:** si alguien cambia algo en uno y se olvida del otro, los préstamos se comportan distinto según cómo se crearon.
Además, es el doble de código para leer.

**Qué hay ahora:** el constructor 1 calcula las fechas y **le pide al constructor 2 que haga el resto**:

```java
public Prestamo(Socio socio, Ejemplar ejemplar, Empleado empleadoResponsable, int diasPrestamo) {
    this(socio, ejemplar, empleadoResponsable, LocalDate.now(), LocalDate.now().plusDays(diasPrestamo));
}
```

- `this(...)` (con paréntesis) significa **"llamá a mi otro constructor con estos datos"**.
- `LocalDate.now()` = la fecha de hoy.
- `LocalDate.now().plusDays(diasPrestamo)` = hoy más esa cantidad de días.

Resultado: el código que guarda los datos está escrito **una sola vez**, en el constructor 2.

---

### Cambio 6 — Una condición larga repetida, ahora en un método con nombre

**Qué había antes:** en `Biblioteca.java`, en dos lugares distintos, había esta condición:

```java
if (r.isActiva() && r.getSocio().getNroSocio() == socio.getNroSocio()
        && r.getMaterial().getIdMaterial() == material.getIdMaterial()) {
```

`&&` significa "Y". Se lee: "si la reserva está activa **Y** es del mismo socio **Y** es del mismo material…".
Es larga, difícil de leer, y estaba escrita dos veces.

**Qué hay ahora:** se creó un método en `Reserva.java` que hace esa pregunta y tiene un nombre que explica qué pregunta:

```java
// Devuelve true si esta reserva sigue pendiente y es de ESE socio para ESE material
public boolean esActivaDe(Socio otroSocio, MaterialBibliografico otroMaterial) {
    return activa
            && socio.getNroSocio() == otroSocio.getNroSocio()
            && material.getIdMaterial() == otroMaterial.getIdMaterial();
}
```

- `boolean` = devuelve verdadero (`true`) o falso (`false`).
- `return activa && ... && ...` = devuelve `true` solo si las tres cosas se cumplen.

Y en `Biblioteca.java` ahora se lee casi como castellano:

```java
if (r.esActivaDe(socio, material)) {
```

---

### Cambio 7 — Reemplazar el operador `? :` por `if / else`

**Qué había antes:** en tres lugares se usaba el operador `? :` (se llama *ternario*), que es una forma comprimida de un `if`:

```java
// De Prestamo.getDiasAtraso()
LocalDate fechaReferencia = (fechaDevolucionReal != null) ? fechaDevolucionReal : LocalDate.now();
```

Se lee: "si `fechaDevolucionReal` no es `null`, usá `fechaDevolucionReal`; si no, usá hoy". Es correcto, pero cuesta leerlo si uno recién empieza.

**Qué hay ahora:** lo mismo, escrito con `if / else`:

```java
LocalDate fechaReferencia;
if (fechaDevolucionReal != null) {
    fechaReferencia = fechaDevolucionReal;   // si ya lo devolvió, cuento hasta ese día
} else {
    fechaReferencia = LocalDate.now();       // si no, cuento hasta hoy
}
```

Lugares cambiados: `Prestamo.getDiasAtraso()`, `Biblioteca.generarComprobantePrestamo()` y `GestorPersistencia.exportarReporteCSV()`.
**El resultado del programa es exactamente el mismo**; solo cambia cómo está escrito.

---

### Cambio 8 — `PanelInicio`: comparar estados de forma segura

**Qué había antes:**

```java
for (var p : biblioteca.getPrestamos()) {
    if (p.getEstado().name().equals("ACTIVO")) activos++;
    if (p.getEstado().name().equals("VENCIDO")) vencidos++;
}
```

Dos cosas eran confusas:
- `var` le deja a Java adivinar el tipo de `p`. Quien lee no sabe qué es `p` sin investigar.
- `.name().equals("ACTIVO")` convierte el estado en texto y compara textos. Si alguien escribe `"ACTIVOS"` por error, **el compilador no avisa** y el contador queda siempre en 0.

**Qué hay ahora:**

```java
for (Prestamo p : biblioteca.getPrestamos()) {
    if (p.getEstado() == EstadoPrestamo.ACTIVO) {
        activos++;
    }
    if (p.getEstado() == EstadoPrestamo.VENCIDO) {
        vencidos++;
    }
}
```

- `Prestamo p` dice claramente que `p` es un préstamo.
- `EstadoPrestamo` es un **`enum`**: una lista cerrada de valores (`ACTIVO`, `DEVUELTO`, `VENCIDO`). Si se escribe mal (`EstadoPrestamo.ACTIVOS`), **el compilador avisa enseguida**.
- Se agregaron llaves `{ }` a cada `if`, para que quede claro qué se ejecuta.

---

### Cambio 9 — Comentarios explicativos y `GUIA.md`

En **todos** los archivos se agregaron comentarios (líneas que empiezan con `//`, que la computadora ignora) explicando:
- Al principio de cada clase: **"¿Qué es esta clase?"** y para qué sirve.
- Junto a cada regla del negocio: qué regla es (por ejemplo "REGLA: no puede haber dos socios con el mismo DNI").
- Junto a cada palabra de Java poco obvia: qué significa (`super(...)`, `@Override`, `throws`, try-with-resources, clase anónima, etc.).

También se agregó `GUIA.md` con un resumen del proyecto y el orden recomendado para leer los archivos.

---

## Parte C — Cómo se verificó que todo funciona

| Prueba | Resultado |
|---|---|
| Compilar todo el proyecto | Sin errores (antes: 19 errores) |
| Cargar datos de ejemplo | 8 materiales, 5 socios, 5 préstamos, 2 multas ✓ |
| Multa fija con 6 días de atraso | $3.000 (6 × $500) ✓ |
| Multa progresiva con 8 días | $4.750 ((5 × 500) + (3 × 500 × 1,5)) ✓ |
| Guardar, reabrir y crear un préstamo | Recibe el #6, sin repetir ✓ |
| Prestarle a un socio con multas pendientes | El sistema lo impide con el mensaje "El socio tiene multas pendientes de pago…" ✓ |
| Abrir la ventana | Se abre "Sistema de Biblioteca" sin errores ✓ |

---

## Parte D — Archivos tocados

| Archivo | Tipo de cambio |
|---|---|
| `src/biblioteca/Main.java` | **Nuevo** |
| `GUIA.md` | **Nuevo** |
| `src/biblioteca/modelo/MaterialBibliografico.java` | Renombrado (sin tilde) + comentarios |
| `src/biblioteca/modelo/Ejemplar.java` | Antes vacío, ahora completo |
| `src/biblioteca/modelo/Prestamo.java` | Contador arreglado, `this(...)`, `if/else`, comentarios |
| `src/biblioteca/modelo/Multa.java` | Contador arreglado, comentarios |
| `src/biblioteca/modelo/Autor.java` | Contador arreglado + `actualizarContadorSiEsMayor`, comentarios |
| `src/biblioteca/modelo/Reserva.java` | Contador arreglado + `actualizarContadorSiEsMayor` + `esActivaDe`, comentarios |
| `src/biblioteca/servicio/Biblioteca.java` | Sincroniza también autores y reservas, usa `esActivaDe`, `if/else`, comentarios |
| `src/biblioteca/persistencia/GestorPersistencia.java` | `if/else`, comentarios |
| `src/biblioteca/gui/PanelInicio.java` | Comparación con `enum`, sin `var`, comentarios |
| Resto de los archivos | Solo comentarios |

---

## Glosario de este informe

| Palabra | Significado simple |
|---|---|
| **compilar** | Traducir los `.java` a `.class` para que la computadora los entienda |
| **clase** | Molde que describe una cosa |
| **objeto** | Una cosa concreta hecha con el molde |
| **atributo** | Un dato que guarda cada objeto |
| **método** | Una acción que sabe hacer un objeto |
| **constructor** | Método que se ejecuta al crear un objeto con `new` |
| **`this`** | "Este objeto" |
| **`this(...)`** | "Llamá a mi otro constructor" |
| **`static`** | Una sola variable compartida por todos los objetos de la clase |
| **`++`** | Sumar 1 |
| **`=` / `==`** | Guardar un valor / preguntar si son iguales |
| **`&&`** | "Y" (todas las condiciones tienen que cumplirse) |
| **`if` / `else`** | Si… / si no… |
| **`for`** | Repetir para cada elemento de una lista |
| **`null`** | "No hay nada" |
| **`boolean`** | Tipo de dato que solo puede ser `true` o `false` |
| **`enum`** | Lista cerrada de valores posibles |
| **ternario `? :`** | Forma comprimida de escribir un `if/else` en una línea |
| **`var`** | Dejar que Java adivine el tipo de una variable |
| **interfaz** | Contrato: métodos que una clase promete tener |
| **`main`** | El método por donde arranca todo programa Java |
| **lambda `() -> {}`** | Un pedazo de código que se pasa para ejecutar después |
| **clase anónima** | Una clase sin nombre, creada en el momento |
| **`JFrame`** | Una ventana |
| **Swing** | La parte de Java que sirve para hacer ventanas |
