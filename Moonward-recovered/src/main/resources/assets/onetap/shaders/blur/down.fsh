#version 330

uniform sampler2D InSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 h = 1.0 / vec2(textureSize(InSampler, 0));
    vec4 sum = texture(InSampler, texCoord) * 4.0;
    sum += texture(InSampler, texCoord + vec2(-h.x, -h.y));
    sum += texture(InSampler, texCoord + vec2( h.x, -h.y));
    sum += texture(InSampler, texCoord + vec2(-h.x,  h.y));
    sum += texture(InSampler, texCoord + vec2( h.x,  h.y));
    fragColor = sum / 8.0;
}
