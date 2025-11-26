package org.example.unitcalculator_2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class UnitRegistry {

    private static final Map<String, UnitDimension> SYMBOL_TO_DIM = new HashMap<>();
    private static final Map<UnitDimension, String> DIM_TO_DERIVED_SYMBOL = new HashMap<>();
    private static final Map<String, String> UNIT_NAMES = new HashMap<>();

    // Prefix hidden for now
    private static final Set<String> PREFIXES = Set.of("k", "m", "μ", "u", "n");

    //**************************************************************************
    // Static initializer
    //**************************************************************************
    static {

        // Base units
        register("kg", new UnitDimension(0, 1, 0, 0, 0, 0));
        register("m",  new UnitDimension(1, 0, 0, 0, 0, 0));
        register("s",  new UnitDimension(0, 0, 1, 0, 0, 0));
        register("A",  new UnitDimension(0, 0, 0, 1, 0, 0));
        register("K",  new UnitDimension(0, 0, 0, 0, 1, 0));
        register("mol",new UnitDimension(0, 0, 0, 0, 0, 1));

        UNIT_NAMES.put("kg", "Mass");
        UNIT_NAMES.put("m",  "Length");
        UNIT_NAMES.put("s",  "Time");
        UNIT_NAMES.put("A",  "Current");
        UNIT_NAMES.put("K",  "Temperature");
        UNIT_NAMES.put("mol","Amount of substance");


        /**********************************************
        * Derived Units
        **********************************************/
        registerDerived("N",
                new UnitDimension(1, 1, -2, 0, 0, 0),
                "Force");

        registerDerived("J",
                new UnitDimension(2, 1, -2, 0, 0, 0),
                "Energy");

        registerDerived("W",
                new UnitDimension(2, 1, -3, 0, 0, 0),
                "Power");

        registerDerived("Pa",
                new UnitDimension(-1, 1, -2, 0, 0, 0),
                "Pressure");

        registerDerived("Hz",
                new UnitDimension(0, 0, -1, 0, 0, 0),
                "Frequency");

        registerDerived("C",
                new UnitDimension(0, 0, 1, 1, 0, 0),
                "Charge");

        registerDerived("V",
                new UnitDimension(2, 1, -3, -1, 0, 0),
                "Voltage");

        registerDerived("E",
                new UnitDimension(1, 1, -3, -1, 0, 0),
                "Electric field");

        UnitDimension ohmDim = new UnitDimension(2, 1, -3, -2, 0, 0);
        registerDerived("Ω",   ohmDim, "Resistance");
        registerDerived("ohm", ohmDim, "Resistance");

        registerDerived("F",
                new UnitDimension(-2, -1, 4, 2, 0, 0),
                "Capacitance");

        registerDerived("T",
                new UnitDimension(0, 1, -2, -1, 0, 0),
                "Magnetic field");

        registerDerived("H",
                new UnitDimension(2, 1, -2, -2, 0, 0),
                "Inductance");

        registerDerived("Φ",
                new UnitDimension(2, 1, -2, -1, 0, 0),
                "Magnetic flux");
    }

    /******************************************************************************************
    Registration helpers
    ******************************************************************************************/
    private static void register(String symbol, UnitDimension dim) {
        SYMBOL_TO_DIM.put(symbol, dim);
    }

    private static void registerDerived(String symbol, UnitDimension dim, String niceName) {
        SYMBOL_TO_DIM.put(symbol, dim);
        DIM_TO_DERIVED_SYMBOL.put(dim, symbol);

        if (niceName != null) {
            UNIT_NAMES.put(symbol, niceName);
        }
    }

    /**********************************************************************************
    Lookup API
    **********************************************************************************/
    public static UnitDimension getDimension(String symbol) {
        UnitDimension dim = SYMBOL_TO_DIM.get(symbol);
        if (dim == null) {
            throw new IllegalArgumentException("Unknown unit symbol: " + symbol);
        }
        return dim;
    }

    /***********************************************************************************************
    Interpret symbols in QuantityExpressionParser
    ***********************************************************************************************/
    public static UnitDimension resolveSymbol(String symbol) {
        UnitDimension dim = SYMBOL_TO_DIM.get(symbol);
        if (dim != null) return dim;

        if (symbol.length() > 1) {
            String prefix = symbol.substring(0, 1);
            String baseSymbol = symbol.substring(1);

            if (PREFIXES.contains(prefix)) {
                UnitDimension base = SYMBOL_TO_DIM.get(baseSymbol);
                if (base != null) return base;
            }
        }

        UnitDimension concat = tryConcatenated(symbol);
        if (concat != null) return concat;

        throw new IllegalArgumentException("Unknown unit symbol: " + symbol);
    }


    // Smooth multiplicatoin
    private static UnitDimension tryConcatenated(String symbol) {
        int len = symbol.length();
        UnitDimension result = null;
        int pos = 0;

        while (pos < len) {
            String bestMatch = null;
            UnitDimension bestDim = null;
            int bestLen = 0;

            for (String key : SYMBOL_TO_DIM.keySet()) {
                if (symbol.startsWith(key, pos) && key.length() > bestLen) {
                    bestMatch = key;
                    bestDim = SYMBOL_TO_DIM.get(key);
                    bestLen = key.length();
                }
            }

            if (bestMatch == null) return null;

            result = (result == null) ? bestDim : result.multiply(bestDim);
            pos += bestLen;
        }

        return result;
    }

    public static String getDerivedSymbol(UnitDimension dim) {
        return DIM_TO_DERIVED_SYMBOL.get(dim);
    }

    public static String getUnitName(String symbol) {
        return UNIT_NAMES.get(symbol);
    }

    // Custom unit API
    public static boolean hasSymbol(String symbol) {
        return SYMBOL_TO_DIM.containsKey(symbol);
    }

    public static void registerCustom(String symbol, UnitDimension dim) {
        SYMBOL_TO_DIM.put(symbol, dim);
    }

    public static void unregisterCustom(String symbol) {
        SYMBOL_TO_DIM.remove(symbol);
        UNIT_NAMES.remove(symbol);
    }
}
