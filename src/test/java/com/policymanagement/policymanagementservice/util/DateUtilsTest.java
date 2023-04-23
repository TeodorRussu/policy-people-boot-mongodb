package com.policymanagement.policymanagementservice.util;

import com.policymanagement.policymanagementservice.exception.InvalidDateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class DateUtilsTest {

    @Test
    void stringToDateValidNotThrowException() {
        assertThatCode(() -> DateUtils.stringToDate("12.02.2023")).doesNotThrowAnyException();
    }

    @Test
    void stringToDateValid() {
        var expectedDate = LocalDate.of(2023, 12, 25);
        var actualOutput = DateUtils.stringToDate("25.12.2023");

        assertThat(actualOutput).isEqualTo(expectedDate);
    }

    @Test
    void stringToDatenullInputThrowsException() {
        assertThatExceptionOfType(InvalidDateException.class)
                .isThrownBy(() -> DateUtils.stringToDate(null))
                .withMessage("The date provided in the request does not follow the format 'dd.MM.yyyy'");
    }


    @ParameterizedTest
    @ValueSource(strings = {"", "word", "2023.11.23", "33.33.2023"})
    void stringToDateInvalidThrowsException(String invalidDateString) {
        assertThatExceptionOfType(InvalidDateException.class)
                .isThrownBy(() -> DateUtils.stringToDate(invalidDateString))
                .withMessage("The date provided in the request does not follow the format 'dd.MM.yyyy'");
    }

}