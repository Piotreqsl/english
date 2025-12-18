@echo off
REM run-lab4.bat - build and run org.example.lab4.GradesStats with given args
echo Building project...
ngradlew.bat build
if errorlevel 1 (
n    echo Build failed
n    exit /b %errorlevel%
n)
necho Running org.example.lab4.GradesStats with args %*
java -cp "build\classes\java\main" org.example.lab4.GradesStats %*
exit /b %errorlevel%

