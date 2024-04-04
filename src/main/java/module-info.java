module com.oracle.connect {
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.swing;
	requires javafx.graphics;
	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires org.kordamp.bootstrapfx.core;
	requires java.desktop;

	opens com.oracle.connect to javafx.fxml, java.desktop;
	exports com.oracle.connect;

	opens com.oracle.connect.gui to javafx.fxml;
	exports com.oracle.connect.gui;
}
