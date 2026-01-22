package org.example.lab5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Console application for managing students and groups (Task 7).
 */
public class ConsoleApp {
    private static final Logger log = LogManager.getLogger(ConsoleApp.class);
    private final Scanner scanner;
    private final StudentRepository studentRepo;
    private final GroupRepository groupRepo;
    private final ConfigManager config;
    
    public ConsoleApp() {
        scanner = new Scanner(System.in);
        studentRepo = new StudentRepository();
        groupRepo = new GroupRepository();
        config = new ConfigManager();
    }
    
    public static void main(String[] args) {
        log.info("Application starting...");
        
        try {
            ConsoleApp app = new ConsoleApp();
            app.run();
        } catch (Exception e) {
            log.error("Unexpected fatal error in main()", e);
            System.err.println("Fatal error, see logs for details: " + e.getMessage());
        }
        
        log.info("Application exiting.");
    }
    
    public void run() {
        log.debug("Starting main application loop");
        
        while (true) {
            showMenu();
            int choice = readInt("Select option: ");
            log.debug("User selected menu option '{}'", choice);
            
            try {
                if (!handleChoice(choice)) {
                    break;
                }
            } catch (Exception e) {
                log.error("Error handling menu option {}", choice, e);
                System.err.println("Error: " + e.getMessage());
            }
        }
        
        scanner.close();
        log.info("User exited application");
        System.out.println("Goodbye!");
    }
    
    private void showMenu() {
        System.out.println("\n=== Student & Group Manager ===");
        System.out.println("1) Load students from CSV");
        System.out.println("2) Load groups from CSV");
        System.out.println("3) Save students to CSV");
        System.out.println("4) Save groups to CSV");
        System.out.println("5) Add new student");
        System.out.println("6) Add new group");
        System.out.println("7) Assign student to group");
        System.out.println("8) Remove student from group");
        System.out.println("9) Show all students");
        System.out.println("10) Show all groups");
        System.out.println("11) Show group details");
        System.out.println("12) Show current configuration");
        System.out.println("13) Edit configuration");
        System.out.println("0) Exit");
    }
    
    private boolean handleChoice(int choice) throws IOException {
        switch (choice) {
            case 1: loadStudents(); break;
            case 2: loadGroups(); break;
            case 3: saveStudents(); break;
            case 4: saveGroups(); break;
            case 5: addNewStudent(); break;
            case 6: addNewGroup(); break;
            case 7: assignStudentToGroup(); break;
            case 8: removeStudentFromGroup(); break;
            case 9: showAllStudents(); break;
            case 10: showAllGroups(); break;
            case 11: showGroupDetails(); break;
            case 12: config.showConfig(); break;
            case 13: editConfiguration(); break;
            case 0: return false;
            default: System.out.println("Invalid option. Try again.");
        }
        return true;
    }
    
    private void loadStudents() throws IOException {
        String filename = config.getStudentsFile();
        Path file = Paths.get(filename);
        
        if (!Files.exists(file)) {
            log.warn("Student file not found: {}", filename);
            System.out.println("File not found: " + filename);
            return;
        }
        
        log.info("Loading students from file: {}", filename);
        List<Student> students = CsvStudentHandler.loadStudents(file, config.getDelimiter());
        for (Student student : students) {
            studentRepo.add(student);
        }
        
        log.info("Successfully imported {} students", students.size());
        System.out.println("Imported " + students.size() + " students from " + filename);
    }
    
    private void loadGroups() throws IOException {
        String filename = config.getGroupsFile();
        Path file = Paths.get(filename);
        
        if (!Files.exists(file)) {
            log.warn("Groups file not found: {}", filename);
            System.out.println("File not found: " + filename);
            return;
        }
        
        log.info("Loading groups from file: {}", filename);
        List<Group> groups = CsvGroupHandler.loadGroups(file, config.getDelimiter(), studentRepo);
        for (Group group : groups) {
            groupRepo.add(group);
        }
        
        log.info("Successfully imported {} groups", groups.size());
        System.out.println("Imported " + groups.size() + " groups from " + filename);
    }
    
    private void saveStudents() throws IOException {
        String filename = config.getStudentsFile();
        Path file = Paths.get(filename);
        
        log.info("Saving {} students to file: {}", studentRepo.size(), filename);
        CsvStudentHandler.saveStudents(studentRepo.getAll(), file, config.getDelimiter());
        log.info("Successfully exported {} students", studentRepo.size());
        System.out.println("Exported " + studentRepo.size() + " students to " + filename);
    }
    
    private void saveGroups() throws IOException {
        String filename = config.getGroupsFile();
        Path file = Paths.get(filename);
        
        log.info("Saving {} groups to file: {}", groupRepo.getAll().size(), filename);
        CsvGroupHandler.saveGroups(groupRepo.getAll(), file, config.getDelimiter());
        log.info("Successfully exported {} groups", groupRepo.getAll().size());
        System.out.println("Exported " + groupRepo.getAll().size() + " groups to " + filename);
    }
    
    private void addNewStudent() {
        System.out.println("\n--- Add New Student ---");
        String firstName = readString("First name: ");
        String lastName = readString("Last name: ");
        String birthDate = readString("Birth date (DD.MM.YYYY): ");
        
        System.out.println("Gender: 1) MALE  2) FEMALE  3) OTHER");
        int genderChoice = readInt("Select: ");
        Gender gender = Gender.OTHER;
        if (genderChoice == 1) gender = Gender.MALE;
        else if (genderChoice == 2) gender = Gender.FEMALE;
        
        String indexNumber = readString("Index number: ");
        
        Student student = new Student(firstName, lastName, birthDate, gender, indexNumber);
        studentRepo.add(student);
        
        log.info("New student added via console: {} {} (ID: {})", firstName, lastName, student.getId());
        System.out.println("Student added with ID: " + student.getId());
        
        // Option to add grades
        String addGrades = readString("Add grades? (y/n): ");
        if (addGrades.equalsIgnoreCase("y")) {
            while (true) {
                String gradeStr = readString("Enter grade (or 'done' to finish): ");
                if (gradeStr.equalsIgnoreCase("done")) break;
                
                try {
                    double grade = Double.parseDouble(gradeStr);
                    student.addGrade(grade);
                    System.out.println("Grade added.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
    
    private void addNewGroup() {
        System.out.println("\n--- Add New Group ---");
        String name = readString("Group name: ");
        
        if (groupRepo.exists(name)) {
            System.out.println("Group already exists: " + name);
            return;
        }
        
        String description = readString("Description: ");
        
        Group group = new Group(name, description);
        groupRepo.add(group);
        
        log.info("New group created via console: {}", name);
        System.out.println("Group created: " + group);
    }
    
    private void assignStudentToGroup() {
        if (studentRepo.size() == 0) {
            System.out.println("No students available.");
            return;
        }
        
        if (groupRepo.getAll().isEmpty()) {
            System.out.println("No groups available.");
            return;
        }
        
        System.out.println("\nAvailable students:");
        int i = 1;
        List<Student> studentList = new ArrayList<>(studentRepo.getAll());
        for (Student s : studentList) {
            System.out.printf("%d) %s [%s]%n", i++, s, s.getId());
        }
        
        int studentIndex = readInt("Select student number: ") - 1;
        if (studentIndex < 0 || studentIndex >= studentList.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Student student = studentList.get(studentIndex);
        
        System.out.println("\nAvailable groups:");
        i = 1;
        List<Group> groupList = new ArrayList<>(groupRepo.getAll());
        for (Group g : groupList) {
            System.out.printf("%d) %s%n", i++, g);
        }
        
        int groupIndex = readInt("Select group number: ") - 1;
        if (groupIndex < 0 || groupIndex >= groupList.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Group group = groupList.get(groupIndex);
        
        if (group.addStudent(student)) {
            System.out.println("Student assigned to group.");
        } else {
            System.out.println("Failed: Student already in group " + 
                GroupRegistry.getGroupName(student.getId()));
        }
    }
    
    private void removeStudentFromGroup() {
        if (groupRepo.getAll().isEmpty()) {
            System.out.println("No groups available.");
            return;
        }
        
        System.out.println("\nAvailable groups:");
        int i = 1;
        List<Group> groupList = new ArrayList<>(groupRepo.getAll());
        for (Group g : groupList) {
            System.out.printf("%d) %s%n", i++, g);
        }
        
        int groupIndex = readInt("Select group number: ") - 1;
        if (groupIndex < 0 || groupIndex >= groupList.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Group group = groupList.get(groupIndex);
        
        if (group.getMembers().isEmpty()) {
            System.out.println("Group is empty.");
            return;
        }
        
        System.out.println("\nMembers:");
        i = 1;
        List<Student> members = new ArrayList<>(group.getMembers());
        for (Student s : members) {
            System.out.printf("%d) %s%n", i++, s);
        }
        
        int studentIndex = readInt("Select student number: ") - 1;
        if (studentIndex < 0 || studentIndex >= members.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Student student = members.get(studentIndex);
        
        if (group.removeStudent(student)) {
            System.out.println("Student removed from group.");
        } else {
            System.out.println("Failed to remove student.");
        }
    }
    
    private void showAllStudents() {
        if (studentRepo.size() == 0) {
            System.out.println("\nNo students in repository.");
            return;
        }
        
        System.out.println("\n=== All Students ===");
        for (Student s : studentRepo.getAll()) {
            String avg = s.average().isPresent() 
                ? String.format("%.2f", s.average().getAsDouble()) 
                : "N/A";
            System.out.printf("%s: %s avg=%s%n", s.getId(), s, avg);
        }
    }
    
    private void showAllGroups() {
        if (groupRepo.getAll().isEmpty()) {
            System.out.println("\nNo groups in repository.");
            return;
        }
        
        System.out.println("\n=== All Groups ===");
        for (Group g : groupRepo.getAll()) {
            System.out.println(g);
        }
    }
    
    private void showGroupDetails() {
        if (groupRepo.getAll().isEmpty()) {
            System.out.println("No groups available.");
            return;
        }
        
        System.out.println("\nAvailable groups:");
        int i = 1;
        List<Group> groupList = new ArrayList<>(groupRepo.getAll());
        for (Group g : groupList) {
            System.out.printf("%d) %s%n", i++, g);
        }
        
        int groupIndex = readInt("Select group number: ") - 1;
        if (groupIndex < 0 || groupIndex >= groupList.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Group group = groupList.get(groupIndex);
        
        System.out.println("\n" + group);
        System.out.println("Members:");
        if (group.getMembers().isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (Student s : group.getMembers()) {
                String avg = s.average().isPresent() 
                    ? String.format("%.2f", s.average().getAsDouble()) 
                    : "N/A";
                System.out.printf("  - %s %s (%s) avg=%s%n",
                    s.getFirstName(), s.getLastName(), s.getIndexNumber(), avg);
            }
        }
    }
    
    private void editConfiguration() {
        System.out.println("\n--- Edit Configuration ---");
        System.out.println("1) Change delimiter");
        System.out.println("2) Change students filename");
        System.out.println("3) Change groups filename");
        System.out.println("0) Cancel");
        
        int choice = readInt("Select: ");
        
        switch (choice) {
            case 1:
                String delimiter = readString("New delimiter: ");
                config.setDelimiter(delimiter);
                System.out.println("Delimiter updated.");
                break;
            case 2:
                String studentsFile = readString("New students filename: ");
                config.setStudentsFile(studentsFile);
                System.out.println("Students filename updated.");
                break;
            case 3:
                String groupsFile = readString("New groups filename: ");
                config.setGroupsFile(groupsFile);
                System.out.println("Groups filename updated.");
                break;
        }
    }
    
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
