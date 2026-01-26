# Changelog - Student & Group Manager

## Lab 7 - GUI Implementation (2026-01-26)

### ✅ New Features

#### Backend Enhancements:
- **Group.setDescription(String)** - Added ability to edit group description
- **Student.removeGrade(int index)** - Remove grade at specific position
- **Student.removeGradeValue(double grade)** - Remove first occurrence of grade value
- **Student.clearGrades()** - Clear all grades from student
- **StudentRepository.remove(String id)** - Remove student by ID
- **StudentRepository.update(String id, Student student)** - Update student in repository

#### GUI Features:
- **Full Student Editor** - Edit all student fields (name, birth date, gender, index)
  - Creates new student object preserving grades (immutable pattern)
  - Maintains group membership
  - Full validation for all fields
  - Index number uniqueness check

- **Enhanced Grades Management**
  - Add grades with validation
  - Remove selected grade with confirmation
  - Clear all grades with warning dialog
  - Live average calculation
  - Visual feedback for all operations

- **Improved Group Management**
  - Edit group description (now fully functional)
  - Real-time UI updates

- **Better UI Architecture**
  - JSplitPane for resizable panels (as per guidelines)
  - Proper separation of concerns
  - All recommendations from instructor guidelines implemented

### 🐛 Bug Fixes
- **Fixed student display after CSV import** - Students now appear in table even when no group is selected
- **Fixed refreshStudentTable()** - Now shows all students when no group is selected

### 📚 Documentation
- Updated README_LAB7.md with all new features
- Added usage guide for new edit features
- Updated architecture section with backend methods
- Removed outdated limitations
- Added comprehensive testing checklist

### ✔️ Guidelines Compliance
All instructor guidelines for Swing implementation are now verified:
- ✅ JSplitPane for layout (3-column design)
- ✅ JList for groups with DefaultListModel
- ✅ JTable for students with DefaultTableModel
- ✅ JScrollPane for scrollable areas
- ✅ GridBagLayout for forms
- ✅ JComboBox for dropdowns
- ✅ JOptionPane for dialogs
- ✅ JFileChooser for CSV operations
- ✅ BorderLayout, BoxLayout, FlowLayout properly used
- ✅ All functional requirements implemented
- ✅ Domain logic separation maintained
- ✅ Exception handling with user-friendly dialogs

### 🎯 Requirements Status
| Task | Status | Compliance |
|------|--------|-----------|
| TASK 1: Functional Requirements | ✅ Complete | 100% |
| TASK 2: Swing Implementation | ✅ Complete | 100% |
| TASK 3: Validation & UX | ✅ Complete | 100% |
| TASK 4: CSV Import/Export | ✅ Complete | 100% |
| TASK 5: Advanced Features | ✅ Complete | 100% |

### 📦 Files Modified
- `src/main/java/org/example/lab5/Student.java`
- `src/main/java/org/example/lab5/Group.java`
- `src/main/java/org/example/lab5/StudentRepository.java`
- `src/main/java/org/example/lab7/StudentManagerSwing.java`
- `src/main/java/org/example/lab7/README_LAB7.md`

### 🚀 How to Use New Features

#### Edit Student:
1. Select student in table
2. Click "Edit Student" button or double-click row
3. Modify any field (name, birth date, gender, index)
4. Click "Save"
5. Student is recreated with new data, preserving grades and group

#### Manage Grades:
1. Select student in table
2. Click "View/Edit Grades"
3. To add: enter grade value, click "Add"
4. To remove: select grade in list, click "Remove Selected"
5. To clear all: click "Clear All" (with confirmation)
6. Average updates automatically

#### Edit Group Description:
1. Select group in list
2. Click "Edit Description"
3. Enter new description
4. Changes are saved immediately

---

## Previous Labs

### Lab 6 - Console Application
- ConsoleApp with menu-driven interface
- CSV import/export for students and groups
- Configuration management
- Logging with Log4j2

### Lab 5 - Domain Model
- Person and Student classes
- Group and GroupRegistry
- Grades management
- CSV handlers

### Labs 1-4
- Basic Java concepts
- File I/O
- Collections
- Exception handling
