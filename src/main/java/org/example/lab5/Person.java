package org.example.lab5;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a person with basic information including name, birth date, gender, and unique ID.
 */
public class Person {
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    private final String id;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final Gender gender;
    
    /**
     * Creates a new Person with the given details.
     * 
     * @param firstName the person's first name
     * @param lastName the person's last name
     * @param birthDate the birth date as a string in DD.MM.YYYY format
     * @param gender the person's gender
     */
    public Person(String firstName, String lastName, String birthDate, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = LocalDate.parse(birthDate, DATE_FORMATTER);
        this.gender = gender;
        this.id = generateId();
    }
    
    /**
     * Generates a unique 7-character ID using base-36 encoding.
     * 
     * @return a unique ID string
     */
    private String generateId() {
        long value = ID_COUNTER.incrementAndGet();
        String base36 = Long.toString(value, 36).toUpperCase();
        return String.format("%7s", base36).replace(' ', '0');
    }
    
    /**
     * Returns the person's unique ID.
     * 
     * @return the ID string
     */
    public String getId() {
        return id;
    }
    
    /**
     * Returns the person's first name.
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Returns the person's last name.
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Returns the person's birth date.
     * 
     * @return the birth date
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    /**
     * Returns the birth date as a formatted string.
     * 
     * @return the birth date in DD.MM.YYYY format
     */
    public String getBirthDateString() {
        return birthDate.format(DATE_FORMATTER);
    }
    
    /**
     * Returns the person's gender.
     * 
     * @return the gender
     */
    public Gender getGender() {
        return gender;
    }
    
    /**
     * Computes the person's full age in years.
     * 
     * @return the age in years
     */
    public int getAgeYears() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    /**
     * Returns a string representation of the person.
     * 
     * @return a formatted string with person details
     */
    @Override
    public String toString() {
        return String.format("%s %s (%s, %s)", firstName, lastName, getBirthDateString(), gender);
    }
}
