package org.example.unitcalculator_2;

// Parse a formula with only unit and convert it to UnitDimension
public final class UnitExpressionParser {

    private UnitExpressionParser() { }

    public static UnitDimension parse(String input) {
        Quantity q = QuantityExpressionParser.parse(input);
        return q.getDimension();
    }
}
