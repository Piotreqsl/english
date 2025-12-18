package org.example.lab5;

/**
 * Demo program for testing the Group class (Task 3).
 */
public class DemoGroup {
    public static void main(String[] args) {
        Group g1 = new Group("G1", "Java Monday");
        
        Student anna = new Student("Anna", "Kowalska", "15.03.2003", Gender.FEMALE, "S1");
        Student piotr = new Student("Piotr", "Nowak", "02.09.2002", Gender.MALE, "S2");
        
        if (g1.addStudent(anna)) {
            System.out.println("Added Anna to G1");
        }
        
        if (g1.addStudent(piotr)) {
            System.out.println("Added Piotr to G1");
        }
        
        // Try to move Piotr to another group
        Group g2 = new Group("G2", "Java Friday");
        if (!g2.addStudent(piotr)) {
            System.out.println("Move failed: Piotr already in group " + GroupRegistry.getGroupName(piotr.getId()));
        }
        
        System.out.println(g1);
    }
}
