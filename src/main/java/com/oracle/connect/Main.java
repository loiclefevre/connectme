package com.oracle.connect;

import com.oracle.connect.gui.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends javafx.application.Application {
	static Controller guiController;

	public static void main(String[] args) {
		System.setProperty("prism.lcdtext", "false");
		Locale.setDefault(Locale.US);

		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		final FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
		Parent parent = fxmlLoader.load();
		final Scene scene = new Scene(parent, -1f, -1f, false, SceneAntialiasing.BALANCED);
		stage.setResizable(false);
		stage.setScene(scene);

		guiController = fxmlLoader.getController();
		guiController.initialize();
		stage.show();
	}
}
