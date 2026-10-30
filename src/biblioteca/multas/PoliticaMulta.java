package biblioteca.multas;

import biblioteca.modelo.ConfiguracionBiblioteca;
import java.io.Serializable;

// ¿QUÉ ES UNA POLÍTICA DE MULTA?
// Es una INTERFAZ: un "contrato" que dice QUÉ se puede hacer, pero no CÓMO.
// Cualquier clase que la implemente debe saber calcular el monto de una multa.
//
// Hoy hay dos formas de calcularla:
//   - MultaFijaDiaria  -> siempre el mismo precio por día
//   - MultaProgresiva  -> más cara a partir del 6° día
//
// La Biblioteca guarda una variable de tipo PoliticaMulta y llama a calcularMonto(...)
// sin saber cuál de las dos es. Para cambiar la regla basta con cambiar el objeto;
// no hay que tocar el código de la Biblioteca. (Esto se llama "patrón Strategy").
public interface PoliticaMulta extends Serializable {
    
    double calcularMonto(long diasAtraso, ConfiguracionBiblioteca config);
    String getNombrePolitica();
    String getExplicacion(ConfiguracionBiblioteca config);
}