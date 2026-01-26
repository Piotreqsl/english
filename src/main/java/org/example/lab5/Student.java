package org.example.lab5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a student, extending the Person class with academic information.
 */
public class Student extends Person {
    private static final Logger log = LogManager.getLogger(Student.class);
    private static final Set<Double> VALID_GRADES = new HashSet<>();
    
    static {
        VALID_GRADES.add(2.0);
        VALID_GRADES.add(3.0);
        VALID_GRADES.add(3.5);
        VALID_GRADES.add(4.0);
        VALID_GRADES.add(4.5);
        VALID_GRADES.add(5.0);
    }
    
    private final String indexNumber;
    private final List<Double> grades;
    
    /**
     * Creates a new Student with the given details.
     * 
     * @param firstName the student's first name
     * @param lastName the student's last name
     * @param birthDate the birth date as a string in DD.MM.YYYY format
     * @param gender the student's gender
     * @param indexNumber the student's index number
     */
    public Student(String firstName, String lastName, String birthDate, Gender gender, String indexNumber) {
        super(firstName, lastName, birthDate, gender);
        this.indexNumber = indexNumber;
        this.grades = new ArrayList<>();
        log.info("New Student created: index={} personId={}", indexNumber, getId());
    }
    
    /**
     * Returns the student's index number.
     * 
     * @return the index number
     */
    public String getIndexNumber() {
        return indexNumber;
    }
    
    /**
     * Returns the list of grades.
     * 
     * @return the list of grades
     */
    public List<Double> getGrades() {
        return new ArrayList<>(grades);
    }
    
    /**
     * Adds a grade to the student's record.
     * Only accepts 2.0, 3.0, 3.5, 4.0, 4.5, or 5.0.
     * 
     * @param grade the grade to add
     * @throws IllegalArgumentException if the grade is not valid
     */
    public void addGrade(double grade) {
        if (!VALID_GRADES.contains(grade)) {
            log.error("Attempt to add invalid grade={} for student index={}", grade, indexNumber);
            throw new IllegalArgumentException(
                "Invalid grade: " + grade + ". Valid grades are: 2.0, 3.0, 3.5, 4.0, 4.5, 5.0"
            );
        }
        grades.add(grade);
        log.debug("Added grade={} to student index={} (now {} grades)",
            grade, indexNumber, grades.size());
    }
    
    /**
     * Removes a specific grade at the given index.
     *
     * @param index the index of the grade to remove (0-based)
     * @return true if the grade was removed, false if index is out of bounds
     */
    public boolean removeGrade(int index) {
        if (index < 0 || index >= grades.size()) {
            log.warn("Attempt to remove grade at invalid index={} for student index={}",
                index, indexNumber);
            return false;
        }
        double removedGrade = grades.remove(index);
        log.info("Removed grade={} at position {} from student index={}",
            removedGrade, index, indexNumber);
        return true;
    }

    /**
     * Removes the first occurrence of the specified grade value.
     *
     * @param grade the grade value to remove
     * @return true if the grade was found and removed, false otherwise
     */
    public boolean removeGradeValue(double grade) {
        boolean removed = grades.remove(Double.valueOf(grade));
        if (removed) {
            log.info("Removed grade value={} from student index={}", grade, indexNumber);
        } else {
            log.warn("Grade value={} not found for student index={}", grade, indexNumber);
        }
        return removed;
    }

    /**
     * Clears all grades for this student.
     */
    public void clearGrades() {
        int count = grades.size();
        grades.clear();
        log.info("Cleared {} grades from student index={}", count, indexNumber);
    }

    /**
     * Calculates the average of all grades.
     * 
     * @return OptionalDouble containing the average, or empty if no grades
     */
    public OptionalDouble average() {
        if (grades.isEmpty()) {
            log.trace("Computed average for index={}: no grades", indexNumber);
            return OptionalDouble.empty();
        }
        double sum = 0;
        for (double grade : grades) {
            sum += grade;
        }
        double avg = sum / grades.size();
        log.trace("Computed average for index={}: {}", indexNumber, avg);
        return OptionalDouble.of(avg);
    }
    
    /**
     * Returns a string representation of the student.
     * 
     * @return a formatted string with student details
     */
    @Override
    public String toString() {
        return super.toString() + " [index=" + indexNumber + "]";
    }
}
