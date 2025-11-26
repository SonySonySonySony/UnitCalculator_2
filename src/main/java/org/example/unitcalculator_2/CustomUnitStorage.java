package org.example.unitcalculator_2;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public final class CustomUnitStorage {

    public record CustomUnit(String symbol, String expression) {}

    private static final Path DIR = Paths.get(System.getProperty("user.home"), ".physics-unit-calculator");
    private static final Path FILE = DIR.resolve("custom-units.txt");

    public static List<CustomUnit> loadAll() {

        List<CustomUnit> list = new ArrayList<>();

        if (!Files.exists(FILE)) {
            return list;
        }

        try {
            for (String line : Files.readAllLines(FILE)) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\|", 2);

                if (parts.length != 2) {
                    continue;
                }

                String symbol = parts[0].trim();
                String expr   = parts[1].trim();

                if (!symbol.isEmpty() && !expr.isEmpty()) {
                    list.add(new CustomUnit(symbol, expr));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void add(CustomUnit unit) {
        try {
            if (!Files.exists(DIR)) {
                Files.createDirectories(DIR);
            }

            String line = unit.symbol() + "|" + unit.expression() + System.lineSeparator();

            Files.writeString(
                    FILE,
                    line,
                    Files.exists(FILE)
                            ? StandardOpenOption.APPEND
                            : StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void overwriteAll(List<CustomUnit> units) {
        try {
            if (units.isEmpty()) {
                Files.deleteIfExists(FILE);
                return;
            }

            if (!Files.exists(DIR)) {
                Files.createDirectories(DIR);
            }

            StringBuilder sb = new StringBuilder();

            for (CustomUnit cu : units) {
                sb.append(cu.symbol()).append("|").append(cu.expression()).append(System.lineSeparator());
            }

            Files.writeString(FILE, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CustomUnitStorage() {}
}
