package com.policymanagement.policymanagementservice.util;

import com.policymanagement.policymanagementservice.exception.InvalidDateException;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {

    public static final String DD_MM_YYYY = "dd.MM.yyyy";

    public static LocalDate stringToDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(DD_MM_YYYY));
        } catch (Exception exception) {
            throw new InvalidDateException();
        }
    }

    public static String dateToString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY);
        return localDate.format(formatter);
    }

}
