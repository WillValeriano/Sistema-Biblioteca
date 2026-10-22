package biblioteca.gui;

import biblioteca.modelo.MaterialBibliografico;
import biblioteca.servicio.Biblioteca;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.Window;
import java.util.TreeSet;

/** Alta de un libro o de una revista. */
public class DialogoMaterial extends DialogoBase {

    private static final long serialVersionUID = 1L;

    private final transient Biblioteca biblioteca;
    private final boolean esLibro;

    private final JTextField titulo = campo(28);
    private final JTextField autores = campo(28);
    private final JTextField editorial = campo(28);
    private final JTextField codigoIsbnIssn = campo(28);
    private final JSpinner numero = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
    private final JComboBox<String> periodicidad = new JComboBox<>(
            new String[]{"Semanal", "Quincenal", "Mensual", "Bimestral", "Trimestral", "Anual"});
    private final JSpinner anio;
    private final JComboBox<String> categoria = new JComboBox<>();
    private final JSpinner ejemplares = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
    private final JTextField ubicacion = campo(28);

    private DialogoMaterial(Window padre, Biblioteca biblioteca, boolean esLibro) {
        super(padre, esLibro ? "Nuevo libro" : "Nueva revista",
                esLibro ? "Los autores que ya existen se reutilizan (agregación)."
                        : "Las revistas se prestan por un máximo de 3 días.",
                "Guardar");
        this.biblioteca = biblioteca;
        this.esLibro = esLibro;
        int anioActual = biblioteca.hoy().getYear();
        anio = new JSpinner(new SpinnerNumberModel(anioActual, 1450, anioActual + 1, 1));
        anio.setEditor(new JSpinner.NumberEditor(anio, "#"));

        TreeSet<String> categorias = new TreeSet<>();
        for (MaterialBibliografico m : biblioteca.listarMateriales()) {
            categorias.add(m.getCategoria());
        }
        categorias.forEach(categoria::addItem);
        categoria.setEditable(true);
        categoria.setSelectedItem("");
        ubicacion.setText(esLibro ? "Estante " : "Hemeroteca");

        for (JSpinner s : new JSpinner[]{anio, numero, ejemplares}) {
            s.setBorder(javax.swing.BorderFactory.createLineBorder(Estilo.BORDE));
            s.setPreferredSize(new java.awt.Dimension(120, 32));
        }
        ComboPlano.aplicar(categoria);
        ComboPlano.aplicar(periodicidad);
        agregarCampo("Título *", titulo);
        if (esLibro) {
            agregarCampo("Autor/es *", autores);
            autores.setToolTipText("Separados por coma: \"Kathy Sierra, Bert Bates\"");
            agregarCampo("Editorial", editorial);
            agregarCampo("ISBN", codigoIsbnIssn);
        } else {
            agregarCampo("ISSN", codigoIsbnIssn);
            agregarCampo("Número *", numero);
            agregarCampo("Periodicidad", periodicidad);
            periodicidad.setSelectedItem("Mensual");
        }
        agregarCampo("Año *", anio);
        agregarCampo("Categoría", categoria);
        agregarCampo("Ejemplares *", ejemplares);
        agregarCampo("Ubicación", ubicacion);
    }

    public static void abrir(Window padre, Biblioteca biblioteca, boolean esLibro) {
        new DialogoMaterial(padre, biblioteca, esLibro).mostrar();
    }

    @Override
    protected void aceptar() {
        String cat = categoria.getEditor().getItem() == null ? "" : categoria.getEditor().getItem().toString();
        boolean ok = Mensajes.intentar(this, () -> {
            if (esLibro) {
                biblioteca.registrarLibro(titulo.getText(), (Integer) anio.getValue(), cat,
                        codigoIsbnIssn.getText(), editorial.getText(), autores.getText(),
                        (Integer) ejemplares.getValue(), ubicacion.getText());
            } else {
                biblioteca.registrarRevista(titulo.getText(), (Integer) anio.getValue(), cat,
                        codigoIsbnIssn.getText(), (Integer) numero.getValue(),
                        (String) periodicidad.getSelectedItem(), (Integer) ejemplares.getValue(), ubicacion.getText());
            }
        });
        if (ok) {
            dispose();
        }
    }
}
