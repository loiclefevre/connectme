package com.oracle.connect;

import javafx.application.Application;

import java.util.Locale;

/**
 * Application main entry point.
 *
 * @author Loïc Lefèvre
 */
public class Main {
	public static void main(final String[] args) {
		// Better looking
		System.setProperty("prism.lcdtext", "false");

		Locale.setDefault(Locale.US);

		Application.launch(JavaFXApplication.class, args);
	}
}
