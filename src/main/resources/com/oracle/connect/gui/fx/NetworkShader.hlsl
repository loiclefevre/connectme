// C:\Program Files (x86)\Windows Kits\10\bin\10.0.22621.0\x64>fxc /nologo /T ps_3_0 /Fo C:\dev\projects\connectme\src\main\resources\com\oracle\connect\gui\fx\NetworkShader.obj C:\dev\projects\connectme\src\main\resources\com\oracle\connect\gui\fx\NetworkShader.hlsl

sampler2D baseImg : register(s0); // The first texture sampler. The index specified in register(s#) must match the declared index in the ShaderDeclaration.
float strength : register(c0); // The declared strength parameter. The index specified in register(c#) must match the declared index in the ShaderDeclaration.
float4 texCoords: register(c1); // The same applies to the texCoords parameter.


#define antialiasing(n) n/min(28.0,750.0 /*iResolution.y,iResolution.x*/)
#define S(d,b) smoothstep(antialiasing(1.0),b,d)
#define B(p,s) max(abs(p).x-s.x,abs(p).y-s.y)

float Hash21(float2 p) {
    p = frac(p*float2(234.56,789.34));
    p+=dot(p,p+34.56);
    return frac(p.x+p.y);
}

float3 randomPlot(float2 p, float3 col, float t){
    p.y *= 3.0;
    p.x *= 20.0;

    p.x+=t;
    float2 gv = frac(p)-0.5;
    float2 id = floor(p);
    gv.y = id.y;

    float n = Hash21(id);
    float w = clamp(0.25*(n*2.0),0.1,1.0);
    float d = B(gv,float2(w,0.02));
    float cn = clamp(n,0.5,1.0);

    //col = float3(cn,cn,cn);
    col = lerp(col,float3(cn,cn,cn),S(d,0.0));

    return col;
}

static float speeds[2] = {-4., 6. /*, 6., 3.5, 5., 7., 4.5, 7.5, 8.0, 9., 2.5, 6.5,10.0,5.3 */};

void main(in float2 pos0 : TEXCOORD0, in float2 pos1 : TEXCOORD1, in float2 pixcoord : VPOS, in float4 jsl_vertexColor : COLOR0, out float4 color : COLOR0) {
/*    float4 img = tex2D(baseImg, pos0);
    float imgR = tex2D(baseImg, pos0 + (float2(0.02, 0.0) * strength) * texCoords.zw).r;
    float imgB = tex2D(baseImg, pos0 + (float2(-0.02, 0.0) * strength) * texCoords.zw).b;

    color = float4(imgR, img.g, imgB, img.a);
*/
/*
    const float4 c00 = float4(1.0,0.0,0.0,1.0);
    const float4 c01 = float4(0.0,0.0,1.0,1.0);
    const float4 c10 = float4(1.0,1.0,0.0,1.0);
    const float4 c11 = float4(0.0,1.0,1.0,1.0);

    const float4 c1 = lerp(c00, c10, pos0.x);
    const float4 c2 = lerp(c01, c11, pos0.x);

    color = lerp(c1, c2, pos0.y) * strength;

    color = pow( clamp(color,0.0,1.0), float4(0.45,0.45,0.45,0.45) );
    */


    float2 uv = pos0;

        float3 col = float3(0.0,0.0,0.0);

        float index = 0.0;
        for(int i = 0; i<2; i++){
            float2 pos = uv+float2(index,-0.05+index);
            col = randomPlot(pos, col, strength*speeds[i]);
            index-=0.45;
        }

    float Black = col.r + col.g + col.b;

	if(Black == 0)
		discard;

        color = float4(col.xyz*float3(0.18039,0.960784,0.18039),0.9);
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