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
