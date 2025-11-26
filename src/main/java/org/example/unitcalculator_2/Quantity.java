package org.example.unitcalculator_2;


/*****************************************************************
 Class for Unit dimension and value
 Used in QuantityExpressionParser
 *******************************************************************/
public final class Quantity {

    private final double value;
    private final UnitDimension dimension;

    public Quantity(double value, UnitDimension dimension) {
        this.value = value;
        this.dimension = dimension;
    }

    public double getValue() {
        return value;
    }

    public UnitDimension getDimension() {
        return dimension;
    }

    // Same dimension addition
    public Quantity add(Quantity other) {
        if (!this.dimension.equals(other.dimension)) {
            throw new IllegalArgumentException("Cannot add quantities in different dimensions");
        }
        return new Quantity(this.value + other.value, this.dimension);
    }

    // Same dimension subtraction
    public Quantity subtract(Quantity other) {
        if (!this.dimension.equals(other.dimension)) {
            throw new IllegalArgumentException(
                    "Cannot subtract quantities in different dimensions");
        }
        return new Quantity(this.value - other.value, this.dimension);
    }

    // Multiplication
    public Quantity multiply(Quantity other) {
        return new Quantity(
                this.value * other.value,
                this.dimension.multiply(other.dimension)
        );
    }

    // Division
    public Quantity divide(Quantity other) {
        return new Quantity(
                this.value / other.value,
                this.dimension.divide(other.dimension)
        );
    }

    // Pow
    public Quantity pow(int exponent) {
        if (exponent == 0) {
            return new Quantity(1.0, UnitDimension.DIMENSIONLESS);  // pow 0 -> 1
        }
        double newValue = Math.pow(this.value, exponent);
        UnitDimension newDim = this.dimension.pow(exponent);
        return new Quantity(newValue, newDim);
    }


    public Quantity negate() {
        return new Quantity(-this.value, this.dimension);
    }

    public static Quantity add(Quantity a, Quantity b) {
        return a.add(b);
    }

    public static Quantity subtract(Quantity a, Quantity b) {
        return a.subtract(b);
    }

    @Override
    public String toString() {
        return value + " " + dimension;
    }
}
