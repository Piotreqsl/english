package org.example.lab5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles CSV operations for groups.
 */
public class CsvGroupHandler {
    private static final Logger log = LogManager.getLogger(CsvGroupHandler.class);

    public static void saveGroups(Collection<Group> groups, Path file, String delimiter) throws IOException {
        List<String> lines = new ArrayList<>();

        for (Group group : groups) {
            String memberIds = group.getMembers().stream()
                .map(Student::getId)
                .collect(Collectors.joining(",", "[", "]"));

            String line = String.join(delimiter,
                group.getName(),
                group.getDescription(),
                memberIds
            );

            lines.add(line);
        }

        Files.write(file, lines);
    }

    public static List<Group> loadGroups(Path file, String delimiter, StudentRepository studentRepo) throws IOException {
        List<Group> groups = new ArrayList<>();
        List<String> lines = Files.readAllLines(file);

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(delimiter, -1);
            if (parts.length < 3) {
                log.warn("Skipping malformed group line (expected 3 fields): {}", line);
                continue;
            }

            try {
                String name = parts[0];
                String description = parts[1];
                String memberIds = parts[2];

                Group group = new Group(name, description);

                // Parse member IDs
                if (memberIds.startsWith("[") && memberIds.endsWith("]")) {
                    String idsContent = memberIds.substring(1, memberIds.length() - 1);
                    if (!idsContent.isEmpty()) {
                        String[] ids = idsContent.split(",");
                        for (String id : ids) {
                            Student student = studentRepo.getById(id.trim());
                            if (student != null) {
                                group.addStudent(student);
                            } else {
                                log.warn("Student with ID {} not found", id.trim());
                            }
                        }
                    }
                }

                groups.add(group);
            } catch (Exception e) {
                log.error("Error parsing group line: {} - {}", line, e.getMessage(), e);
            }
        }

        return groups;
    }
}
