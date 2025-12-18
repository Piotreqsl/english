package org.example.lab4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.stream.DoubleStream;

public class GradesStats {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java GradesStats <csv-file>");
            return;
        }

        Path path = Path.of(args[0]);
        if (!Files.exists(path)) {
            System.out.println("File not found: " + path);
            return;
        }

        try (var lines = Files.lines(path)) {
            DoubleStream grades = lines.skip(1)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToDouble(GradesStats::parseGradeFromLine);

            double[] arr = grades.toArray();
            int count = arr.length;
            if (count == 0) {
                System.out.println("No grade records found.");
                return;
            }

            double sum = 0.0;
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (double g : arr) {
                sum += g;
                if (g < min) min = g;
                if (g > max) max = g;
            }

            double avg = sum / count;

            DecimalFormat avgFmt = new DecimalFormat("#0.00");
            DecimalFormat oneFmt = new DecimalFormat("#0.0");

            System.out.println("Average: " + avgFmt.format(avg));
            System.out.println("Highest: " + oneFmt.format(max));
            System.out.println("Lowest: " + oneFmt.format(min));
            System.out.println("Count: " + count);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static double parseGradeFromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 2) return Double.NaN;
        String g = parts[parts.length - 1].trim();
        g = g.replace(',', '.');
        try {
            return Double.parseDouble(g);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}

