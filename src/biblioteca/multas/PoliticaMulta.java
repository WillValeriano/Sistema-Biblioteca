package biblioteca.multas;

import biblioteca.modelo.ConfiguracionBiblioteca;
import java.io.Serializable;

// Segunda interfaz del proyecto para el Diagrama UML.
// Permite cambiar la forma en que se calculan las multas (Fija o Progresiva).
public interface PoliticaMulta extends Serializable {
    
    double calcularMonto(long diasAtraso, ConfiguracionBiblioteca config);
    String getNombrePolitica();
    String getExplicacion(ConfiguracionBiblioteca config);
}