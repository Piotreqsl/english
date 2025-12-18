package org.example.lab3;

/**
 * Lab 3 - Rozwinieice Lab 2
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Lab 3 - Start");

        if (args.length < 1) {
            System.out.println("Usage: java PrimeCheck <N>");
            return;
        }

        long n;
        try {
            n = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid number");
            return;
        }

        if (n <= 1) {
            System.out.println(n + " is not prime");
            System.out.println("No smallest divisor (> 1)");
            return;
        }

        if (n == 2) {
            System.out.println(n + " is prime");
            return;
        }

        if (n % 2 == 0) {
            System.out.println(n + " is not prime");
            System.out.println("Smallest divisor: 2");
            return;
        }

        long sqrtN = (long) Math.sqrt(n);
        for (long i = 3; i <= sqrtN; i += 2) {
            if (n % i == 0) {
                System.out.println(n + " is not prime");
                System.out.println("Smallest divisor: " + i);
                return;
            }
        }

        System.out.println(n + " is prime");
    }
}
