module com.example.testchangescene {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.testchangescene to javafx.fxml;
    opens domain to javafx.base;
    exports com.example.testchangescene;
}