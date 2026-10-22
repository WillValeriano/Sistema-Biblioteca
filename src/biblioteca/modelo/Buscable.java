package biblioteca.modelo;

import java.text.Normalizer;

/**
 * Contrato para entidades que pueden filtrarse con un texto libre
 * (búsqueda del catálogo, de socios, etc.).
 */
public interface Buscable {

    boolean coincideCon(String texto);

    /** Normaliza un texto: minúsculas y sin tildes, para comparar "Cortázar" con "cortazar". */
    static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().trim();
    }

    /** Devuelve true si alguno de los campos contiene el texto buscado. */
    static boolean contieneAlguno(String texto, String... campos) {
        String buscado = normalizar(texto);
        if (buscado.isEmpty()) {
            return true;
        }
        for (String campo : campos) {
            if (normalizar(campo).contains(buscado)) {
                return true;
            }
        }
        return false;
    }
}
