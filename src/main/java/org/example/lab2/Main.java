package org.example.lab2;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;

public class Main {
    private static final String[] CHINESE_ANIMALS = {
        "Rat", "Ox", "Tiger", "Rabbit", "Dragon", "Snake",
        "Horse", "Goat", "Monkey", "Rooster", "Dog", "Pig"
    };

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java BirthdayInfo <year> <month> <day>");
            return;
        }

        try {
            int year = Integer.parseInt(args[0]);
            int month = Integer.parseInt(args[1]);
            int day = Integer.parseInt(args[2]);

            LocalDate birthday = LocalDate.of(year, month, day);
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            System.out.println("Birthday: " + birthday.format(formatter));
            System.out.println("Day of the week: " + getDayOfWeekName(birthday.getDayOfWeek()));

            if (birthday.isLeapYear()) {
                System.out.println(year + " is a leap year");
            }

            System.out.println("Western zodiac: " + getWesternZodiac(month, day));

            System.out.println("Chinese zodiac: " + getChineseZodiac(year, month, day));

            System.out.println();


            long daysSinceBirth = ChronoUnit.DAYS.between(birthday, today);
            System.out.println("Today is " + today.format(formatter) + " - since the birthday on "
                + birthday.format(formatter) + ", " + daysSinceBirth + " days have passed.");

            Period age = Period.between(birthday, today);
            System.out.println("Age today: " + age.getYears() + " years, "
                + age.getMonths() + " months, " + age.getDays() + " days.");

            printBirthdayInfo(birthday, today, formatter);

        } catch (Exception e) {
            System.out.println("Error: Invalid date format. Usage: java BirthdayInfo <year> <month> <day>");
        }
    }

    private static String getDayOfWeekName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "Monday";
            case TUESDAY: return "Tuesday";
            case WEDNESDAY: return "Wednesday";
            case THURSDAY: return "Thursday";
            case FRIDAY: return "Friday";
            case SATURDAY: return "Saturday";
            case SUNDAY: return "Sunday";
            default: return "";
        }
    }

    private static String getWesternZodiac(int month, int day) {
        if ((month == 12 && day >= 22) || (month == 1 && day <= 19)) return "Capricorn";
        if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) return "Aquarius";
        if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) return "Pisces";
        if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) return "Aries";
        if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) return "Taurus";
        if ((month == 5 && day >= 21) || (month == 6 && day <= 20)) return "Gemini";
        if ((month == 6 && day >= 21) || (month == 7 && day <= 22)) return "Cancer";
        if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "Leo";
        if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "Virgo";
        if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) return "Libra";
        if ((month == 10 && day >= 23) || (month == 11 && day <= 21)) return "Scorpio";
        if ((month == 11 && day >= 22) || (month == 12 && day <= 21)) return "Sagittarius";
        return "Unknown";
    }

    private static String getChineseZodiac(int year, int month, int day) {
        int zodiacYear = year;
        if (month < 2 || (month == 2 && day < 4)) {
            zodiacYear = year - 1;
        }

        int index = (zodiacYear - 1900) % 12;
        String animal = CHINESE_ANIMALS[index];

        if ((month == 1 && day >= 21) || (month == 2 && day <= 3)) {
            return animal + " (warning: possible inaccuracy for Jan 21-Feb 3, simplified boundary Feb 4)";
        }

        return animal;
    }

    private static void printBirthdayInfo(LocalDate birthday, LocalDate today, DateTimeFormatter formatter) {
        int currentYear = today.getYear();

        LocalDate birthdayThisYear;
        if (birthday.getMonthValue() == 2 && birthday.getDayOfMonth() == 29 && !LocalDate.of(currentYear, 1, 1).isLeapYear()) {
            birthdayThisYear = LocalDate.of(currentYear, 2, 28);
        } else {
            birthdayThisYear = LocalDate.of(currentYear, birthday.getMonthValue(), birthday.getDayOfMonth());
        }

        long daysTo = ChronoUnit.DAYS.between(today, birthdayThisYear);

        if (daysTo > 0) {
            System.out.println("Birthday is in " + daysTo + " days (this year).");
        } else if (daysTo < 0) {
            System.out.println("Birthday was " + Math.abs(daysTo) + " days ago (this year).");

            int nextYear = currentYear + 1;
            LocalDate nextBirthday;
            if (birthday.getMonthValue() == 2 && birthday.getDayOfMonth() == 29 && !LocalDate.of(nextYear, 1, 1).isLeapYear()) {
                nextBirthday = LocalDate.of(nextYear, 2, 28);
            } else {
                nextBirthday = LocalDate.of(nextYear, birthday.getMonthValue(), birthday.getDayOfMonth());
            }

            long daysToNext = ChronoUnit.DAYS.between(today, nextBirthday);
            String dayOfWeek = getDayOfWeekName(nextBirthday.getDayOfWeek());

            System.out.println("Next birthday: " + nextBirthday.format(formatter)
                + " (" + dayOfWeek + "), in " + daysToNext + " days.");
        } else {
            System.out.println("Birthday is today!");
        }
    }
}
