package org.example.lab5;

/**
 * Demo program for testing the Student class (Task 2).
 */
public class DemoStudent {
    public static void main(String[] args) {
        Student s1 = new Student("Anna", "Kowalska", "15.03.2003", Gender.FEMALE, "S12345");
        s1.addGrade(5.0);
        s1.addGrade(4.0);
        s1.addGrade(3.5);
        
        Student s2 = new Student("Piotr", "Nowak", "02.09.2002", Gender.MALE, "S99999");
        s2.addGrade(3.5);
        s2.addGrade(3.5);
        
        System.out.println(s1.getId() + ": " + s1 + 
            (s1.average().isPresent() ? String.format(" avg=%.2f", s1.average().getAsDouble()) : " avg=N/A"));
        
        System.out.println(s2.getId() + ": " + s2 + 
            (s2.average().isPresent() ? String.format(" avg=%.2f", s2.average().getAsDouble()) : " avg=N/A"));
    }
}
