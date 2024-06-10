package me.abb3v.xsouls.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    private static final char COLOR_CHAR = '\u00A7';
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    private static final Pattern COLOR_PATTERN = Pattern.compile("&([0-9a-fk-orA-FK-OR])");

    public static String formatMessage(String message, Placeholders... placeholders) {
        if (message == null) {
            return null;
        }

        for (Placeholders placeholder : placeholders) {
            message = message.replace(placeholder.getPlaceholder(), placeholder.getValue());
        }

        message = translateHexColorCodes(message);
        message = translateColorCodes(message);

        return message;
    }

    private static String translateHexColorCodes(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 32);

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            StringBuilder replacement = new StringBuilder(COLOR_CHAR + "x");
            for (char c : hexColor.toCharArray()) {
                replacement.append(COLOR_CHAR).append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String translateColorCodes(String message) {
        Matcher matcher = COLOR_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length());

        while (matcher.find()) {
            matcher.appendReplacement(buffer, COLOR_CHAR + matcher.group(1));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
