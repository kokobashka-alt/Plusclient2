#version 330

layout(std140) uniform GlowParams {
    vec4 Color;
    vec4 Data;
};

uniform sampler2D Sampler0;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    float s = max(Data.x, 0.75);
    vec2 hp = s * 0.5 / vec2(textureSize(Sampler0, 0));
    vec4 c = texture(Sampler0, texCoord) * 4.0;
    c += texture(Sampler0, texCoord - hp);
    c += texture(Sampler0, texCoord + hp);
    c += texture(Sampler0, texCoord + vec2( hp.x, -hp.y));
    c += texture(Sampler0, texCoord + vec2(-hp.x,  hp.y));
    fragColor = c * 0.125;
}
