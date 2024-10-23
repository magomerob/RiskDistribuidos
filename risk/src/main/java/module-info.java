module com.magomerob {
    requires javafx.controls;
    requires javafx.fxml;
	requires org.json;

    opens cliente to javafx.fxml;
    exports cliente;
}
