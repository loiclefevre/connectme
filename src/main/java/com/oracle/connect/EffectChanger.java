package com.oracle.connect;

import com.oracle.connect.gui.fx.BilinearGradientEffect;
import javafx.application.Platform;

import java.util.TimerTask;

public class EffectChanger extends TimerTask {
	private final long start = System.currentTimeMillis();
	private final BilinearGradientEffect effect;

	public EffectChanger(BilinearGradientEffect effect) {
		this.effect=effect;
	}

	@Override
	public void run() {
		Platform.runLater( () -> effect.setStrength( (double)(System.currentTimeMillis() - start)/1000d ) );
	}
}
