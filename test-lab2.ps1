# Test script for Lab 2 - Birthday Info

Write-Host "Compiling the project..." -ForegroundColor Cyan
.\gradlew.bat build --quiet

Write-Host "`n=== Test Case 1: Birthday already passed (1997-11-02) ===" -ForegroundColor Yellow
java -cp build\classes\java\main org.example.lab2.Main 1997 11 2

Write-Host "`n=== Test Case 2: Leap year birthday (2000-02-29) ===" -ForegroundColor Yellow
java -cp build\classes\java\main org.example.lab2.Main 2000 2 29

Write-Host "`n=== Test Case 3: Chinese zodiac warning range (2000-02-01) ===" -ForegroundColor Yellow
java -cp build\classes\java\main org.example.lab2.Main 2000 2 1

Write-Host "`n=== Test Case 4: Birthday before Feb 4 (2008-01-15) ===" -ForegroundColor Yellow
java -cp build\classes\java\main org.example.lab2.Main 2008 1 15

Write-Host "`n=== Test Case 5: Birthday after Feb 4 (2008-07-15) ===" -ForegroundColor Yellow
java -cp build\classes\java\main org.example.lab2.Main 2008 7 15

