module org.example.unitcalculator_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.unitcalculator_2 to javafx.fxml;
    exports org.example.unitcalculator_2;
}