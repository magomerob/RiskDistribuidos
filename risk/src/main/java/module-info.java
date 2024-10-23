module com.magomerob {
    requires javafx.controls;
    requires javafx.fxml;

    opens cliente to javafx.fxml;
    exports cliente;
}
