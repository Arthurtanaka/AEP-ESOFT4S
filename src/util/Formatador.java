package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Funções para mostrar números e datas de forma bonita na tela (padrão brasileiro). */
public class Formatador {

    private static final Locale BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Formatador() {
    }

    /** 75.0 minutos vira "1h 15min"; 40.0 vira "40 min". */
    public static String tempo(double minutos) {
        long total = Math.round(minutos);
        long horas = total / 60;
        long resto = total % 60;
        if (horas == 0) {
            return resto + " min";
        }
        return horas + "h " + String.format("%02d", resto) + "min";
    }

    /** 12.5 vira "12,50 km". */
    public static String distancia(double km) {
        return String.format(BR, "%.2f km", km);
    }

    /** Abaixo de 1000 g mostra em gramas; acima, em quilos. */
    public static String impacto(double gramasCo2) {
        if (gramasCo2 >= 1000) {
            return String.format(BR, "%.2f kg CO2", gramasCo2 / 1000.0);
        }
        return String.format(BR, "%.0f g CO2", gramasCo2);
    }

    public static String decimal(double valor) {
        return String.format(BR, "%.2f", valor);
    }

    public static String dataHora(LocalDateTime data) {
        if (data == null) {
            return "-";
        }
        return data.format(DATA_HORA);
    }
}
