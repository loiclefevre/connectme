#ifdef GL_ES // This definition block is used by every JavaFX shader. This may be copied to allow for further compatibility.
#extension GL_OES_standard_derivatives: enable
#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
precision highp int;
#else
precision mediump float;
precision mediump int;
#endif
#else
#define highp
#define mediump
#define lowp
#endif

// Note: The vertex shader is handled by JavaFX and is not customizable for 2D effects.
varying vec2 texCoord0; // The texture coordinate for the first texture sampler.

uniform vec4 jsl_pixCoordOffset; // This offset will be set by JavaFX and may be discarded if not needed.
uniform sampler2D baseImg; // The first texture sampler. The name must match the declared name in the ShaderDeclaration.
uniform float strength; // The declared strength parameter. The name must match the declared name in the ShaderDeclaration.
uniform vec4 texCoords; // The same applies to the texCoords parameter.

void main() {
    // gl_FragCoord alone does not match the pixel coordinates of the image.
    // JavaFX uses this calculation to get the correct pixel coordinates.
    // (For this shader, this is not needed, but it is included for completeness.)
    // vec2 pixcoord = vec2(gl_FragCoord.x - jsl_pixCoordOffset.x, ((jsl_pixCoordOffset.z - gl_FragCoord.y) * jsl_pixCoordOffset.w) - jsl_pixCoordOffset.y);
    // For this example, the shader will shift some color channels by a variable amount.
//    vec4 img = texture2D(baseImg, texCoord0);
    // Here we need to compensate the offset for the varying texture dimensions by scaling the offset by the provided texture dimensions.
//    float imgR = texture2D(baseImg, texCoord0 + (vec2(0.02, 0.0) * strength) * texCoords.zw).r;
//    float imgB = texture2D(baseImg, texCoord0 + (vec2(-0.02, 0.0) * strength) * texCoords.zw).b;

//    gl_FragColor = vec4(imgR, img.g, imgB, img.a);


    const vec4 c00 = vec4(1.0,0.0,0.0,1.0);
    const vec4 c01 = vec4(0.0,0.0,1.0,1.0);
    const vec4 c10 = vec4(1.0,1.0,0.0,1.0);
    const vec4 c11 = vec4(0.0,1.0,1.0,1.0);

    vec4 c1 = mix(c00, c10, texCoord0.x);
    vec4 c2 = mix(c01, c11, texCoord0.x);

    gl_FragColor = mix(c1, c2, texCoord0.y) * strength;

    gl_FragColor = pow( clamp(gl_FragColor,0.0,1.0), vec4(0.45,0.45,0.45,0.45) );
}
