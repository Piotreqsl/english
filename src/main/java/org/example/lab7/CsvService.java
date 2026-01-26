package org.example.lab7;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.lab5.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Service layer for CSV import/export operations.
 * Handles business logic for file operations.
 */
public class CsvService {
    private static final Logger log = LogManager.getLogger(CsvService.class);

    private final StudentRepository studentRepo;
    private final GroupRepository groupRepo;
    private final ConfigManager config;

    public CsvService(StudentRepository studentRepo, GroupRepository groupRepo, ConfigManager config) {
        this.studentRepo = studentRepo;
        this.groupRepo = groupRepo;
        this.config = config;
    }

    /**
     * Loads students from a CSV file.
     *
     * @param filePath path to CSV file
     * @param addToGroup optional group name to add students to
     * @return result containing number of students loaded
     * @throws IOException if file operation fails
     * @throws CsvFormatException if CSV format is invalid
     */
    public CsvImportResult loadStudents(Path filePath, String addToGroup) throws IOException, CsvFormatException {
        log.info("Loading students from CSV: {}", filePath.toAbsolutePath());

        List<Student> students = CsvStudentHandler.loadStudents(filePath, config.getDelimiter());

        int addedToRepo = 0;
        int addedToGroup = 0;
        int skipped = 0;

        Group targetGroup = null;
        if (addToGroup != null && !addToGroup.trim().isEmpty()) {
            targetGroup = groupRepo.getByName(addToGroup);
            if (targetGroup == null) {
                log.warn("Target group not found: {}", addToGroup);
            }
        }

        for (Student student : students) {
            // Check if student already exists (by index number)
            boolean exists = studentRepo.getAll().stream()
                .anyMatch(s -> s.getIndexNumber().equals(student.getIndexNumber()));

            if (exists) {
                log.debug("Skipping duplicate student: {}", student.getIndexNumber());
                skipped++;
                continue;
            }

            studentRepo.add(student);
            addedToRepo++;

            // Add to group if specified
            if (targetGroup != null) {
                if (targetGroup.addStudent(student)) {
                    addedToGroup++;
                }
            }
        }

        log.info("Loaded {} students from CSV (skipped {} duplicates)", addedToRepo, skipped);

        return new CsvImportResult(addedToRepo, addedToGroup, skipped);
    }

    /**
     * Loads groups from a CSV file.
     *
     * @param filePath path to CSV file
     * @return result containing number of groups loaded
     * @throws IOException if file operation fails
     * @throws CsvFormatException if CSV format is invalid
     */
    public CsvImportResult loadGroups(Path filePath) throws IOException, CsvFormatException {
        log.info("Loading groups from CSV: {}", filePath.toAbsolutePath());

        List<Group> groups = CsvGroupHandler.loadGroups(filePath, config.getDelimiter(), studentRepo);

        int added = 0;
        int skipped = 0;

        for (Group group : groups) {
            if (groupRepo.exists(group.getName())) {
                log.debug("Skipping duplicate group: {}", group.getName());
                skipped++;
                continue;
            }

            groupRepo.add(group);
            added++;
        }

        log.info("Loaded {} groups from CSV (skipped {} duplicates)", added, skipped);

        return new CsvImportResult(added, 0, skipped);
    }

    /**
     * Saves students to a CSV file.
     *
     * @param filePath path to CSV file
     * @return number of students saved
     * @throws IOException if file operation fails
     */
    public int saveStudents(Path filePath) throws IOException {
        log.info("Saving {} students to CSV: {}", studentRepo.size(), filePath.toAbsolutePath());

        CsvStudentHandler.saveStudents(studentRepo.getAll(), filePath, config.getDelimiter());

        log.info("Successfully exported {} students", studentRepo.size());
        return studentRepo.size();
    }

    /**
     * Saves groups to a CSV file.
     *
     * @param filePath path to CSV file
     * @return number of groups saved
     * @throws IOException if file operation fails
     */
    public int saveGroups(Path filePath) throws IOException {
        int groupCount = groupRepo.getAll().size();
        log.info("Saving {} groups to CSV: {}", groupCount, filePath.toAbsolutePath());

        CsvGroupHandler.saveGroups(groupRepo.getAll(), filePath, config.getDelimiter());

        log.info("Successfully exported {} groups", groupCount);
        return groupCount;
    }

    /**
     * Result of a CSV import operation.
     */
    public static class CsvImportResult {
        private final int itemsAdded;
        private final int itemsAddedToGroup;
        private final int itemsSkipped;

        public CsvImportResult(int itemsAdded, int itemsAddedToGroup, int itemsSkipped) {
            this.itemsAdded = itemsAdded;
            this.itemsAddedToGroup = itemsAddedToGroup;
            this.itemsSkipped = itemsSkipped;
        }

        public int getItemsAdded() {
            return itemsAdded;
        }

        public int getItemsAddedToGroup() {
            return itemsAddedToGroup;
        }

        public int getItemsSkipped() {
            return itemsSkipped;
        }

        public String getMessage() {
            if (itemsAddedToGroup > 0) {
                return String.format("Loaded %d item(s), added %d to group (skipped %d duplicate(s))",
                    itemsAdded, itemsAddedToGroup, itemsSkipped);
            } else {
                return String.format("Loaded %d item(s) (skipped %d duplicate(s))",
                    itemsAdded, itemsSkipped);
            }
        }
    }
}
