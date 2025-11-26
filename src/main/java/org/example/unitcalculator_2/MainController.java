package org.example.unitcalculator_2;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainController {

    // Toggle theme
    @FXML private AnchorPane root;
    @FXML private Button themeToggleButton;
    private String darkThemeUrl;

    // Main fields
    @FXML private TextField inputField;
    @FXML private Label resultLabel;
    @FXML private Label resultNameLabel;
    @FXML private Label baseLabel;
    @FXML private Label statusLabel;


    // Unit dropdown + description label
    @FXML private ComboBox<String> unitCombo;
    @FXML private Label unitFormulaLabel;

    // Drop down unit description
    private static final Map<String, String> UNIT_DESCRIPTIONS = new LinkedHashMap<>();
    static {
        UNIT_DESCRIPTIONS.put("N",  "Force (Newton) = kg•m/s²");
        UNIT_DESCRIPTIONS.put("J",  "Energy (Joule) = kg•m²/s²");
        UNIT_DESCRIPTIONS.put("W",  "Power (Watt) = J/s = kg•m²/s³");
        UNIT_DESCRIPTIONS.put("Pa", "Pressure (Pascal) = N/m² = kg/(m•s²)");
        UNIT_DESCRIPTIONS.put("Hz", "Frequency (Hertz) = 1/s");
        UNIT_DESCRIPTIONS.put("C",  "Charge (Coulomb) = A•s");
        UNIT_DESCRIPTIONS.put("V",  "Voltage (Volt) = J/C = W/A = kg•m²/(A•s³)");
        UNIT_DESCRIPTIONS.put("E",  "Electric Field (E) = N/C = V/m = kg•m/(A•s³)");
        UNIT_DESCRIPTIONS.put("A",  "Electric Current (Ampere) = C/s");
        UNIT_DESCRIPTIONS.put("Ω",  "Resistance (Ohm) = V/A = kg•m²/(A²•s³)");
        UNIT_DESCRIPTIONS.put("F",  "Capacitance (Farad) = C/V = s⁴•A²/(kg•m²)");
        UNIT_DESCRIPTIONS.put("T",  "Magnetic field (Tesla) = N/(A•m) = kg/(A•s²)");
        UNIT_DESCRIPTIONS.put("H",  "Inductance (Henry) = Φ/A = kg•m²/(A²•s²)");
        UNIT_DESCRIPTIONS.put("Φ",  "Magnetic flux (Weber) = V•s = kg•m²/(A•s²)");

    }

    // Custom units
    @FXML private FlowPane customUnitPane;
    @FXML private TextField customSymbolField;
    @FXML private TextField customExprField;
    @FXML private Label customUnitStatusLabel;

    private final List<CustomUnitStorage.CustomUnit> customUnits = new ArrayList<>();

    // prefix disabled
    private static final Set<Character> PREFIX_CHARS = Set.of();
    private static final int MAX_CUSTOM_SYMBOL_LEN = 5;

    //******************************************************************
    // initialize()
    //******************************************************************
    @FXML
    private void initialize() {

        // theme css
        var url = getClass().getResource("dark-theme.css");

        if (url != null) {
            darkThemeUrl = url.toExternalForm();
        }
        if (themeToggleButton != null) {
            themeToggleButton.setText("Light mode");
        }

        // Enter to simplify
        inputField.setOnAction(e -> onSimplify());

        // Unit preset ComboBox
        if (unitCombo != null) {
            unitCombo.getItems().setAll(UNIT_DESCRIPTIONS.keySet());
            unitCombo.setOnAction(e -> onSelectUnit());
        }

        // Load custom units
        for (CustomUnitStorage.CustomUnit cu : CustomUnitStorage.loadAll()) {
            try {
                UnitDimension dim = UnitExpressionParser.parse(cu.expression());
                UnitRegistry.registerCustom(cu.symbol(), dim);
                addCustomUnitButton(cu.symbol());
                customUnits.add(cu);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        root.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> onClearInput();
            }
        });

    }


    // Unit preset selection handler
    @FXML
    private void onSelectUnit() {
        if (unitCombo == null || unitFormulaLabel == null) return;

        String sym = unitCombo.getValue();
        if (sym == null || sym.isEmpty()) return;

        String desc = UNIT_DESCRIPTIONS.get(sym);
        if (desc == null) {
            unitFormulaLabel.setText("");
        } else {
            unitFormulaLabel.setText(desc);
        }
    }


    // Theme toggle
    @FXML
    private void onToggleTheme() {
        if (root == null || darkThemeUrl == null) return;

        var scene = root.getScene();
        if (scene == null) return;

        var styles = scene.getStylesheets();

        if (styles.contains(darkThemeUrl)) {
            styles.remove(darkThemeUrl);
            themeToggleButton.setText("Dark mode");
        } else {
            styles.add(darkThemeUrl);
            themeToggleButton.setText("Light mode");
        }
    }

    //******************************************************************
    // Simplify
    //******************************************************************
    @FXML
    private void onSimplify() {
        String text = inputField.getText();
        if (text == null || text.trim().isEmpty()) {
            statusLabel.setText("Please enter an expression.");
            resultLabel.setText("");
            resultNameLabel.setText("");
            baseLabel.setText("");
            return;
        }

        try {
            Quantity q = QuantityExpressionParser.parse(text);

            double value = q.getValue();
            UnitDimension dim = q.getDimension();

            String compact = text.replaceAll("\\s+", "");
            boolean isSingleSymbol = compact.matches("[A-Za-zμΩΦ]+");

            if (dim.isDimensionless()) {
                String valueStr = formatValue(value);
                resultLabel.setText(valueStr);
                resultNameLabel.setText("");
                baseLabel.setText("1");
                statusLabel.setText("");
                return;
            }

            // Find derived units
            String derived = UnitRegistry.getDerivedSymbol(dim);

            String displayUnit;
            if (isSingleSymbol) {
                displayUnit = compact;
            } else if (derived != null) {
                displayUnit = derived;
            } else {
                displayUnit = dim.toBaseString();   // base form
            }

            // base SI form
            baseLabel.setText(dim.toBaseString());

            String name = null;
            if (derived != null) {
                name = UnitRegistry.getUnitName(derived);
            } else if (isSingleSymbol) {
                String baseSymbol = stripPrefix(compact);
                name = UnitRegistry.getUnitName(baseSymbol);
            }

            String valueStr = formatValue(value);
            String resultText;

            if (Math.abs(value - 1.0) < 1e-9) {
                resultText = displayUnit;
            } else {
                resultText = valueStr + " " + displayUnit;
            }

            resultLabel.setText(resultText);
            resultNameLabel.setText(name != null ? "(" + name + ")" : "");
            statusLabel.setText("");

        } catch (IllegalArgumentException ex) {
            resultLabel.setText("");
            resultNameLabel.setText("");
            baseLabel.setText("");
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    //******************************************************************
    // Value formatting in 3 decimal places and scientific notation
    //******************************************************************
    private String formatValue(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            return Double.toString(v);
        }

        double abs = Math.abs(v);

        // range for 3 decimal places
        if (abs == 0.0 || (abs >= 1e-3 && abs < 1e6)) {
            String s = String.format(java.util.Locale.US, "%.3f", v);

            s = s.replaceAll("\\.?0+$", "");
            return s;
        }

        // scientific range
        return String.format(java.util.Locale.US, "%.3e", v);
    }

    private String stripPrefix(String symbol) {
        if (symbol.length() > 1 && PREFIX_CHARS.contains(symbol.charAt(0))) {
            return symbol.substring(1);
        }
        return symbol;
    }


    // Basic editing controls
    @FXML
    private void onClearInput() {
        inputField.clear();
        resultLabel.setText("");
        resultNameLabel.setText("");
        baseLabel.setText("");
        statusLabel.setText("");
        inputField.requestFocus();
    }

    @FXML
    private void onBackspace() {
        String text = inputField.getText();
        if (text == null || text.isEmpty()) return;

        int caret = inputField.getCaretPosition();
        int anchor = inputField.getAnchor();

        if (caret != anchor) {
            int start = Math.min(anchor, caret);
            int end   = Math.max(anchor, caret);

            inputField.deleteText(start, end);
            inputField.positionCaret(start);
        } else if (caret > 0) {
            inputField.deleteText(caret - 1, caret);
            inputField.positionCaret(caret - 1);
        }

        inputField.requestFocus();
    }

    @FXML
    private void onInsertSymbol(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        String token = btn.getText();
        if (token == null) return;

        if (!inputField.isFocused()) {
            String text = inputField.getText();
            int end = (text == null) ? 0 : text.length();

            inputField.requestFocus();
            inputField.positionCaret(end);
        }

        inputField.replaceSelection(token);
    }

    //******************************************************************
    // Custom units
    //******************************************************************
    @FXML
    private void onAddCustomUnit() {
        String symbol = customSymbolField.getText();
        String expr   = customExprField.getText();

        if (symbol == null) symbol = "";
        if (expr == null) expr = "";

        symbol = symbol.trim();
        expr   = expr.trim();

        if (symbol.isEmpty() || expr.isEmpty()) {
            if (customUnitStatusLabel != null)
                customUnitStatusLabel.setText("Name and unit are required.");
            return;
        }

        if (symbol.length() > MAX_CUSTOM_SYMBOL_LEN) {
            if (customUnitStatusLabel != null)
                customUnitStatusLabel.setText("Name too long (max " + MAX_CUSTOM_SYMBOL_LEN + ").");
            return;
        }

        if (!symbol.matches("[A-Za-zμΩΦ]+")) {
            if (customUnitStatusLabel != null)
                customUnitStatusLabel.setText("Invalid name (letters only).");
            return;
        }

        if (UnitRegistry.hasSymbol(symbol)) {
            customUnitStatusLabel.setText("Unit '" + symbol + "' already exists.");
            return;
        }

        UnitDimension dim;
        try {
            dim = UnitExpressionParser.parse(expr);
        } catch (IllegalArgumentException ex) {
            customUnitStatusLabel.setText("Invalid unit expression: " + ex.getMessage());
            return;
        }

        UnitRegistry.registerCustom(symbol, dim);

        CustomUnitStorage.CustomUnit cu = new CustomUnitStorage.CustomUnit(symbol, expr);
        customUnits.add(cu);
        CustomUnitStorage.add(cu);

        addCustomUnitButton(symbol);

        customSymbolField.clear();
        customExprField.clear();
        customUnitStatusLabel.setText("Added " + symbol + " = " + expr);
    }

    @FXML
    private void onDeleteLastCustomUnit() {
        if (customUnits.isEmpty()) {
            customUnitStatusLabel.setText("No custom units to delete.");
            return;
        }

        CustomUnitStorage.CustomUnit last = customUnits.remove(customUnits.size() - 1);

        String symbol = last.symbol();

        if (!customUnitPane.getChildren().isEmpty()) {
            customUnitPane.getChildren().remove(
                    customUnitPane.getChildren().size() - 1
            );
        }

        UnitRegistry.unregisterCustom(symbol);
        CustomUnitStorage.overwriteAll(customUnits);

        customUnitStatusLabel.setText("Deleted " + symbol);
    }

    private void addCustomUnitButton(String symbol) {
        Button b = new Button(symbol);
        b.setFocusTraversable(false);
        b.setOnAction(this::onInsertSymbol);
        customUnitPane.getChildren().add(b);
    }
}
