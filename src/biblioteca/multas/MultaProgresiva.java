package biblioteca.multas;

import biblioteca.modelo.ConfiguracionBiblioteca;

// Forma de calcular la multa: los primeros 5 días se cobran normal,
// y desde el 6° día cada día cuesta un 50% más (x 1.5). También tiene tope máximo.
// Ejemplo con $500/día:  8 días -> (5 x 500) + (3 x 500 x 1.5) = 2.500 + 2.250 = $4.750
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