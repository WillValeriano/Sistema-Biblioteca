package biblioteca.modelo;

/**
 * Revista o publicación periódica. Su préstamo está limitado a 3 días,
 * sin importar la categoría del socio.
 */
public class Revista extends MaterialBibliografico {

    private static final long serialVersionUID = 1L;

    private String issn;
    private int numero;
    private String periodicidad;

    public Revista(String codigo, String titulo, int anioPublicacion, String categoria,
                   String issn, int numero, String periodicidad) {
        super(codigo, titulo, anioPublicacion, categoria);
        this.issn = issn;
        this.numero = numero;
        this.periodicidad = periodicidad;
    }

    @Override
    public String getTipo() {
        return "Revista";
    }

    @Override
    public int getPlazoMaximoDias() {
        return 3;
    }

    @Override
    public String getDetalle() {
        return "N.º " + numero + " · " + periodicidad;
    }

    @Override
    public boolean coincideCon(String texto) {
        return super.coincideCon(texto) || Buscable.contieneAlguno(texto, issn);
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getPeriodicidad() {
        return periodicidad;
    }

    public void setPeriodicidad(String periodicidad) {
        this.periodicidad = periodicidad;
    }
}
