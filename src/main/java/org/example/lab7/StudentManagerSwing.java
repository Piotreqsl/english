package org.example.lab7;

import org.example.lab5.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

/**
 * Main GUI application for Student & Group Manager using Swing (Lab 7).
 * This is the presentation layer built on top of the domain model from Labs 5-6.
 */
public class StudentManagerSwing extends JFrame {
    private static final Logger log = LogManager.getLogger(StudentManagerSwing.class);

    // Domain repositories
    private final StudentRepository studentRepo;
    private final GroupRepository groupRepo;
    private final ConfigManager config;

    // UI Components
    private DefaultListModel<String> groupListModel;
    private JList<String> groupList;
    private DefaultTableModel studentTableModel;
    private JTable studentTable;
    private JLabel statusLabel;
    private JTextField searchField;

    // Form fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField birthDateField;
    private JComboBox<Gender> genderComboBox;
    private JTextField indexNumberField;
    private JTextField gradesField;

    public StudentManagerSwing() {
        log.info("Initializing Swing GUI application");

        // Initialize repositories
        studentRepo = new StudentRepository();
        groupRepo = new GroupRepository();
        config = new ConfigManager();

        // Setup main window
        setTitle("Student & Group Manager — Lab 7");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Build UI
        initializeUI();

        log.info("Swing GUI initialized successfully");
    }

    private void initializeUI() {
        // Menu bar
        setJMenuBar(createMenuBar());

        // Main container with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Left panel: Groups list
        JPanel leftPanel = createGroupsPanel();

        // Center panel: Students table
        JPanel centerPanel = createStudentsPanel();

        // Right panel: Forms and actions (with CardLayout)
        JPanel rightPanel = createActionsPanel();

        // Use JSplitPane for better layout (as recommended in guidelines)
        JSplitPane leftCenterSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
        leftCenterSplit.setDividerLocation(260);
        leftCenterSplit.setResizeWeight(0.2);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCenterSplit, rightPanel);
        mainSplit.setDividerLocation(820);
        mainSplit.setResizeWeight(0.7);

        mainPanel.add(mainSplit, BorderLayout.CENTER);

        // Bottom: Status bar
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem loadStudentsItem = new JMenuItem("Load Students from CSV");
        loadStudentsItem.addActionListener(e -> loadStudentsFromCsv());

        JMenuItem loadGroupsItem = new JMenuItem("Load Groups from CSV");
        loadGroupsItem.addActionListener(e -> loadGroupsFromCsv());

        JMenuItem saveStudentsItem = new JMenuItem("Save Students to CSV");
        saveStudentsItem.addActionListener(e -> saveStudentsToCsv());

        JMenuItem saveGroupsItem = new JMenuItem("Save Groups to CSV");
        saveGroupsItem.addActionListener(e -> saveGroupsToCsv());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            log.info("User exiting application via menu");
            System.exit(0);
        });

        fileMenu.add(loadStudentsItem);
        fileMenu.add(loadGroupsItem);
        fileMenu.addSeparator();
        fileMenu.add(saveStudentsItem);
        fileMenu.add(saveGroupsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createGroupsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(new TitledBorder("Groups"));

        // Groups list
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshStudentTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(groupList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        JButton addGroupButton = new JButton("Add Group");
        addGroupButton.addActionListener(e -> addNewGroup());

        JButton editDescButton = new JButton("Edit Description");
        editDescButton.addActionListener(e -> editGroupDescription());

        JButton removeGroupButton = new JButton("Remove Group");
        removeGroupButton.addActionListener(e -> removeGroup());

        JButton showAllButton = new JButton("Show All Students");
        showAllButton.addActionListener(e -> showAllStudents());

        buttonsPanel.add(addGroupButton);
        buttonsPanel.add(editDescButton);
        buttonsPanel.add(removeGroupButton);
        buttonsPanel.add(showAllButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Students in Selected Group"));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchField = new JTextField();
        searchField.addActionListener(e -> filterStudents());
        JButton searchButton = new JButton("Filter");
        searchButton.addActionListener(e -> filterStudents());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Students table
        String[] columnNames = {"ID", "Index", "First Name", "Last Name", "Birth Date", "Gender", "Average"};
        studentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setReorderingAllowed(false);

        // Double-click to edit student
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editStudent();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JButton editStudentButton = new JButton("Edit Student");
        editStudentButton.addActionListener(e -> editStudent());

        JButton removeStudentButton = new JButton("Remove Student");
        removeStudentButton.addActionListener(e -> removeStudent());

        JButton viewGradesButton = new JButton("View/Edit Grades");
        viewGradesButton.addActionListener(e -> viewEditGrades());

        actionsPanel.add(editStudentButton);
        actionsPanel.add(removeStudentButton);
        actionsPanel.add(viewGradesButton);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(new TitledBorder("Actions"));

        // Add student form
        JPanel addStudentPanel = createAddStudentForm();
        panel.add(addStudentPanel);

        panel.add(Box.createVerticalStrut(10));

        // Transfer student panel
        JPanel transferPanel = createTransferPanel();
        panel.add(transferPanel);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createAddStudentForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Add New Student"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        panel.add(firstNameField, gbc);

        // Last name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(15);
        panel.add(lastNameField, gbc);

        // Birth date
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Birth Date:"), gbc);
        gbc.gridx = 1;
        birthDateField = new JTextField("DD.MM.YYYY", 15);
        panel.add(birthDateField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderComboBox = new JComboBox<>(Gender.values());
        panel.add(genderComboBox, gbc);

        // Index number
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Index Number:"), gbc);
        gbc.gridx = 1;
        indexNumberField = new JTextField(15);
        panel.add(indexNumberField, gbc);

        // Grades
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Grades:"), gbc);
        gbc.gridx = 1;
        gradesField = new JTextField("e.g., 3.5,4.0,5.0", 15);
        panel.add(gradesField, gbc);

        // Add button
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> addNewStudent());
        panel.add(addButton, gbc);

        return panel;
    }

    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Transfer Student"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Move selected student to:"), gbc);

        gbc.gridy = 1; gbc.gridwidth = 2;
        JComboBox<String> targetGroupCombo = new JComboBox<>();
        formPanel.add(targetGroupCombo, gbc);

        gbc.gridy = 2;
        JButton transferButton = new JButton("Transfer");
        transferButton.addActionListener(e -> {
            transferStudent(targetGroupCombo);
        });
        formPanel.add(transferButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Update combo when groups change
        groupList.addListSelectionListener(e -> {
            targetGroupCombo.removeAllItems();
            for (int i = 0; i < groupListModel.size(); i++) {
                targetGroupCombo.addItem(groupListModel.getElementAt(i));
            }
        });

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

        statusLabel = new JLabel(" Ready");
        statusLabel.setBorder(new EmptyBorder(3, 5, 3, 5));
        panel.add(statusLabel, BorderLayout.WEST);

        // Info label with counts
        JLabel infoLabel = new JLabel();
        infoLabel.setBorder(new EmptyBorder(3, 5, 3, 5));
        panel.add(infoLabel, BorderLayout.EAST);

        // Update info periodically
        Timer timer = new Timer(1000, e -> {
            int studentCount = studentRepo.size();
            int groupCount = groupRepo.getAll().size();
            infoLabel.setText(String.format("Students: %d | Groups: %d ", studentCount, groupCount));
        });
        timer.start();

        return panel;
    }

    // ========== GROUP OPERATIONS ==========

    private void showAllStudents() {
        log.debug("User requested to show all students");

        studentTableModel.setRowCount(0);

        if (studentRepo.size() == 0) {
            showInfo("No students in repository. Import students from CSV first.");
            return;
        }

        int unassignedCount = 0;

        for (Student student : studentRepo.getAll()) {
            String groupName = GroupRegistry.getGroupName(student.getId());
            if (groupName == null) {
                unassignedCount++;
            }

            String average = student.average().isPresent()
                ? String.format("%.2f", student.average().getAsDouble())
                : "N/A";

            Object[] row = {
                student.getId(),
                student.getIndexNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getBirthDateString(),
                student.getGender(),
                average
            };

            studentTableModel.addRow(row);
        }

        // Clear group selection
        groupList.clearSelection();

        String message = "Showing all " + studentRepo.size() + " students";
        if (unassignedCount > 0) {
            message += "\n(" + unassignedCount + " students not assigned to any group)";
        }

        setStatus(message);
        log.info("Displayed all {} students ({} unassigned)", studentRepo.size(), unassignedCount);
    }

    private void addNewGroup() {
        log.debug("User requested to add new group");

        String name = JOptionPane.showInputDialog(this, "Enter group name:", "Add Group", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        name = name.trim();

        // Validation: check if group already exists
        if (groupRepo.exists(name)) {
            showError("Group with name '" + name + "' already exists.");
            log.warn("Attempt to create duplicate group: {}", name);
            return;
        }

        String description = JOptionPane.showInputDialog(this, "Enter group description:", "Add Group", JOptionPane.PLAIN_MESSAGE);
        if (description == null) {
            description = "";
        }

        try {
            Group group = new Group(name, description.trim());
            groupRepo.add(group);
            refreshGroupList();
            setStatus("Group '" + name + "' created successfully");
            log.info("Group created via GUI: {}", name);
        } catch (Exception ex) {
            showError("Failed to create group: " + ex.getMessage());
            log.error("Error creating group", ex);
        }
    }

    private void editGroupDescription() {
        String selectedGroupName = groupList.getSelectedValue();
        if (selectedGroupName == null) {
            showWarning("Please select a group first.");
            return;
        }

        Group group = groupRepo.getByName(selectedGroupName);
        if (group == null) {
            showError("Group not found.");
            return;
        }

        String newDescription = JOptionPane.showInputDialog(
            this,
            "Edit description for group '" + selectedGroupName + "':",
            group.getDescription()
        );

        if (newDescription == null) {
            return; // User cancelled
        }

        try {
            // Update the description using the setter
            group.setDescription(newDescription.trim());
            setStatus("Group description updated");
            log.info("Description updated for group: {}", selectedGroupName);

            // Optionally refresh the group list to reflect changes if displayed
            refreshGroupList();
        } catch (Exception ex) {
            showError("Failed to update description: " + ex.getMessage());
            log.error("Error updating group description", ex);
        }
    }

    private void removeGroup() {
        String selectedGroupName = groupList.getSelectedValue();
        if (selectedGroupName == null) {
            showWarning("Please select a group first.");
            return;
        }

        Group group = groupRepo.getByName(selectedGroupName);
        if (group == null) {
            return;
        }

        if (!group.getMembers().isEmpty()) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Group '" + selectedGroupName + "' has " + group.getMembers().size() + " members.\n" +
                "Removing the group will unassign all members. Continue?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            // Remove all students from the group
            new ArrayList<>(group.getMembers()).forEach(group::removeStudent);
        }

        groupRepo.getAll().removeIf(g -> g.getName().equals(selectedGroupName));
        refreshGroupList();
        refreshStudentTable();
        setStatus("Group '" + selectedGroupName + "' removed");
        log.info("Group removed via GUI: {}", selectedGroupName);
    }

    // ========== STUDENT OPERATIONS ==========

    private void addNewStudent() {
        log.debug("User requested to add new student");

        try {
            // Validate and collect data
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String birthDate = birthDateField.getText().trim();
            Gender gender = (Gender) genderComboBox.getSelectedItem();
            String indexNumber = indexNumberField.getText().trim();
            String gradesText = gradesField.getText().trim();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty()) {
                showError("First name and last name are required.");
                return;
            }

            if (indexNumber.isEmpty()) {
                showError("Index number is required.");
                return;
            }

            // Check for duplicate index number
            for (Student s : studentRepo.getAll()) {
                if (s.getIndexNumber().equals(indexNumber)) {
                    showError("Student with index number '" + indexNumber + "' already exists.");
                    return;
                }
            }

            // Create student (this validates birth date format)
            Student student = new Student(firstName, lastName, birthDate, gender, indexNumber);

            // Parse and add grades
            if (!gradesText.isEmpty() && !gradesText.startsWith("e.g.")) {
                String[] gradeStrings = gradesText.split("[,;\\s]+");
                for (String gradeStr : gradeStrings) {
                    try {
                        double grade = Double.parseDouble(gradeStr.trim());
                        student.addGrade(grade);
                    } catch (NumberFormatException e) {
                        showWarning("Invalid grade format: " + gradeStr + " (skipped)");
                    } catch (IllegalArgumentException e) {
                        showError("Invalid grade value: " + e.getMessage());
                        return;
                    }
                }
            }

            // Add to repository
            studentRepo.add(student);

            // Optionally add to selected group
            String selectedGroup = groupList.getSelectedValue();
            if (selectedGroup != null) {
                Group group = groupRepo.getByName(selectedGroup);
                if (group != null) {
                    if (group.addStudent(student)) {
                        setStatus("Student '" + firstName + " " + lastName + "' added to group '" + selectedGroup + "'");
                    } else {
                        setStatus("Student added to repository (could not add to group)");
                    }
                }
            } else {
                setStatus("Student '" + firstName + " " + lastName + "' added to repository");
            }

            // Clear form
            clearStudentForm();

            // Refresh UI
            refreshStudentTable();

            log.info("Student added via GUI: {} {} (index: {})", firstName, lastName, indexNumber);

        } catch (DateTimeParseException e) {
            showError("Invalid birth date format. Use DD.MM.YYYY (e.g., 15.03.2000)");
            log.warn("Invalid birth date format entered", e);
        } catch (Exception e) {
            showError("Error adding student: " + e.getMessage());
            log.error("Error adding student via GUI", e);
        }
    }

    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a student first.");
            return;
        }

        String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
        Student student = studentRepo.getById(studentId);

        if (student == null) {
            showError("Student not found.");
            return;
        }

        // Create edit dialog
        JDialog dialog = new JDialog(this, "Edit Student", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        JTextField firstNameEdit = new JTextField(student.getFirstName(), 20);
        formPanel.add(firstNameEdit, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        JTextField lastNameEdit = new JTextField(student.getLastName(), 20);
        formPanel.add(lastNameEdit, gbc);

        // Birth Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Birth Date:"), gbc);
        gbc.gridx = 1;
        JTextField birthDateEdit = new JTextField(student.getBirthDateString(), 20);
        formPanel.add(birthDateEdit, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        JComboBox<Gender> genderEdit = new JComboBox<>(Gender.values());
        genderEdit.setSelectedItem(student.getGender());
        formPanel.add(genderEdit, gbc);

        // Index Number
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Index Number:"), gbc);
        gbc.gridx = 1;
        JTextField indexEdit = new JTextField(student.getIndexNumber(), 20);
        formPanel.add(indexEdit, gbc);

        // Info label
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><i>Note: Editing creates a new student with the same grades.<br>Use 'View/Edit Grades' to modify grades.</i></html>");
        infoLabel.setFont(infoLabel.getFont().deriveFont(10f));
        formPanel.add(infoLabel, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String newFirstName = firstNameEdit.getText().trim();
                String newLastName = lastNameEdit.getText().trim();
                String newBirthDate = birthDateEdit.getText().trim();
                Gender newGender = (Gender) genderEdit.getSelectedItem();
                String newIndex = indexEdit.getText().trim();

                // Validation
                if (newFirstName.isEmpty() || newLastName.isEmpty() || newBirthDate.isEmpty() || newIndex.isEmpty()) {
                    showError("All fields are required.");
                    return;
                }

                // Check if index number changed and is already taken
                if (!newIndex.equals(student.getIndexNumber())) {
                    for (Student s : studentRepo.getAll()) {
                        if (s.getIndexNumber().equals(newIndex) && !s.getId().equals(studentId)) {
                            showError("Index number already exists: " + newIndex);
                            return;
                        }
                    }
                }

                // Get current group membership
                String groupName = GroupRegistry.getGroupName(studentId);
                Group currentGroup = null;
                if (groupName != null) {
                    currentGroup = groupRepo.getByName(groupName);
                }

                // Remove old student from group
                if (currentGroup != null) {
                    currentGroup.removeStudent(student);
                }

                // Create new student with updated data
                Student updatedStudent = new Student(newFirstName, newLastName, newBirthDate, newGender, newIndex);

                // Copy grades from old student
                for (Double grade : student.getGrades()) {
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

                refreshStudentTable();
                setStatus("Student updated successfully");
                log.info("Student updated: old index={}, new index={}", student.getIndexNumber(), newIndex);
                dialog.dispose();

            } catch (DateTimeParseException ex) {
                showError("Invalid birth date format. Use DD.MM.YYYY (e.g., 15.03.2000)");
            } catch (Exception ex) {
                showError("Failed to update student: " + ex.getMessage());
                log.error("Error updating student", ex);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void removeStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a student first.");
            return;
        }

        String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
        Student student = studentRepo.getById(studentId);

        if (student == null) {
            showError("Student not found.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
            this,
            "Remove student '" + student.getFirstName() + " " + student.getLastName() + "'?",
            "Confirm Remove",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
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
        studentRepo.getAll().removeIf(s -> s.getId().equals(studentId));

        refreshStudentTable();
        setStatus("Student removed");
        log.info("Student removed via GUI: {} {}", student.getFirstName(), student.getLastName());
    }

    private void viewEditGrades() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a student first.");
            return;
        }

        String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
        Student student = studentRepo.getById(studentId);

        if (student == null) {
            showError("Student not found.");
            return;
        }

        // Create grades dialog
        JDialog dialog = new JDialog(this, "Grades - " + student.getFirstName() + " " + student.getLastName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        // Current grades list
        DefaultListModel<String> gradesListModel = new DefaultListModel<>();
        for (Double grade : student.getGrades()) {
            gradesListModel.addElement(String.format("%.1f", grade));
        }

        JList<String> gradesList = new JList<>(gradesListModel);
        gradesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(gradesList);
        scrollPane.setBorder(new TitledBorder("Current Grades"));

        // Add grade panel
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setBorder(new TitledBorder("Add Grade"));
        JTextField newGradeField = new JTextField(10);
        JButton addGradeButton = new JButton("Add");

        // Average panel
        JPanel avgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel avgLabel = new JLabel();

        // Helper method to update average label
        Runnable updateAverage = () -> {
            if (student.average().isPresent()) {
                avgLabel.setText(String.format("Average: %.2f", student.average().getAsDouble()));
            } else {
                avgLabel.setText("Average: N/A");
            }
        };

        updateAverage.run(); // Initial update
        avgPanel.add(avgLabel);

        addGradeButton.addActionListener(e -> {
            try {
                double grade = Double.parseDouble(newGradeField.getText().trim());
                student.addGrade(grade);
                gradesListModel.addElement(String.format("%.1f", grade));
                newGradeField.setText("");
                updateAverage.run();
                refreshStudentTable();
                log.info("Grade {} added to student {}", grade, student.getIndexNumber());
            } catch (NumberFormatException ex) {
                showError("Invalid grade format.");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });

        addPanel.add(new JLabel("Grade:"));
        addPanel.add(newGradeField);
        addPanel.add(addGradeButton);
        addPanel.add(new JLabel("(Valid: 2.0, 3.0, 3.5, 4.0, 4.5, 5.0)"));

        // Remove grade panel
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        removePanel.setBorder(new TitledBorder("Remove Selected Grade"));
        JButton removeGradeButton = new JButton("Remove Selected");
        JButton clearAllGradesButton = new JButton("Clear All");

        removeGradeButton.addActionListener(e -> {
            int selectedIndex = gradesList.getSelectedIndex();
            if (selectedIndex == -1) {
                showWarning("Please select a grade to remove.");
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                dialog,
                "Remove grade " + gradesListModel.getElementAt(selectedIndex) + "?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                if (student.removeGrade(selectedIndex)) {
                    gradesListModel.remove(selectedIndex);
                    updateAverage.run();
                    refreshStudentTable();
                    log.info("Grade at index {} removed from student {}", selectedIndex, student.getIndexNumber());
                } else {
                    showError("Failed to remove grade.");
                }
            }
        });

        clearAllGradesButton.addActionListener(e -> {
            if (gradesListModel.isEmpty()) {
                showWarning("No grades to clear.");
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                dialog,
                "Clear all " + gradesListModel.getSize() + " grades?",
                "Confirm Clear All",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                student.clearGrades();
                gradesListModel.clear();
                updateAverage.run();
                refreshStudentTable();
                log.info("All grades cleared from student {}", student.getIndexNumber());
            }
        });

        removePanel.add(removeGradeButton);
        removePanel.add(clearAllGradesButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(addPanel, BorderLayout.NORTH);
        topPanel.add(removePanel, BorderLayout.CENTER);
        topPanel.add(avgPanel, BorderLayout.SOUTH);

        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void transferStudent(JComboBox<String> targetGroupCombo) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a student first.");
            return;
        }

        String targetGroupName = (String) targetGroupCombo.getSelectedItem();
        if (targetGroupName == null) {
            showWarning("Please select a target group.");
            return;
        }

        String studentId = (String) studentTableModel.getValueAt(selectedRow, 0);
        Student student = studentRepo.getById(studentId);

        if (student == null) {
            showError("Student not found.");
            return;
        }

        Group targetGroup = groupRepo.getByName(targetGroupName);
        if (targetGroup == null) {
            showError("Target group not found.");
            return;
        }

        // Check current group
        String currentGroupName = GroupRegistry.getGroupName(studentId);

        if (targetGroupName.equals(currentGroupName)) {
            showInfo("Student is already in group '" + targetGroupName + "'");
            return;
        }

        try {
            // Remove from current group if assigned
            if (currentGroupName != null) {
                Group currentGroup = groupRepo.getByName(currentGroupName);
                if (currentGroup != null) {
                    currentGroup.removeStudent(student);
                    log.info("Student {} removed from group {}", studentId, currentGroupName);
                }
            }

            // Add to target group
            if (targetGroup.addStudent(student)) {
                setStatus("Student transferred to group '" + targetGroupName + "'");
                log.info("Student {} transferred to group {}", studentId, targetGroupName);
                refreshStudentTable();
            } else {
                showError("Failed to add student to target group.");
            }

        } catch (Exception ex) {
            showError("Transfer failed: " + ex.getMessage());
            log.error("Error transferring student", ex);
        }
    }

    private void filterStudents() {
        String searchText = searchField.getText().trim().toLowerCase();
        refreshStudentTable(searchText);
    }

    // ========== CSV OPERATIONS ==========

    private void loadStudentsFromCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        fileChooser.setCurrentDirectory(new File("."));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                log.info("Loading students from CSV: {}", file.getAbsolutePath());
                List<Student> students = CsvStudentHandler.loadStudents(
                    file.toPath(),
                    config.getDelimiter()
                );

                int addedToRepo = 0;
                int addedToGroup = 0;

                // Get currently selected group (if any)
                String selectedGroupName = groupList.getSelectedValue();
                Group selectedGroup = null;
                if (selectedGroupName != null) {
                    selectedGroup = groupRepo.getByName(selectedGroupName);
                }

                for (Student student : students) {
                    // Add to repository
                    studentRepo.add(student);
                    addedToRepo++;

                    // If a group is selected, try to add student to it
                    if (selectedGroup != null) {
                        if (selectedGroup.addStudent(student)) {
                            addedToGroup++;
                        }
                    }
                }

                refreshStudentTable();

                // Build informative message
                StringBuilder message = new StringBuilder();
                message.append("Imported ").append(students.size()).append(" students from ").append(file.getName());

                if (selectedGroup != null) {
                    message.append("\n\nAdded ").append(addedToGroup).append(" students to group '")
                           .append(selectedGroupName).append("'");
                    if (addedToGroup < students.size()) {
                        message.append("\n(").append(students.size() - addedToGroup)
                               .append(" students were already assigned to other groups)");
                    }
                } else {
                    message.append("\n\nNote: No group selected. Students are in repository but not assigned to any group.")
                           .append("\nSelect a group and use 'Transfer' to assign them.");
                }

                showInfo(message.toString());
                setStatus("Imported " + students.size() + " students");
                log.info("Successfully imported {} students ({} added to group)", students.size(), addedToGroup);

            } catch (IOException ex) {
                showError("Failed to load file: " + ex.getMessage());
                log.error("Error loading students from CSV", ex);
            } catch (Exception ex) {
                showError("Error importing students: " + ex.getMessage());
                log.error("Error importing students", ex);
            }
        }
    }

    private void loadGroupsFromCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        fileChooser.setCurrentDirectory(new File("."));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                log.info("Loading groups from CSV: {}", file.getAbsolutePath());
                List<Group> groups = CsvGroupHandler.loadGroups(
                    file.toPath(),
                    config.getDelimiter(),
                    studentRepo
                );

                for (Group group : groups) {
                    groupRepo.add(group);
                }

                refreshGroupList();
                refreshStudentTable();
                showInfo("Imported " + groups.size() + " groups from " + file.getName());
                setStatus("Imported " + groups.size() + " groups");
                log.info("Successfully imported {} groups", groups.size());

            } catch (IOException ex) {
                showError("Failed to load file: " + ex.getMessage());
                log.error("Error loading groups from CSV", ex);
            } catch (Exception ex) {
                showError("Error importing groups: " + ex.getMessage());
                log.error("Error importing groups", ex);
            }
        }
    }

    private void saveStudentsToCsv() {
        if (studentRepo.size() == 0) {
            showWarning("No students to export.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setSelectedFile(new File("students.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Confirm overwrite
            if (file.exists()) {
                int result = JOptionPane.showConfirmDialog(
                    this,
                    "File already exists. Overwrite?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION
                );
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                log.info("Saving students to CSV: {}", file.getAbsolutePath());
                CsvStudentHandler.saveStudents(
                    studentRepo.getAll(),
                    file.toPath(),
                    config.getDelimiter()
                );

                showInfo("Exported " + studentRepo.size() + " students to " + file.getName());
                setStatus("Exported " + studentRepo.size() + " students");
                log.info("Successfully exported {} students", studentRepo.size());

            } catch (IOException ex) {
                showError("Failed to save file: " + ex.getMessage());
                log.error("Error saving students to CSV", ex);
            }
        }
    }

    private void saveGroupsToCsv() {
        if (groupRepo.getAll().isEmpty()) {
            showWarning("No groups to export.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setSelectedFile(new File("groups.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Confirm overwrite
            if (file.exists()) {
                int result = JOptionPane.showConfirmDialog(
                    this,
                    "File already exists. Overwrite?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION
                );
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                log.info("Saving groups to CSV: {}", file.getAbsolutePath());
                CsvGroupHandler.saveGroups(
                    groupRepo.getAll(),
                    file.toPath(),
                    config.getDelimiter()
                );

                int groupCount = groupRepo.getAll().size();
                showInfo("Exported " + groupCount + " groups to " + file.getName());
                setStatus("Exported " + groupCount + " groups");
                log.info("Successfully exported {} groups", groupCount);

            } catch (IOException ex) {
                showError("Failed to save file: " + ex.getMessage());
                log.error("Error saving groups to CSV", ex);
            }
        }
    }

    // ========== UI REFRESH ==========

    private void refreshGroupList() {
        groupListModel.clear();
        for (Group group : groupRepo.getAll()) {
            groupListModel.addElement(group.getName());
        }
    }

    private void refreshStudentTable() {
        refreshStudentTable("");
    }

    private void refreshStudentTable(String filter) {
        studentTableModel.setRowCount(0);

        String selectedGroupName = groupList.getSelectedValue();

        // If no group is selected, show all students (fixes the issue after CSV load)
        if (selectedGroupName == null) {
            // Show all students from repository
            for (Student student : studentRepo.getAll()) {
                // Apply filter
                if (!filter.isEmpty()) {
                    String searchText = (student.getFirstName() + " " + student.getLastName() + " " +
                                        student.getIndexNumber()).toLowerCase();
                    if (!searchText.contains(filter)) {
                        continue;
                    }
                }

                String average = student.average().isPresent()
                    ? String.format("%.2f", student.average().getAsDouble())
                    : "N/A";

                Object[] row = {
                    student.getId(),
                    student.getIndexNumber(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getBirthDateString(),
                    student.getGender(),
                    average
                };

                studentTableModel.addRow(row);
            }
            return;
        }

        // Show students from selected group
        Group group = groupRepo.getByName(selectedGroupName);
        if (group == null) {
            return;
        }

        for (Student student : group.getMembers()) {
            // Apply filter
            if (!filter.isEmpty()) {
                String searchText = (student.getFirstName() + " " + student.getLastName() + " " +
                                    student.getIndexNumber()).toLowerCase();
                if (!searchText.contains(filter)) {
                    continue;
                }
            }

            String average = student.average().isPresent()
                ? String.format("%.2f", student.average().getAsDouble())
                : "N/A";

            Object[] row = {
                student.getId(),
                student.getIndexNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getBirthDateString(),
                student.getGender(),
                average
            };

            studentTableModel.addRow(row);
        }
    }

    private void clearStudentForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        birthDateField.setText("DD.MM.YYYY");
        indexNumberField.setText("");
        gradesField.setText("e.g., 3.5,4.0,5.0");
        genderComboBox.setSelectedIndex(0);
    }

    // ========== DIALOGS ==========

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String message = "Student & Group Manager\n" +
                         "Lab 7 - Object-Oriented Programming (GUI)\n\n" +
                         "Built with Java Swing\n" +
                         "Domain model from Labs 5-6\n\n" +
                         "Features:\n" +
                         "- Student and group management\n" +
                         "- CSV import/export\n" +
                         "- Transfer students between groups\n" +
                         "- Grade management with validation\n" +
                         "- Search and filter functionality";

        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setStatus(String message) {
        statusLabel.setText(" " + message);
        log.debug("Status: {}", message);
    }

    // ========== MAIN ==========

    public static void main(String[] args) {
        log.info("Starting Student Manager Swing application");

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.warn("Could not set system look and feel", e);
        }

        SwingUtilities.invokeLater(() -> {
            StudentManagerSwing app = new StudentManagerSwing();
            app.setVisible(true);
        });
    }
}
