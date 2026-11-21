#version 330

uniform sampler2D InSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 d = 0.5 / vec2(textureSize(InSampler, 0));
    vec4 sum = texture(InSampler, texCoord) * 4.0;
    sum += texture(InSampler, texCoord + vec2(-d.x,  0.0)) * 2.0;
    sum += texture(InSampler, texCoord + vec2( d.x,  0.0)) * 2.0;
    sum += texture(InSampler, texCoord + vec2( 0.0, -d.y)) * 2.0;
    sum += texture(InSampler, texCoord + vec2( 0.0,  d.y)) * 2.0;
    sum += texture(InSampler, texCoord + vec2(-d.x, -d.y));
    sum += texture(InSampler, texCoord + vec2( d.x, -d.y));
    sum += texture(InSampler, texCoord + vec2(-d.x,  d.y));
    sum += texture(InSampler, texCoord + vec2( d.x,  d.y));
    fragColor = sum / 16.0;
}
