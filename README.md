# UnitCalculator_2
Unit calculator for physics.
The program supports arithmetic with quantities and units.

---------------------------------------------------------------

## Features
- Parse mathematical expressions with units such as:
  - (500kg*20m)/(6s^2) = 1666.667N, N*m = J, 5C/s = 5A.
- Automatically reduce to SI base form 
  - Example: 'N' -> kg*m/s²
- Derived unit recognition
  - N, J, W, Pa, Hz, C, V, Ω(Ohm), F, T, H, Weber...
- Custom unit creation (saved locally)
- Unit description (dropdown menu)
- Formatting numerical output
- Enter to simplify & ESC to clear input
- Custom units are saved to: ~/.physics-unit-calculator/custom-units.txt
- Dark mode & Light mode

---------------------------------------------------------------

## Tech Stack
- Java21
- JavaFX 21.0.6
- Maven
- FXML + CSS(dark theme)

---------------------------------------------------------------

## Project structure

src/main/java/org/example/unitcalculator_2/ 
- CustomUnitStorage.java
- MainController.java
- Quantity.java
- QuantityExpressionParser.java
- UnitCalculatorApp.java
- UnitDimension.java
- UnitExpressionParser.java
- UnitRegistry.java

src/main/resources/org/example/unitcalculator_2
- main-view.fxml
- dark-theme.css

---------------------------------------------------------------

## Running the Application
IntelliJ (recommended)
- Open the project in IntelliJ and run: UnitCalculatorApp.java
- Maven + JavaFX dependencies load automatically

---------------------------------------------------------------

## Roadmap
- More derived units
- Better UI layout
- History panel
- More mathematical functions
