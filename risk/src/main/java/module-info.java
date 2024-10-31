module com.magomerob {
    requires javafx.controls;
    requires javafx.fxml;
	requires org.json;
    requires javafx.graphics;

    opens cliente to javafx.fxml;
    exports cliente;
}
