package biblioteca.modelo;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Utilidades de formato para mostrar fechas y montos en castellano rioplatense. */
public final class Formato {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale AR = Locale.forLanguageTag("es-AR");

    private Formato() {
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "—" : fecha.format(FECHA);
    }

    public static String moneda(double monto) {
        NumberFormat nf = NumberFormat.getNumberInstance(AR);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return "$ " + nf.format(monto);
    }
}
