#version 330

layout(std140) uniform AtmParams {
    vec4 Color;
    vec4 Data;
};

uniform sampler2D Sampler0;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    float end = max(Data.x, 4.0);
    float density = max(Data.y, 0.01);

    float depth = texture(Sampler0, texCoord).r;
    float dist = min(0.05 / max(depth, 1e-6), end * 4.0);

    float near = end * 0.06;
    float d = max(dist - near, 0.0);
    float optical = density * d / max(end * 0.32, 1.0);
    float t = 1.0 - exp(-optical);
    if (t < 0.004) discard;

    fragColor = vec4(Color.rgb, clamp(t, 0.0, 1.0));
}