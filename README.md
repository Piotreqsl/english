# Projekt Laboratoria - Java

Projekt składa się z 4 laboratoriów (lab1-lab4), gdzie każde kolejne jest rozwinięciem poprzedniego.

## Struktura projektu

```
src/main/java/org/example/
├── Main.java          # Główne menu do uruchamiania laboratoriów
├── lab1/
│   └── Main.java      # Lab 1 - Podstawowa wersja
├── lab2/
│   └── Main.java      # Lab 2 - Rozwinięcie Lab 1
├── lab3/
│   └── Main.java      # Lab 3 - Rozwinięcie Lab 2
└── lab4/
    └── Main.java      # Lab 4 - Rozwinięcie Lab 3
```

## Jak uruchomić projekt

### Metoda 1: Przez menu główne (interaktywnie)
```powershell
.\gradlew run --console=plain
```
Następnie wybierz numer laboratorium (1-4) lub 0 aby wyjść.

### Metoda 2: Bezpośrednie uruchomienie konkretnego laboratorium

**Lab 1:**
```powershell
.\gradlew run --console=plain -PmainClass=org.example.lab1.Main
```

**Lab 2:**
```powershell
.\gradlew run --console=plain -PmainClass=org.example.lab2.Main
```

**Lab 3:**
```powershell
.\gradlew run --console=plain -PmainClass=org.example.lab3.Main
```

**Lab 4:**
```powershell
.\gradlew run --console=plain -PmainClass=org.example.lab4.Main
```

## Kompilacja projektu

```powershell
.\gradlew build
```

## Czyszczenie projektu

```powershell
.\gradlew clean
```

## Jak pracować z laboratoriami

1. Każde laboratorium ma własny pakiet (lab1, lab2, lab3, lab4)
2. Każde laboratorium ma własną klasę Main z metodą main()
3. Możesz dodawać dodatkowe klasy w odpowiednich pakietach
4. Laboratorium 2 może importować klasy z lab1
5. Laboratorium 3 może importować klasy z lab1 i lab2
6. Laboratorium 4 może importować klasy z lab1, lab2 i lab3

## Przykład rozbudowy

Jeśli chcesz dodać np. klasę pomocniczą w lab1:

```java
// src/main/java/org/example/lab1/Helper.java
package org.example.lab1;

public class Helper {
    // Twój kod
}
```

A następnie użyć jej w lab2:

```java
// src/main/java/org/example/lab2/Main.java
package org.example.lab2;

import org.example.lab1.Helper;

public class Main {
    public static void main(String[] args) {
        Helper helper = new Helper();
        // Twój kod
    }
}
```

