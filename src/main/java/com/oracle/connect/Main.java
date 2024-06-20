package com.oracle.connect;

import de.teragam.jfxshader.JFXShaderModule;
import javafx.application.Application;

import java.util.Locale;

/**
 * Application main entry point.
 *
 * @author Loïc Lefèvre
 */
public class Main {
	public static void main(final String[] args) {
		JFXShaderModule.setup();

		// Better looking
		System.setProperty("prism.lcdtext", "false");
		System.setProperty("oracle.jdbc.diagnostic.enableDiagnoseFirstFailure", "false");

		// Standardize dates and numbers formatting
		Locale.setDefault(Locale.US);


		/*
		try {
			final Process lsnrctl = Runtime.getRuntime().exec(new String[] {"lsnrctl","stat"});

			//lsnrctl.getOutputStream()
			//System.out.;
		}
		catch (IOException e) {
		} */

		// Launch the JavaFX app
		Application.launch(JavaFXApplication.class, args);
	}
}
