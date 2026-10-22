package biblioteca.multas;

import biblioteca.modelo.Formato;

/**
 * El valor diario aumenta un porcentaje por cada semana completa de atraso.
 * Ej.: base $500 y 50%: días 1-7 a $500, días 8-14 a $750, días 15-21 a $1000...
 * Desalienta los atrasos largos.
 */
public class MultaProgresiva implements PoliticaMulta {

    private static final long serialVersionUID = 1L;

    private final double montoBase;
    private final double incrementoSemanal;

    public MultaProgresiva(double montoBase, double incrementoSemanal) {
        this.montoBase = montoBase;
        this.incrementoSemanal = incrementoSemanal;
    }

    @Override
    public double calcular(int diasAtraso) {
        double total = 0;
        for (int dia = 0; dia < diasAtraso; dia++) {
            int semana = dia / 7;
            total += montoBase * (1 + incrementoSemanal * semana);
        }
        return total;
    }

    @Override
    public String getNombre() {
        return "Progresiva";
    }

    @Override
    public String getDescripcion() {
        return Formato.moneda(montoBase) + " por día, +" + Math.round(incrementoSemanal * 100) + "% por semana de atraso";
    }
}
