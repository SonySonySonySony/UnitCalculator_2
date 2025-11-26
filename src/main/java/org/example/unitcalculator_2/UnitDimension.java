package org.example.unitcalculator_2;

import java.util.Objects;

public final class UnitDimension {
    private final int mass;        // kg
    private final int length;      // m
    private final int current;     // A
    private final int temperature; // K
    private final int amount;      // mol
    private final int time;        // s

    public static final UnitDimension DIMENSIONLESS = new UnitDimension(0, 0, 0, 0, 0, 0);

    public UnitDimension(int length, int mass, int time, int current, int temperature, int amount) {
        this.length = length;
        this.mass = mass;
        this.time = time;
        this.current = current;
        this.temperature = temperature;
        this.amount = amount;
    }

    public UnitDimension multiply(UnitDimension other) {
        return new UnitDimension(
                this.length + other.length,
                this.mass + other.mass,
                this.time + other.time,
                this.current + other.current,
                this.temperature + other.temperature,
                this.amount + other.amount
        );
    }

    public UnitDimension divide(UnitDimension other) {
        return new UnitDimension(
                this.length - other.length,
                this.mass - other.mass,
                this.time - other.time,
                this.current - other.current,
                this.temperature - other.temperature,
                this.amount - other.amount
        );
    }


    public UnitDimension pow(int exponent) {
        return new UnitDimension(
                this.length * exponent,
                this.mass * exponent,
                this.time * exponent,
                this.current * exponent,
                this.temperature * exponent,
                this.amount * exponent
        );
    }

    public boolean isDimensionless() {
        return this.equals(DIMENSIONLESS);
    }

    public String toBaseString() {
        StringBuilder num = new StringBuilder();
        StringBuilder den = new StringBuilder();

        appendUnit(num, den, "kg", mass);
        appendUnit(num, den, "m", length);
        appendUnit(num, den, "A", current);
        appendUnit(num, den, "K", temperature);
        appendUnit(num, den, "mol", amount);
        appendUnit(num, den, "s", time);


        if (num.length() == 0 && den.length() == 0) {
            return "1";
        }

        String numerator = (num.length() == 0) ? "1" : num.toString();
        if (den.length() == 0) {
            return numerator;
        } else {
            return numerator + "/" + den;
        }
    }

    private void appendUnit(StringBuilder num, StringBuilder den, String symbol, int exp) {
        if (exp == 0) {
            return;
        }

        int abs = Math.abs(exp);
        StringBuilder target = (exp > 0 ? num : den);

        if (target.length() > 0) {
            target.append("*");
        }
        target.append(symbol);
        if (abs != 1) {
            target.append("^").append(abs);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof UnitDimension that)) {
            return false;
        }

        return length == that.length &&
                mass == that.mass &&
                time == that.time &&
                current == that.current &&
                temperature == that.temperature &&
                amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, mass, time, current, temperature, amount);
    }

    @Override
    public String toString() {
        return toBaseString();
    }
}
