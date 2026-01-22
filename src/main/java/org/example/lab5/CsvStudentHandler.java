package org.example.lab5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles CSV operations for students.
 */
public class CsvStudentHandler {
    private static final Logger log = LogManager.getLogger(CsvStudentHandler.class);

    public static void saveStudents(Collection<Student> students, Path file, String delimiter) throws IOException {
        List<String> lines = new ArrayList<>();

        for (Student student : students) {
            String gradesString = student.getGrades().stream()
                .map(g -> String.format(Locale.US, "%.1f", g))
                .collect(Collectors.joining(",", "[", "]"));

            String line = String.join(delimiter,
                student.getId(),
                student.getIndexNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getBirthDateString(),
                student.getGender().toString(),
                gradesString
            );

            lines.add(line);
        }

        Files.write(file, lines);
    }

    public static List<Student> loadStudents(Path file, String delimiter) throws IOException {
        List<Student> students = new ArrayList<>();
        List<String> lines = Files.readAllLines(file);

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(delimiter, -1);
            if (parts.length < 7) {
                log.warn("Skipping malformed line (expected 7 fields): {}", line);
                continue;
            }

            try {
                String indexNumber = parts[1];
                String firstName = parts[2];
                String lastName = parts[3];
                String birthDate = parts[4];
                Gender gender = Gender.valueOf(parts[5]);
                String gradesString = parts.length > 6 ? parts[6] : "[]";

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
                                log.warn("Invalid grade '{}' in line: {}", gradeStr, line);
                            }
                        }
                    }
                }

                students.add(student);
            } catch (Exception e) {
                log.error("Error parsing line: {} - {}", line, e.getMessage(), e);
            }
        }

        return students;
    }
}
