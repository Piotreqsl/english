package org.example.lab5;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Demo program for sorting students and calculating statistics (Task 4).
 */
public class DemoSortStats {
    public static void main(String[] args) {
        Group group = new Group("G1", "Java Monday");
        
        // Create sample students with grades
        Student anna = new Student("Anna", "Kowalska", "15.03.2003", Gender.FEMALE, "S1");
        anna.addGrade(5.0);
        anna.addGrade(4.5);
        anna.addGrade(4.5);
        
        Student piotr = new Student("Piotr", "Nowak", "02.09.2002", Gender.MALE, "S2");
        piotr.addGrade(4.0);
        piotr.addGrade(4.0);
        piotr.addGrade(4.0);
        
        Student julia = new Student("Julia", "Mazur", "01.01.2004", Gender.FEMALE, "S3");
        julia.addGrade(3.5);
        julia.addGrade(3.5);
        
        Student adam = new Student("Adam", "Adamski", "10.05.2001", Gender.MALE, "S4");
        adam.addGrade(4.5);
        adam.addGrade(4.0);
        
        group.addStudent(anna);
        group.addStudent(piotr);
        group.addStudent(julia);
        group.addStudent(adam);
        
        // Sort by last name, then first name
        System.out.println("=== Sorted by name ===");
        List<Student> byName = group.getMembers().stream()
            .sorted(Comparator.comparing(Student::getLastName)
                .thenComparing(Student::getFirstName))
            .collect(Collectors.toList());
        
        byName.forEach(s -> System.out.println(s.getLastName() + " " + s.getFirstName()));
        
        // Sort by average (descending)
        System.out.println("\n=== Sorted by average (descending) ===");
        List<Student> byAverage = group.getMembers().stream()
            .sorted(Comparator.comparingDouble((Student s) -> s.average().orElse(0))
                .reversed())
            .collect(Collectors.toList());
        
        byAverage.forEach(s -> System.out.printf("%s %s avg=%.2f%n",
            s.getLastName(), s.getFirstName(), s.average().orElse(0)));
        
        // Sort by age (ascending)
        System.out.println("\n=== Sorted by age (ascending) ===");
        List<Student> byAge = group.getMembers().stream()
            .sorted(Comparator.comparingInt(Student::getAgeYears))
            .collect(Collectors.toList());
        
        byAge.forEach(s -> System.out.printf("%s %s age=%d%n",
            s.getLastName(), s.getFirstName(), s.getAgeYears()));
        
        // TOP-3 by average
        System.out.println("\n=== TOP-3 by average ===");
        List<Student> top3 = group.getMembers().stream()
            .sorted(Comparator.comparingDouble((Student s) -> s.average().orElse(0))
                .reversed())
            .limit(3)
            .collect(Collectors.toList());
        
        for (int i = 0; i < top3.size(); i++) {
            Student s = top3.get(i);
            System.out.printf("%d) %-20s avg = %.2f%n",
                i + 1, s.getFirstName() + " " + s.getLastName(), s.average().orElse(0));
        }
        
        // Group average
        double groupAverage = group.getMembers().stream()
            .filter(s -> s.average().isPresent())
            .mapToDouble(s -> s.average().getAsDouble())
            .average()
            .orElse(0);
        
        // Median
        List<Double> averages = group.getMembers().stream()
            .filter(s -> s.average().isPresent())
            .map(s -> s.average().getAsDouble())
            .sorted()
            .collect(Collectors.toList());
        
        double median = 0;
        if (!averages.isEmpty()) {
            int size = averages.size();
            if (size % 2 == 0) {
                median = (averages.get(size / 2 - 1) + averages.get(size / 2)) / 2.0;
            } else {
                median = averages.get(size / 2);
            }
        }
        
        System.out.printf("%nGroup average = %.2f, median = %.2f%n", groupAverage, median);
    }
}
