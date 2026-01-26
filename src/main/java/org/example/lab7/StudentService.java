package org.example.lab7;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.lab5.*;

/**
 * Service layer for student operations.
 * Handles business logic for student management.
 */
public class StudentService {
    private static final Logger log = LogManager.getLogger(StudentService.class);

    private final StudentRepository studentRepo;
    private final GroupRepository groupRepo;

    public StudentService(StudentRepository studentRepo, GroupRepository groupRepo) {
        this.studentRepo = studentRepo;
        this.groupRepo = groupRepo;
    }

    /**
     * Creates a new student and optionally adds them to a group.
     *
     * @param firstName student's first name
     * @param lastName student's last name
     * @param birthDate birth date in DD.MM.YYYY format
     * @param gender student's gender
     * @param indexNumber unique index number
     * @param grades array of grades to add
     * @param groupName optional group name to add student to
     * @return the created student
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if index number already exists
     */
    public Student createStudent(String firstName, String lastName, String birthDate,
                                  Gender gender, String indexNumber, double[] grades,
                                  String groupName) {
        log.debug("Creating student: {} {} (index: {})", firstName, lastName, indexNumber);

        // Validation
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name and last name are required.");
        }

        if (indexNumber == null || indexNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Index number is required.");
        }

        // Check for duplicate index number
        if (isIndexNumberTaken(indexNumber)) {
            throw new IllegalStateException("Student with index number '" + indexNumber + "' already exists.");
        }

        // Create student (validates birth date format)
        Student student = new Student(firstName, lastName, birthDate, gender, indexNumber);

        // Add grades
        if (grades != null) {
            for (double grade : grades) {
                student.addGrade(grade);
            }
        }

        // Add to repository
        studentRepo.add(student);
        log.info("Student created: {} {} (index: {})", firstName, lastName, indexNumber);

        // Optionally add to group
        if (groupName != null && !groupName.trim().isEmpty()) {
            Group group = groupRepo.getByName(groupName);
            if (group != null) {
                if (group.addStudent(student)) {
                    log.info("Student added to group: {}", groupName);
                } else {
                    log.warn("Could not add student to group: {}", groupName);
                }
            }
        }

        return student;
    }

    /**
     * Updates an existing student's information.
     * Creates a new student object with updated data and preserves grades.
     *
     * @param studentId ID of student to update
     * @param newFirstName new first name
     * @param newLastName new last name
     * @param newBirthDate new birth date
     * @param newGender new gender
     * @param newIndexNumber new index number
     * @return the updated student
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if new index number is already taken
     */
    public Student updateStudent(String studentId, String newFirstName, String newLastName,
                                  String newBirthDate, Gender newGender, String newIndexNumber) {
        log.debug("Updating student: {}", studentId);

        Student oldStudent = studentRepo.getById(studentId);
        if (oldStudent == null) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }

        // Validation
        if (newFirstName == null || newFirstName.trim().isEmpty() ||
            newLastName == null || newLastName.trim().isEmpty() ||
            newBirthDate == null || newBirthDate.trim().isEmpty() ||
            newIndexNumber == null || newIndexNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("All fields are required.");
        }

        // Check if index number changed and is already taken
        if (!newIndexNumber.equals(oldStudent.getIndexNumber()) && isIndexNumberTaken(newIndexNumber)) {
            throw new IllegalStateException("Index number already exists: " + newIndexNumber);
        }

        // Get current group membership
        String groupName = GroupRegistry.getGroupName(studentId);
        Group currentGroup = null;
        if (groupName != null) {
            currentGroup = groupRepo.getByName(groupName);
        }

        // Remove old student from group
        if (currentGroup != null) {
            currentGroup.removeStudent(oldStudent);
        }

        // Create new student with updated data
        Student updatedStudent = new Student(newFirstName, newLastName, newBirthDate, newGender, newIndexNumber);

        // Copy grades from old student
        for (Double grade : oldStudent.getGrades()) {
            updatedStudent.addGrade(grade);
        }

        // Remove old student from repository
        studentRepo.remove(studentId);

        // Add updated student to repository
        studentRepo.add(updatedStudent);

        // Re-add to group with new student object
        if (currentGroup != null) {
            currentGroup.addStudent(updatedStudent);
        }

        log.info("Student updated: old index={}, new index={}", oldStudent.getIndexNumber(), newIndexNumber);
        return updatedStudent;
    }

    /**
     * Removes a student from the system.
     *
     * @param studentId ID of student to remove
     * @return true if student was removed, false otherwise
     */
    public boolean removeStudent(String studentId) {
        log.debug("Removing student: {}", studentId);

        Student student = studentRepo.getById(studentId);
        if (student == null) {
            log.warn("Student not found: {}", studentId);
            return false;
        }

        // Remove from group if assigned
        String groupName = GroupRegistry.getGroupName(studentId);
        if (groupName != null) {
            Group group = groupRepo.getByName(groupName);
            if (group != null) {
                group.removeStudent(student);
            }
        }

        // Remove from repository
        boolean removed = studentRepo.getAll().removeIf(s -> s.getId().equals(studentId));

        if (removed) {
            log.info("Student removed: {} {}", student.getFirstName(), student.getLastName());
        }

        return removed;
    }

    /**
     * Transfers a student to a different group.
     *
     * @param studentId ID of student to transfer
     * @param targetGroupName name of target group
     * @throws IllegalArgumentException if student or group not found
     * @throws IllegalStateException if student is already in target group
     */
    public void transferStudent(String studentId, String targetGroupName) {
        log.debug("Transferring student {} to group {}", studentId, targetGroupName);

        Student student = studentRepo.getById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }

        Group targetGroup = groupRepo.getByName(targetGroupName);
        if (targetGroup == null) {
            throw new IllegalArgumentException("Target group not found.");
        }

        // Check current group
        String currentGroupName = GroupRegistry.getGroupName(studentId);

        if (targetGroupName.equals(currentGroupName)) {
            throw new IllegalStateException("Student is already in group '" + targetGroupName + "'");
        }

        // Remove from current group if assigned
        if (currentGroupName != null) {
            Group currentGroup = groupRepo.getByName(currentGroupName);
            if (currentGroup != null) {
                currentGroup.removeStudent(student);
                log.info("Student {} removed from group {}", studentId, currentGroupName);
            }
        }

        // Add to target group
        if (!targetGroup.addStudent(student)) {
            throw new IllegalStateException("Failed to add student to target group.");
        }

        log.info("Student {} transferred to group {}", studentId, targetGroupName);
    }

    /**
     * Adds a grade to a student.
     *
     * @param studentId ID of student
     * @param grade grade value
     * @throws IllegalArgumentException if student not found or grade invalid
     */
    public void addGrade(String studentId, double grade) {
        Student student = studentRepo.getById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }

        student.addGrade(grade);
        log.info("Grade {} added to student {}", grade, student.getIndexNumber());
    }

    /**
     * Removes a grade from a student.
     *
     * @param studentId ID of student
     * @param gradeIndex index of grade to remove
     * @return true if grade was removed, false otherwise
     * @throws IllegalArgumentException if student not found
     */
    public boolean removeGrade(String studentId, int gradeIndex) {
        Student student = studentRepo.getById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }

        boolean removed = student.removeGrade(gradeIndex);
        if (removed) {
            log.info("Grade at index {} removed from student {}", gradeIndex, student.getIndexNumber());
        }
        return removed;
    }

    /**
     * Clears all grades from a student.
     *
     * @param studentId ID of student
     * @throws IllegalArgumentException if student not found
     */
    public void clearGrades(String studentId) {
        Student student = studentRepo.getById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }

        student.clearGrades();
        log.info("All grades cleared from student {}", student.getIndexNumber());
    }

    /**
     * Checks if an index number is already taken.
     *
     * @param indexNumber index number to check
     * @return true if taken, false otherwise
     */
    private boolean isIndexNumberTaken(String indexNumber) {
        return studentRepo.getAll().stream()
            .anyMatch(s -> s.getIndexNumber().equals(indexNumber));
    }

    /**
     * Parses grades from a comma/semicolon/space-separated string.
     *
     * @param gradesText text containing grades
     * @return array of valid grades
     */
    public static double[] parseGrades(String gradesText) {
        if (gradesText == null || gradesText.trim().isEmpty() || gradesText.startsWith("e.g.")) {
            return new double[0];
        }

        String[] gradeStrings = gradesText.split("[,;\\s]+");
        java.util.List<Double> grades = new java.util.ArrayList<>();

        for (String gradeStr : gradeStrings) {
            try {
                double grade = Double.parseDouble(gradeStr.trim());
                grades.add(grade);
            } catch (NumberFormatException e) {
                // Skip invalid grades
                log.warn("Invalid grade format: {}", gradeStr);
            }
        }

        return grades.stream().mapToDouble(Double::doubleValue).toArray();
    }
}
