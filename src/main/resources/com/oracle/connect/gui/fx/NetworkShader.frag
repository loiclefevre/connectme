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


/*
#define antialiasing(n) n/min(iResolution.y,iResolution.x)
#define S(d,b) smoothstep(antialiasing(1.0),b,d)
#define B(p,s) max(abs(p).x-s.x,abs(p).y-s.y)

float Hash21(vec2 p) {
    p = fract(p*vec2(234.56,789.34));
    p+=dot(p,p+34.56);
    return fract(p.x+p.y);
}

vec3 randomPlot(vec2 p, vec3 col, float t){
    p.y *= 3.0;
    p.x *= 20.0;
    p.x+=t;
    vec2 gv = fract(p)-0.5;
    vec2 id = floor(p);
    gv.y = id.y;

    float n = Hash21(id);
    float w = clamp(0.25*(n*2.0),0.1,1.0);
    float d = B(gv,vec2(w,0.02));
    float cn = clamp(n,0.5,1.0);
    col = mix(col,vec3(cn,cn,cn),S(d,0.0));
    return col;
}

const float speeds[14] = float[](-2., 3., 6., 3.5, 5., 7., 4.5, 7.5, 8.0, 9., 2.5, 6.5,10.0,5.3);
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = (fragCoord-0.5*iResolution.xy)/iResolution.y;

    vec3 col = vec3(0.0);

    float index = 0.0;
    for(int i = 0; i<2; i++){
        vec2 pos = uv+vec2(index,-0.1+(index));
        col = randomPlot(pos, col, iTime*speeds[i]);
        index+=0.5;
    }

    fragColor = vec4(col,1.0);
}
*/