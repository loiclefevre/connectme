sampler2D baseImg : register(s0); // The first texture sampler. The index specified in register(s#) must match the declared index in the ShaderDeclaration.
float strength : register(c0); // The declared strength parameter. The index specified in register(c#) must match the declared index in the ShaderDeclaration.
float4 texCoords: register(c1); // The same applies to the texCoords parameter.

void main(in float2 pos0 : TEXCOORD0, in float2 pos1 : TEXCOORD1, in float2 pixcoord : VPOS, in float4 jsl_vertexColor : COLOR0, out float4 color : COLOR0) {
/*    float4 img = tex2D(baseImg, pos0);
    float imgR = tex2D(baseImg, pos0 + (float2(0.02, 0.0) * strength) * texCoords.zw).r;
    float imgB = tex2D(baseImg, pos0 + (float2(-0.02, 0.0) * strength) * texCoords.zw).b;

    color = float4(imgR, img.g, imgB, img.a);
*/
    const float4 c00 = float4(1.0,0.0,0.0,1.0);
    const float4 c01 = float4(0.0,0.0,1.0,1.0);
    const float4 c10 = float4(1.0,1.0,0.0,1.0);
    const float4 c11 = float4(0.0,1.0,1.0,1.0);

    const float4 c1 = lerp(c00, c10, pos0.x);
    const float4 c2 = lerp(c01, c11, pos0.x);

    color = lerp(c1, c2, pos0.y) * strength;

    color = pow( clamp(color,0.0,1.0), float4(0.45,0.45,0.45,0.45) );
}