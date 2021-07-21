package ru.redline.opprison.utils.formatter;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@UtilityClass
public class DoubleFormatter {

    private static final DecimalFormat SPACE_FORMAT;

    static {
        SPACE_FORMAT = new DecimalFormat("");
        val custom = new DecimalFormatSymbols();
        custom.setGroupingSeparator(' ');
        custom.setDecimalSeparator('.');
        SPACE_FORMAT.setDecimalFormatSymbols(custom);
        SPACE_FORMAT.setGroupingSize(3);
    }

    public static String space(double to) {
        String format = SPACE_FORMAT.format(to);
        return format.contains(".") ? format.split("\\.")[0] : format;
    }

    private static final DecimalFormat COMMA_FORMAT;

    static {
        COMMA_FORMAT = new DecimalFormat("");
        val custom = new DecimalFormatSymbols();
        custom.setGroupingSeparator(',');
        custom.setDecimalSeparator('.');
        COMMA_FORMAT.setDecimalFormatSymbols(custom);
        COMMA_FORMAT.setGroupingSize(3);
    }

    public static String comma(double to) {
        String format = COMMA_FORMAT.format(to);
        return format.contains(".") ? format.split("\\.")[0] : format;
    }

    private static final DecimalFormat CHAR_FORMAT = new DecimalFormat("##.##");
    private static final String[] CHARS = new String[34];

    static {
        CHARS[33] = "d";
        CHARS[30] = "N";
        CHARS[27] = "O";
        CHARS[24] = "S";
        CHARS[21] = "s";
        CHARS[18] = "Q";
        CHARS[15] = "q";
        CHARS[12] = "T";
        CHARS[9] = "B";
        CHARS[6] = "M";
        CHARS[3] = "k";
    }

    public static String chars(double toFormat) {
        String formatted = "";
        for (int i = 33; i >= 3; i -= 3) {
            double timely = toFormat / Math.pow(10, i);
            if (timely >= 1d) {
                formatted = CHAR_FORMAT.format(timely) + CHARS[i];
                break;
            }
        }
        if (formatted.isEmpty())
            formatted = CHAR_FORMAT.format(toFormat);
        while (formatted.contains(".") && (formatted.endsWith("0") || formatted.endsWith(".")))
            formatted = formatted.substring(0, formatted.length() - 1);
        return formatted.replace(",", ".");
    }

    public static String fix(double to) {
        String formatted = CHAR_FORMAT.format(to);
        while (formatted.contains(".") && (formatted.endsWith("0") || formatted.endsWith(".")))
            formatted = formatted.substring(0, formatted.length() - 1);
        return formatted.replace(",", ".");
    }

    public static String clear(double to) {
        String format = CHAR_FORMAT.format(to);
        return format.contains(".") ? format.split("\\.")[0] : format;
    }

}
