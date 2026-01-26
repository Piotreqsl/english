package org.example.lab5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a group of students.
 */
public class Group {
    private static final Logger log = LogManager.getLogger(Group.class);
    private final String name;
    private String description;  // Changed from final to allow editing
    private final Set<Student> members;
    
    /**
     * Creates a new Group.
     * 
     * @param name the group name (e.g., "G1")
     * @param description a short description (e.g., "Java Monday")
     */
    public Group(String name, String description) {
        this.name = name;
        this.description = description;
        this.members = new HashSet<>();
        log.info("Group created: name='{}', description='{}'", name, description);
    }
    
    /**
     * Returns the group name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the group description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets a new description for the group.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
        log.info("Group '{}' description updated to: '{}'", name, description);
    }

    /**
     * Adds a student to the group.
     * The student can only be added if they are not already in another group.
     * 
     * @param student the student to add
     * @return true if the student was added, false if they are already in another group
     */
    public boolean addStudent(Student student) {
        String studentId = student.getId();
        
        if (GroupRegistry.isAssigned(studentId)) {
            String assignedGroup = GroupRegistry.getGroupName(studentId);
            if (!assignedGroup.equals(this.name)) {
                log.warn("Cannot add student id={} to group='{}' — already in group='{}'",
                    studentId, name, assignedGroup);
                return false; // Student is in a different group
            }
            // Student is already in this group, no need to add again
            log.debug("Student index={} already in group='{}'", student.getIndexNumber(), name);
            return true;
        }
        
        boolean added = members.add(student);
        if (added) {
            GroupRegistry.assign(studentId, this.name);
            log.info("Student index={} added to group='{}'", student.getIndexNumber(), name);
        }
        return added;
    }
    
    /**
     * Removes a student from the group.
     * 
     * @param student the student to remove
     * @return true if the student was removed, false if they were not in the group
     */
    public boolean removeStudent(Student student) {
        boolean removed = members.remove(student);
        if (removed) {
            GroupRegistry.unassign(student.getId());
            log.info("Student index={} removed from group='{}'", student.getIndexNumber(), name);
        } else {
            log.warn("Attempt to remove not-member index={} from group='{}'",
                student.getIndexNumber(), name);
        }
        return removed;
    }
    
    /**
     * Returns an unmodifiable view of the group members.
     * 
     * @return the set of members
     */
    public Set<Student> getMembers() {
        return Collections.unmodifiableSet(members);
    }
    
    /**
     * Exports all students in the group to a CSV file using the default delimiter (;).
     * 
     * @param file the path to the output file
     * @throws IOException if an I/O error occurs
     */
    public void exportToCsv(Path file) throws IOException {
        exportToCsv(file, ";");
    }
    
    /**
     * Exports all students in the group to a CSV file using a custom delimiter.
     * 
     * Format: id;index;firstName;lastName;birthDate;[grades]
     * Grades are exported with a dot (.) as decimal separator.
     * 
     * @param file the path to the output file
     * @param delimiter the CSV delimiter
     * @throws IOException if an I/O error occurs
     */
    public void exportToCsv(Path file, String delimiter) throws IOException {
        log.info("Exporting {} students from group='{}' to file={}",
            members.size(), name, file);
        
        List<String> lines = new ArrayList<>();
        
        for (Student student : members) {
            String gradesString = student.getGrades().stream()
                .map(g -> String.format(Locale.US, "%.1f", g))
                .collect(Collectors.joining(",", "[", "]"));
            
            String line = String.join(delimiter,
                student.getId(),
                student.getIndexNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getBirthDateString(),
                gradesString
            );
            
            log.debug("CSV line: {}", line);
            lines.add(line);
        }
        
        try {
            Files.write(file, lines);
            log.info("Export successful: {} lines written to {}", lines.size(), file);
        } catch (IOException e) {
            log.error("Export failed for file={}: {}", file, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Imports students from a CSV file using the default delimiter (;).
     * 
     * @param file the path to the input file
     * @throws IOException if an I/O error occurs
     * @throws CsvFormatException if the CSV format is invalid
     */
    public void importFromCsv(Path file) throws IOException, CsvFormatException {
        importFromCsv(file, ";");
    }
    
    /**
     * Imports students from a CSV file using a custom delimiter.
     * 
     * Expected format: id;index;firstName;lastName;birthDate;[grades]
     * 
     * @param file the path to the input file
     * @param delimiter the CSV delimiter
     * @throws IOException if an I/O error occurs
     * @throws CsvFormatException if the CSV format is invalid
     */
    public void importFromCsv(Path file, String delimiter) throws IOException, CsvFormatException {
        log.info("Importing students into group='{}' from file={}", name, file);
        
        List<String> lines = Files.readAllLines(file);
        int imported = 0;
        
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            
            log.debug("Raw CSV line: {}", line);
            
            String[] parts = line.split(Pattern.quote(delimiter), -1);
            if (parts.length != 6) {
                String msg = "Malformed CSV line (expected 6 fields): " + line;
                log.error(msg);
                throw new CsvFormatException(msg);
            }
            
            try {
                String indexNumber = parts[1];
                String firstName = parts[2];
                String lastName = parts[3];
                String birthDate = parts[4];
                String gradesString = parts[5];
                
                // Parse gender - default to OTHER if not specified
                Gender gender = Gender.OTHER;
                
                Student student = new Student(firstName, lastName, birthDate, gender, indexNumber);
                
                // Parse grades
                if (gradesString.startsWith("[") && gradesString.endsWith("]")) {
                    String gradesContent = gradesString.substring(1, gradesString.length() - 1);
                    if (!gradesContent.isEmpty()) {
                        String[] gradeStrings = gradesContent.split(",");
                        for (String gradeStr : gradeStrings) {
                            try {
                                double grade = Double.parseDouble(gradeStr.trim());
                                student.addGrade(grade);
                            } catch (IllegalArgumentException e) {
                                log.warn("Invalid grade: {} in line: {}", gradeStr, line);
                            }
                        }
                    }
                }
                
                if (addStudent(student)) {
                    imported++;
                }
            } catch (Exception e) {
                String msg = "Error parsing CSV line: " + line;
                log.error(msg, e);
                throw new CsvFormatException(msg, e);
            }
        }
        
        log.info("Import finished: {} students imported into group='{}'", imported, name);
    }
    
    /**
     * Returns a string representation of the group.
     * 
     * @return a formatted string with group details
     */
    @Override
    public String toString() {
        return String.format("Group %s (%s) size=%d", name, description, members.size());
    }
}
