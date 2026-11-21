#version 330

layout(std140) uniform GlowParams {
    vec4 Color;
    vec4 Data;
};

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

void main() {
    float decay = clamp(Data.x, 0.0, 0.99);
    float rise = Data.y;
    float time = Data.z;

    vec2 px = 1.0 / vec2(textureSize(Sampler0, 0));
    float n = vnoise(texCoord * 14.0 + time * 1.6);
    float n2 = vnoise(texCoord * 9.0 - time * 1.1);
    vec2 advect = vec2(
        (n - 0.5) * px.x * 1.4,
        -rise * px.y * (1.8 + n2 * 1.2)
    );

    vec4 prev = texture(Sampler0, texCoord + advect) * 0.55;
    prev += texture(Sampler0, texCoord + advect + vec2(0.0, -px.y)) * 0.25;
    prev += texture(Sampler0, texCoord + advect + vec2(px.x, -px.y * 0.5)) * 0.10;
    prev += texture(Sampler0, texCoord + advect - vec2(px.x, px.y * 0.5)) * 0.10;

    vec4 cur = texture(Sampler1, texCoord);
    vec4 trail = prev * decay + cur;
    trail = min(trail, vec4(1.0));
    if (trail.a < 0.008 && cur.a < 0.008) {
        fragColor = vec4(0.0);
        return;
    }
    fragColor = trail;
}
