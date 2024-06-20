package com.oracle.connect.gui.fx;

import de.teragam.jfxshader.effect.EffectDependencies;
import de.teragam.jfxshader.effect.OneSamplerEffect;
import javafx.beans.property.DoubleProperty;

@EffectDependencies(BilinearGradientEffectPeer.class)
public class BilinearGradientEffect extends OneSamplerEffect {

	private final DoubleProperty strength;

	public BilinearGradientEffect(double strength, double width, double height) {
		this.strength = this.createEffectDoubleProperty(strength, "strength");
	}

	public double getStrength() {
		return this.strength.get();
	}

	public DoubleProperty strengthProperty() {
		return this.strength;
	}

	public void setStrength(double strength) {
		this.strength.set(strength);
	}
}
