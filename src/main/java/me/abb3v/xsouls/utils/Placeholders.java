package me.abb3v.xsouls.utils;

public class Placeholders {
    private final String placeholder;
    private final String value;

    public Placeholders(String placeholder, String value) {
        this.placeholder = placeholder;
        this.value = value;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getValue() {
        return value;
    }
}
