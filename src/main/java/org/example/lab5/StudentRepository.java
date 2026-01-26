package org.example.lab5;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository for managing students in memory.
 */
public class StudentRepository {
    private final Map<String, Student> students = new HashMap<>();

    public void add(Student student) {
        students.put(student.getId(), student);
    }

    /**
     * Updates a student in the repository by ID.
     * Replaces the existing student with the new one.
     *
     * @param id the ID of the student to replace
     * @param student the new student object
     * @return true if student was found and updated, false otherwise
     */
    public boolean update(String id, Student student) {
        if (students.containsKey(id)) {
            students.put(id, student);
            return true;
        }
        return false;
    }

    /**
     * Removes a student from the repository by ID.
     *
     * @param id the ID of the student to remove
     * @return the removed student, or null if not found
     */
    public Student remove(String id) {
        return students.remove(id);
    }

    public Student getById(String id) {
        return students.get(id);
    }

    public Collection<Student> getAll() {
        return students.values();
    }

    public void clear() {
        students.clear();
    }

    public int size() {
        return students.size();
    }
}
