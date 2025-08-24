package com.example.scrapeservice.constants;

public class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Constants class");
    }

    public static final int BILLA_ID = 1;
    public static final int LIDL_ID = 2;
    public static final int KAUFLAND_ID = 3;
    public static final String KAUFLAND_URL = "https://www.kaufland.bg";
    public static final String DATE_TIME_FORMATTER_PATTERN = "dd.MM.yyyy";
    public static final String USER_NOT_FOUND = "User not found";
}
