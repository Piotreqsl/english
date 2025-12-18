package org.example.lab1;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

/**
 * Lab 1 - Birthday Calculation
 * TASK 5: Calculate days since birth and days to next birthday
 */
public class Main {
    public static void main(String[] args) {
        LocalDate birthday = LocalDate.of(1997, 11, 2);

        LocalDate today = LocalDate.now();

        long daysSinceBirth = ChronoUnit.DAYS.between(birthday, today);

        LocalDate birthdayThisYear = LocalDate.of(today.getYear(), birthday.getMonth(), birthday.getDayOfMonth());

        long daysToBirthday = ChronoUnit.DAYS.between(today, birthdayThisYear);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        System.out.println("Today is " + today.format(formatter) + " – since the birthday on "
            + birthday.format(formatter) + ", " + daysSinceBirth + " days have passed.");
        System.out.println("Days to birthday this year: " + daysToBirthday);
    }
}
