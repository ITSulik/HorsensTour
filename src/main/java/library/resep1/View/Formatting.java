package library.resep1.View;

import library.resep1.Model.Enums.BusType;

import java.util.Locale;

/** Small display-formatting helpers shared by the dashboard's dialogs. */
final class Formatting {

    private Formatting() {
    }

    /** MINIBUS -> "Minibus", SPECIAL_PURPOSE_BUS -> "Special purpose bus". */
    static String prettifyBusType(BusType type) {
        if (type == null) {
            return "";
        }
        String[] words = type.name().toLowerCase(Locale.ENGLISH).split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }
}
