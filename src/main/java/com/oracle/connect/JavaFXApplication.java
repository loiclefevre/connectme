package com.oracle.connect;

import com.oracle.connect.gui.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

public class JavaFXApplication extends Application {

	static Controller guiController;

	@Override
	public void start(Stage stage) throws Exception {
		final FXMLLoader fxmlLoader = new FXMLLoader(JavaFXApplication.class.getResource("main-view.fxml"));
		Parent parent = fxmlLoader.load();
		final Scene scene = new Scene(parent, -1f, -1f, false, SceneAntialiasing.BALANCED);
		stage.setResizable(false);
		stage.setScene(scene);

		guiController = fxmlLoader.getController();
		guiController.initialize();
		stage.show();
	}

}
