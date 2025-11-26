package org.example.unitcalculator_2;

/*********************************************************
 * Parser for a formula with numbers and units
 ********************************************************/
public final class QuantityExpressionParser {

    private final String input;
    private int pos;

    private QuantityExpressionParser(String input) {
        this.input = input;
        this.pos = 0;
    }

    public static Quantity parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input is null");
        }
        QuantityExpressionParser p = new QuantityExpressionParser(input);
        Quantity result = p.parseExpression();
        p.skipSpaces();

        if (!p.isEnd()) {
            throw new IllegalArgumentException("Unexpected trailing characters at position " + p.pos);
        }
        return result;
    }

    private Quantity parseExpression() {
        Quantity result = parseTerm();
        while (true) {
            skipSpaces();
            if (match('+')) {
                Quantity rhs = parseTerm();
                result = result.add(rhs);
            } else if (match('-')) {
                Quantity rhs = parseTerm();
                result = result.subtract(rhs);
            } else {
                break;
            }
        }
        return result;
    }

    private Quantity parseTerm() {
        Quantity result = parseFactor();

        while (true) {
            skipSpaces();

            if (match('*')) {
                Quantity rhs = parseFactor();
                result = result.multiply(rhs);
            } else if (match('/')) {
                Quantity rhs = parseFactor();
                result = result.divide(rhs);
            } else if (isImplicitMulAhead()) {
                Quantity rhs = parseFactor();
                result = result.multiply(rhs);
            } else {
                break;
            }
        }
        return result;
    }

    // Factor = unary
    private Quantity parseFactor() {
        skipSpaces();
        boolean negate = false;
        while (true) {
            if (match('+')) {
                    //ignore
            } else if (match('-')) {
                negate = !negate;
            } else {
                break;
            }
        }

        Quantity base = parsePrimary();

        skipSpaces();
        if (match('^')) {
            int exp = parseSignedInt();
            base = base.pow(exp);
        }

        if (negate) {
            base = base.negate();
        }
        return base;
    }

    private Quantity parsePrimary() {
        skipSpaces();
        if (isEnd()) {
            throw new IllegalArgumentException("Unexpected end of input");
        }

        char c = peek();
        if (c == '(') {
            consume();
            Quantity inside = parseExpression();
            skipSpaces();
            if (!match(')')) {
                throw new IllegalArgumentException("Missing closing parenthesis");
            }
            return inside;
        }

        if (Character.isDigit(c) || c == '.') {
            double value = parseNumber();
            // Only numbers -> no dimension
            Quantity q = new Quantity(value, UnitDimension.DIMENSIONLESS);

            // Numbers & units together
            while (true) {
                skipSpaces();
                if (isSymbolStart(peek())) {
                    String sym = parseSymbol();
                    UnitDimension dim = UnitRegistry.resolveSymbol(sym);
                    Quantity unitQ = new Quantity(1.0, dim);
                    q = q.multiply(unitQ);
                } else {
                    break;
                }
            }
            return q;
        }

        if (isSymbolStart(c)) {
            String sym = parseSymbol();
            UnitDimension dim = UnitRegistry.resolveSymbol(sym);

            return new Quantity(1.0, dim);
        }

        throw new IllegalArgumentException("Unexpected character: '" + c + "' at position " + pos);
    }


    // Parse numbers exponents symbols
    private double parseNumber() {
        skipSpaces();
        int start = pos;
        boolean hasDot = false;

        while (!isEnd()) {
            char c = peek();
            if (Character.isDigit(c)) {
                consume();
            } else if (c == '.' && !hasDot) {
                hasDot = true;
                consume();
            } else {
                break;
            }
        }

        if (!isEnd()) {
            char c = peek();
            if (c == 'e' || c == 'E') {
                consume();
                if (!isEnd()) {
                    char sign = peek();
                    if (sign == '+' || sign == '-') {
                        consume();
                    }
                }
                while (!isEnd() && Character.isDigit(peek())) {
                    consume();
                }
            }
        }

        String s = input.substring(start, pos);

        if (s.isEmpty() || s.equals(".") || s.equals("+") || s.equals("-")) {
            throw new IllegalArgumentException("Invalid number at position " + start);
        }
        return Double.parseDouble(s);
    }

    private int parseSignedInt() {
        skipSpaces();

        int start = pos;
        boolean negative = false;

        if (match('-')) {
            negative = true;
        } else if (match('+')) {
            // positive index
        }

        if (isEnd() || !Character.isDigit(peek())) {
            throw new IllegalArgumentException("Invalid exponent at position " + start);
        }

        int value = 0;
        while (!isEnd() && Character.isDigit(peek())) {
            int d = peek() - '0';

            consume();
            value = value * 10 + d;
        }

        return negative ? -value : value;
    }

    private String parseSymbol() {
        skipSpaces();

        int start = pos;

        while (!isEnd() && isSymbolPart(peek())) {
            consume();
        }
        if (start == pos) {
            throw new IllegalArgumentException("Expected unit symbol at position " + start);
        }
        return input.substring(start, pos);
    }


    private boolean isImplicitMulAhead() {
        skipSpaces();
        if (isEnd()) {
            return false;
        }
        char c = peek();

        return c == '(' || Character.isDigit(c) || c == '.' || isSymbolStart(c);
    }

    private boolean isSymbolStart(char c) {
        if (c == 0) {
            return false;
        }
        return Character.isLetter(c) || c == 'μ' || c == 'Ω' || c == 'Φ';
    }

    private boolean isSymbolPart(char c) {
        return isSymbolStart(c);
    }

    private void skipSpaces() {
        while (!isEnd() && Character.isWhitespace(peek())) {
            consume();
        }
    }

    private boolean match(char expected) {
        if (!isEnd() && peek() == expected) {
            consume();
            return true;
        }
        return false;
    }

    private char peek() {
        if (isEnd()) {
            return 0;
        }
        return input.charAt(pos);
    }

    private void consume() {
        pos++;
    }

    private boolean isEnd() {
        return pos >= input.length();
    }
}
