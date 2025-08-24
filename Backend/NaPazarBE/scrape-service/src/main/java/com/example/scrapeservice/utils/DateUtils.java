package com.example.scrapeservice.utils;

import com.example.scrapeservice.constants.Constants;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("Utils class");
    }

    public static Date convertToDate(String dateString, SimpleDateFormat formatter) {
        try {
            String fullDateString = dateString + new SimpleDateFormat("yyyy").format(new java.util.Date());
            return formatter.parse(fullDateString);
        } catch (ParseException e) {
            log.error("Failed to parse date: " + dateString);
            return null;
        }
    }

    public static LocalDate parsePartialDate(String dateStr) {
        int currentYear = LocalDate.now().getYear();
        String fullDateStr = dateStr + currentYear;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_PATTERN);

        return LocalDate.parse(fullDateStr, formatter);
    }
}
