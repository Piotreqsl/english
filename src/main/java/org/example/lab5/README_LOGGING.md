# Lab 5 - Student Management System with Log4j 2

This lab implements a complete student and group management system with comprehensive logging using Apache Log4j 2.

## 📁 Project Structure

```
src/main/java/org/example/lab5/
├── Gender.java                 - Enum for gender types
├── Person.java                 - Base class with ID generation and logging
├── Student.java                - Student class with grades and logging
├── Group.java                  - Group management with CSV I/O and logging
├── GroupRegistry.java          - Registry to track student-group assignments
├── CsvFormatException.java     - Custom exception for CSV format errors
├── ConsoleApp.java             - Main console application with logging
├── DemoLoggingBasic.java       - Demo: Log4j 2 without configuration
├── DemoLoggingXml.java         - Demo: Log4j 2 with XML configuration
├── DemoPerson.java             - Demo: Person class
├── DemoStudent.java            - Demo: Student class
├── DemoGroup.java              - Demo: Group class
└── DemoSortStats.java          - Demo: Sorting and statistics

src/main/resources/
└── log4j2.xml                  - Log4j 2 configuration file
```

## ✨ Logging Features

### Task 1: Getting Started with Log4j 2
- **Dependencies**: Added Log4j 2 API and Core to `build.gradle`
- **Configuration**: XML-based configuration with console and file appenders
- **Demo Classes**: 
  - `DemoLoggingBasic` - Shows fallback configuration
  - `DemoLoggingXml` - Shows XML-configured logging

### Task 2: Logging in Person and Student
- **Person class**:
  - Logs constructor calls with DEBUG level
  - Logs ID generation with TRACE level
  - Logs age computation with TRACE level
  
- **Student class**:
  - Logs constructor with INFO level (student creation)
  - Logs grade additions with DEBUG level
  - Logs invalid grade attempts with ERROR level
  - Logs average calculation with TRACE level

### Task 3: Logging in Group & GroupRegistry
- **GroupRegistry**:
  - Logs student assignment with INFO level
  - Logs unassignment with DEBUG level
  - Logs invalid unassignment attempts with WARN level
  
- **Group**:
  - Logs group creation with INFO level
  - Logs student additions with INFO level
  - Logs failed additions (already in another group) with WARN level
  - Logs student removal with INFO/WARN levels

### Task 4: Logging CSV Import/Export + CsvFormatException
- **CsvFormatException**: Custom checked exception for CSV format errors
- **Export logging**:
  - Logs export start with file path and count (INFO)
  - Logs each CSV line (DEBUG)
  - Logs export completion (INFO)
  - Logs I/O errors (ERROR)
  
- **Import logging**:
  - Logs import start with file path (INFO)
  - Logs each raw CSV line (DEBUG)
  - Throws CsvFormatException for malformed lines
  - Logs import completion with count (INFO)

### Task 5: Console App with Global Exception Handler
- **Main application**:
  - Global try-catch in `main()` method
  - Logs application startup/shutdown (INFO)
  - Logs menu selections (DEBUG)
  - Logs file operations (INFO)
  - Logs user actions (INFO)
  - Logs errors with full stack traces (ERROR)

### Task 6: Advanced Configuration (log4j2.xml)
- **Console Appender**: Formatted output to console
- **RollingFile Appender**: 
  - Logs stored in `logs/app.log`
  - Automatic rotation at 5 MB
  - Keeps up to 10 archived files
  - Compressed archives (.gz format)
  
- **Per-package Levels**:
  - `org.example.lab5` package: DEBUG level
  - Root logger: INFO level

## 🚀 Running the Application

### Compile the project
```bash
.\gradlew build
```

### Run demo programs
```bash
# Basic logging demo
.\gradlew run -PmainClass=org.example.lab5.DemoLoggingBasic

# XML-configured logging demo
.\gradlew run -PmainClass=org.example.lab5.DemoLoggingXml

# Person class demo
.\gradlew run -PmainClass=org.example.lab5.DemoPerson

# Student class demo
.\gradlew run -PmainClass=org.example.lab5.DemoStudent

# Group class demo
.\gradlew run -PmainClass=org.example.lab5.DemoGroup

# Sorting and statistics demo
.\gradlew run -PmainClass=org.example.lab5.DemoSortStats

# Main console application
.\gradlew run -PmainClass=org.example.lab5.ConsoleApp
```

## 📊 Log Levels Used

| Level | Usage | Examples |
|-------|-------|----------|
| **TRACE** | Very detailed information | ID generation, age calculation, average computation |
| **DEBUG** | Developer information | Object creation, method calls, CSV lines |
| **INFO** | High-level information | Student/group creation, file operations, application lifecycle |
| **WARN** | Suspicious situations | Failed operations, invalid attempts, missing files |
| **ERROR** | Operation failures | Invalid grades, CSV format errors, I/O failures |
| **FATAL** | Application crashes | (Not used in this lab) |

## 📝 Configuration Properties

The application uses `console.properties` for configuration:
- `delimiter` - CSV delimiter (default: `;`)
- `students` - Student CSV filename (default: `students.csv`)
- `groups` - Groups CSV filename (default: `groups.csv`)

## 🔍 Viewing Logs

Logs are written to two locations:
1. **Console**: Real-time output with simplified format
2. **File**: `logs/app.log` with detailed timestamps and package names

To view log files:
```bash
# View current log
cat logs/app.log

# View archived logs
ls logs/
```

## 🎯 Key Learning Outcomes

1. ✅ Understand logging framework advantages over `System.out.println()`
2. ✅ Configure Log4j 2 with XML
3. ✅ Use appropriate log levels for different scenarios
4. ✅ Implement custom exceptions with logging
5. ✅ Create global exception handlers
6. ✅ Configure rolling file appenders
7. ✅ Set per-package log levels
8. ✅ Integrate logging throughout an OOP application

## 📚 References

- [Apache Log4j 2 Documentation](https://logging.apache.org/log4j/2.x/)
- [Log4j 2 Configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html)
- [Pattern Layout](https://logging.apache.org/log4j/2.x/manual/layouts.html#PatternLayout)
