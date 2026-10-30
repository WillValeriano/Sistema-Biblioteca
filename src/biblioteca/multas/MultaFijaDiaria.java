package biblioteca.multas;

import biblioteca.modelo.ConfiguracionBiblioteca;

// Forma de calcular la multa: días de atraso x precio por día, con un tope máximo.
// Ejemplo con $500/día y tope $15.000:  6 días -> 6 x 500 = $3.000
public class MultaFijaDiaria implements PoliticaMulta {

    @Override
    public double calcularMonto(long diasAtraso, ConfiguracionBiblioteca config) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        double total = diasAtraso * config.getMultaPorDia();
        // Si supera el tope máximo configurado en Administración, cobramos hasta el tope
        if (total > config.getTopeMaximoMulta()) {
            return config.getTopeMaximoMulta();
        }
        return total;
    }

    @Override
    public String getNombrePolitica() {
        return "Multa Fija Diaria";
    }

    @Override
    public String getExplicacion(ConfiguracionBiblioteca config) {
        return "Cobra $" + config.getMultaPorDia() + " por cada día de atraso (Tope máx: $" + config.getTopeMaximoMulta() + ").";
    }

    @Override
    public String toString() {
        return getNombrePolitica();
    }
}