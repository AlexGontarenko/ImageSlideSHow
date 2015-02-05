package ru.redmadrobot.alexgontarenko.slideshow.api;

public class APIWebEsception extends Exception {

    public static final String NO_INTERNET = "NO_INTERNET";
    public static final String NOT_200 = "NOT_200";
    public static final String BAD_CONNECTION = "BAD_CONNECTION";

    public APIWebEsception(String detailMessage) {
        super(detailMessage);
    }
}
