#version 330

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;
flat in float pxRange;

out vec4 fragColor;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

void main() {
    vec2 unitRange = vec2(pxRange) / vec2(textureSize(Sampler0, 0));
    float screenPxRange = max(0.5 * dot(unitRange, vec2(1.0) / fwidth(texCoord0)), 1.0);
    vec3 msd = texture(Sampler0, texCoord0).rgb;
    float alpha = clamp(screenPxRange * (median(msd.r, msd.g, msd.b) - 0.5) + 0.5, 0.0, 1.0);
    fragColor = vec4(vertexColor.rgb, vertexColor.a * alpha);
}
