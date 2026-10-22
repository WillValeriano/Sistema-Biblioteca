package biblioteca.gui;

import biblioteca.modelo.Socio;
import biblioteca.modelo.SocioDocente;
import biblioteca.modelo.SocioEstudiante;
import biblioteca.modelo.SocioGeneral;
import biblioteca.servicio.Biblioteca;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Window;
import java.time.LocalDate;

/** Alta y edición de socios. En edición no se puede cambiar el DNI ni la categoría. */
public class DialogoSocio extends DialogoBase {

    private static final long serialVersionUID = 1L;

    private static final String ESTUDIANTE = "Estudiante";
    private static final String DOCENTE = "Docente";
    private static final String GENERAL = "General";

    private final transient Biblioteca biblioteca;
    private final transient Socio existente;

    private final JComboBox<String> tipo = new JComboBox<>(new String[]{ESTUDIANTE, DOCENTE, GENERAL});
    private final JLabel condiciones = new JLabel();
    private final JTextField dni = campo(24);
    private final JTextField nombre = campo(24);
    private final JTextField apellido = campo(24);
    private final JTextField email = campo(24);
    private final JTextField telefono = campo(24);
    private final JTextField carrera = campo(24);
    private final JTextField legajo = campo(24);
    private final JTextField departamento = campo(24);

    private DialogoSocio(Window padre, Biblioteca biblioteca, Socio existente) {
        super(padre, existente == null ? "Nuevo socio" : "Editar socio",
                existente == null ? "La categoría define cuántos préstamos puede tener y por cuánto tiempo."
                        : existente.getNumeroSocio() + " · " + existente.getTipo(),
                "Guardar");
        this.biblioteca = biblioteca;
        this.existente = existente;

        condiciones.setFont(Estilo.fuente(Font.PLAIN, 12));
        condiciones.setForeground(Estilo.ACENTO);

        ComboPlano.aplicar(tipo);
        if (existente == null) {
            agregarCampo("Categoría", tipo);
            agregarCampo("", condiciones);
        }
        agregarCampo("DNI *", dni);
        agregarCampo("Nombre *", nombre);
        agregarCampo("Apellido *", apellido);
        agregarCampo("Email", email);
        agregarCampo("Teléfono", telefono);
        if (existente == null) {
            agregarCampo("Carrera", carrera);
            agregarCampo("Legajo univ.", legajo);
            agregarCampo("Departamento", departamento);
            ocultarSegunTipo();
            tipo.addActionListener(e -> ocultarSegunTipo());
        } else {
            dni.setText(existente.getDni());
            dni.setEnabled(false);
            nombre.setText(existente.getNombre());
            apellido.setText(existente.getApellido());
            email.setText(existente.getEmail());
            telefono.setText(existente.getTelefono());
        }
    }

    public static void abrirAlta(Window padre, Biblioteca biblioteca) {
        new DialogoSocio(padre, biblioteca, null).mostrar();
    }

    public static void abrirEdicion(Window padre, Biblioteca biblioteca, Socio socio) {
        new DialogoSocio(padre, biblioteca, socio).mostrar();
    }

    /** Muestra solo los campos propios de la categoría elegida y sus condiciones (polimorfismo). */
    private void ocultarSegunTipo() {
        String t = (String) tipo.getSelectedItem();
        boolean est = ESTUDIANTE.equals(t);
        boolean doc = DOCENTE.equals(t);
        cambiarVisibilidad(carrera, est);
        cambiarVisibilidad(legajo, est);
        cambiarVisibilidad(departamento, doc);

        Socio ejemplo = crearEjemploDeCategoria(t);
        condiciones.setText("Hasta " + ejemplo.getMaxPrestamos() + " préstamos · " + ejemplo.getDiasPrestamo()
                + " días · " + Math.round(ejemplo.getDescuentoMulta() * 100) + "% de descuento en multas");
        pack();
    }

    private static Socio crearEjemploDeCategoria(String t) {
        LocalDate hoy = LocalDate.now();
        if (ESTUDIANTE.equals(t)) {
            return new SocioEstudiante("", "", "", "", "", "", hoy, "", "");
        }
        if (DOCENTE.equals(t)) {
            return new SocioDocente("", "", "", "", "", "", hoy, "");
        }
        return new SocioGeneral("", "", "", "", "", "", hoy);
    }

    /** Oculta el campo y su etiqueta (la etiqueta es el componente anterior en el formulario). */
    private void cambiarVisibilidad(JTextField campo, boolean visible) {
        campo.setVisible(visible);
        java.awt.Component[] comps = cuerpo.getComponents();
        for (int i = 1; i < comps.length; i++) {
            if (comps[i] == campo) {
                comps[i - 1].setVisible(visible);
            }
        }
    }

    @Override
    protected void aceptar() {
        boolean ok = Mensajes.intentar(this, () -> {
            if (existente != null) {
                biblioteca.actualizarDatosSocio(existente, nombre.getText(), apellido.getText(),
                        email.getText(), telefono.getText());
                return;
            }
            String t = (String) tipo.getSelectedItem();
            if (ESTUDIANTE.equals(t)) {
                biblioteca.registrarEstudiante(dni.getText(), nombre.getText(), apellido.getText(),
                        email.getText(), telefono.getText(), carrera.getText(), legajo.getText());
            } else if (DOCENTE.equals(t)) {
                biblioteca.registrarDocente(dni.getText(), nombre.getText(), apellido.getText(),
                        email.getText(), telefono.getText(), departamento.getText());
            } else {
                biblioteca.registrarGeneral(dni.getText(), nombre.getText(), apellido.getText(),
                        email.getText(), telefono.getText());
            }
        });
        if (ok) {
            dispose();
        }
    }
}
