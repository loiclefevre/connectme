package com.oracle.connect;

import javafx.application.Application;

import java.util.Locale;

public class Main {

	public static void main(String[] args) {
		System.setProperty("prism.lcdtext", "false");
		Locale.setDefault(Locale.US);

		Application.launch(JavaFXApplication.class);
	}
}
