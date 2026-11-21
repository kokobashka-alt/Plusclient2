#version 330

layout(std140) uniform HandParams {
    vec4 Color;
    vec4 Data;
};

uniform sampler2D Sampler0;

in vec2 texCoord;
out vec4 fragColor;

float maskAlpha(vec2 uv) {
    return smoothstep(1e-5, 0.02, texture(Sampler0, uv).r) * Color.a;
}

vec4 maskSample(vec2 uv) {
    float alpha = maskAlpha(uv);
    return vec4(Color.rgb * alpha, alpha);
}

void main() {
    float s = max(Data.x, 0.75);
    vec2 hp = s * 0.5 / vec2(textureSize(Sampler0, 0));
    vec4 c;
    if (Data.y > 0.5) {
        c = maskSample(texCoord) * 4.0;
        c += maskSample(texCoord - hp);
        c += maskSample(texCoord + hp);
        c += maskSample(texCoord + vec2( hp.x, -hp.y));
        c += maskSample(texCoord + vec2(-hp.x,  hp.y));
    } else {
        c = texture(Sampler0, texCoord) * 4.0;
        c += texture(Sampler0, texCoord - hp);
        c += texture(Sampler0, texCoord + hp);
        c += texture(Sampler0, texCoord + vec2( hp.x, -hp.y));
        c += texture(Sampler0, texCoord + vec2(-hp.x,  hp.y));
    }
    fragColor = c * 0.125;
}
