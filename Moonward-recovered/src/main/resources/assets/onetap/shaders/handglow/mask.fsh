#version 330

layout(std140) uniform HandParams {
    vec4 Color;
    vec4 Data;
};

uniform sampler2D Sampler0;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    float d = texture(Sampler0, texCoord).r;
    if (d < 1e-5) discard;
    float edge = smoothstep(1e-5, 0.02, d);
    fragColor = vec4(Color.rgb * Color.a * edge, Color.a * edge);
}