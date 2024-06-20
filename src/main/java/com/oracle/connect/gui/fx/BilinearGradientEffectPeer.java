package com.oracle.connect.gui.fx;

import de.teragam.jfxshader.JFXShader;
import de.teragam.jfxshader.ShaderDeclaration;
import de.teragam.jfxshader.effect.EffectPeer;
import de.teragam.jfxshader.effect.ShaderEffectPeer;
import de.teragam.jfxshader.effect.ShaderEffectPeerConfig;

import java.util.HashMap;
import java.util.Map;

@EffectPeer("BilinearGradientEffect")
public class BilinearGradientEffectPeer extends ShaderEffectPeer<BilinearGradientEffect> {
	public BilinearGradientEffectPeer(ShaderEffectPeerConfig config) {
		super(config);
	}

	@Override
	protected ShaderDeclaration createShaderDeclaration() {
		final Map<String, Integer> samplers = new HashMap<>();
		samplers.put("baseImg", 0);
		final Map<String, Integer> params = new HashMap<>();
		params.put("strength", 0);
		params.put("texCoords", 1);
		return new ShaderDeclaration(samplers, params,
				BilinearGradientEffectPeer.class.getResourceAsStream("NetworkShader.frag"), // OpenGL fragment shader (Linux, Mac)
				BilinearGradientEffectPeer.class.getResourceAsStream("NetworkShader.obj")); // Compiled DirectX 9 pixel shader (Windows)
	}

	@Override
	protected void updateShader(JFXShader shader, BilinearGradientEffect effect) {
		shader.setConstant("strength", (float) effect.getStrength());
		// JavaFX uses an image pool to select textures that are at least as large as the requested dimensions.
		// Therefore, the texture coordinates for the actual content may not be in the range [0, 1], which may cause issues for shaders that rely on this range.
		// Here we pass the content texture coordinates of the input texture "baseImg" to the shader.
		final float[] texCoords = this.getTextureCoords(0);
		shader.setConstant("texCoords", texCoords[0], texCoords[1], texCoords[2], texCoords[3]);
	}
}
