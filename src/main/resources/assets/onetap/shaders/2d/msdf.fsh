#version 330

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;
flat in float pxRange;

out vec4 fragColor;

// The atlases are mtsdf: the alpha channel holds a true signed distance field. At the small UI sizes
// (12 to 16 px from a 64 px em) the median of the RGB channels leaves stray bars and ragged edges,
// so the alpha distance is used instead.
void main() {
    vec2 unitRange = vec2(pxRange) / vec2(textureSize(Sampler0, 0));
    float screenPxRange = max(0.5 * dot(unitRange, vec2(1.0) / fwidth(texCoord0)), 1.0);
    float sd = texture(Sampler0, texCoord0).a;
    float alpha = clamp(screenPxRange * (sd - 0.5) + 0.5, 0.0, 1.0);
    fragColor = vec4(vertexColor.rgb, vertexColor.a * alpha);
}
