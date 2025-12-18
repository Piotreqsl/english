package org.example.lab5;

/**
 * Demo program for testing the Person class (Task 1).
 */
public class DemoPerson {
    public static void main(String[] args) {
        Person p1 = new Person("Peter", "Pilegaard", "31.10.1972", Gender.MALE);
        Person p2 = new Person("Mouna", "Guddemane", "02.11.1997", Gender.FEMALE);
        
        System.out.println("ID=" + p1.getId() + " " + p1 + ", age=" + p1.getAgeYears());
        System.out.println("ID=" + p2.getId() + " " + p2 + ", age=" + p2.getAgeYears());
    }
}
