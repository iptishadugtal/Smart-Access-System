package com.amdocs.sas.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.amdocs.sas.exceptions.InvalidDateFormatException;

public class DateUtil {

    // dd-mm-yyyy to yyyy-mm-dd for DB
    public static String convertToDbFormat(String input) throws InvalidDateFormatException {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(input, inputFormatter);
            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
        	throw new InvalidDateFormatException("Invalid ! Please enter the date in your local time format: DD-MM-YYYY.");
        }
    }
    // Converts from sql to dd-mm-yyyy for display
    public static String convertToDisplayFormat(java.sql.Date sqlDate) {
        if (sqlDate == null) return "NA";
        LocalDate localDate = sqlDate.toLocalDate();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(outputFormatter);
    }
}
