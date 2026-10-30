package biblioteca.multas;

import biblioteca.modelo.ConfiguracionBiblioteca;

public class MultaProgresiva implements PoliticaMulta {

    @Override
    public double calcularMonto(long diasAtraso, ConfiguracionBiblioteca config) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        double base = config.getMultaPorDia();
        double total;

        if (diasAtraso <= 5) {
            total = diasAtraso * base;
        } else {
            long diasExtra = diasAtraso - 5;
            // Primeros 5 días a valor base + días restantes con 50% de recargo (1.5)
            total = (5 * base) + (diasExtra * base * 1.5);
        }

        if (total > config.getTopeMaximoMulta()) {
            return config.getTopeMaximoMulta();
        }
        return total;
    }

    @Override
    public String getNombrePolitica() {
        return "Multa Progresiva (+50% tras 5 días)";
    }

    @Override
    public String getExplicacion(ConfiguracionBiblioteca config) {
        return "Primeros 5 días a $" + config.getMultaPorDia() + "/día; desde el 6° día suma 50% de recargo.";
    }

    @Override
    public String toString() {
        return getNombrePolitica();
    }
}