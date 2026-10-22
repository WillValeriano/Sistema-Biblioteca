package biblioteca.multas;

import biblioteca.modelo.Formato;

/** Cobra un monto fijo por cada día de atraso. */
public class MultaFijaDiaria implements PoliticaMulta {

    private static final long serialVersionUID = 1L;

    private final double montoPorDia;

    public MultaFijaDiaria(double montoPorDia) {
        this.montoPorDia = montoPorDia;
    }

    @Override
    public double calcular(int diasAtraso) {
        return Math.max(0, diasAtraso) * montoPorDia;
    }

    @Override
    public String getNombre() {
        return "Fija diaria";
    }

    @Override
    public String getDescripcion() {
        return Formato.moneda(montoPorDia) + " por cada día de atraso";
    }
}
