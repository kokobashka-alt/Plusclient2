#version 330

layout(std140) uniform HandParams {
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

vec3 firePalette(float t, vec3 tint) {
    t = clamp(t, 0.0, 1.0);
    vec3 base = max(tint, vec3(0.04));
    float lum = max(max(base.r, base.g), base.b);
    base = base / max(lum, 0.15);
    vec3 dim = base * 0.45;
    vec3 mid = base * 0.95;
    vec3 hot = mix(base, vec3(1.0), 0.35) * 1.15;
    if (t < 0.45) return mix(dim, mid, t / 0.45);
    return mix(mid, hot, (t - 0.45) / 0.55);
}

vec3 normalizeTint(vec3 tint) {
    vec3 base = max(tint, vec3(0.04));
    float lum = max(max(base.r, base.g), base.b);
    return base / max(lum, 0.12);
}

// Interference + domain-warp caustics (cheap, monochrome tint)
void waveEnergy(vec2 uv, float t, out float body, out float crest, out float flow) {
    vec2 p = (uv - 0.5) * vec2(1.75, 1.15);

    vec2 warp = vec2(
        sin(p.y * 6.8 + t * 1.45),
        cos(p.x * 5.6 - t * 1.15)
    );
    vec2 q = p + warp * 0.10;

    vec2 s1 = vec2(sin(t * 0.63) * 0.42, cos(t * 0.47) * 0.30);
    vec2 s2 = vec2(cos(t * 0.55 + 1.7) * -0.38, sin(t * 0.71) * 0.34);
    vec2 s3 = vec2(sin(t * 0.39 + 2.4) * 0.22, cos(t * 0.88 + 0.6) * -0.40);

    float r1 = length(q - s1);
    float r2 = length(q - s2);
    float r3 = length(q - s3);

    float phase =
        sin(r1 * 34.0 - t * 4.6) * 0.50 +
        sin(r2 * 29.0 + t * 3.9) * 0.42 +
        sin(r3 * 26.0 - t * 3.2) * 0.30 +
        sin(dot(q, vec2(13.0, 19.0)) - t * 2.7) * 0.32 +
        sin(dot(q, vec2(-16.0, 10.0)) + t * 2.2) * 0.28 +
        sin((q.x + q.y * 1.35) * 21.0 + t * 2.0 + warp.x) * 0.22;

    body = 0.5 + 0.5 * sin(phase);
    float c1 = sin(phase * 2.15 + r1 * 9.0);
    float c2 = sin(phase * 1.35 + r2 * 6.0 + 0.9);
    crest = pow(max(c1, 0.0), 5.5) + pow(max(c2, 0.0), 8.0) * 0.55;
    crest = clamp(crest, 0.0, 1.0);
    flow = 0.5 + 0.5 * sin(dot(q, vec2(9.0, -12.0)) + t * 3.1 + warp.y);
}

vec3 waveTint(float body, float crest, float flow, vec3 tint) {
    vec3 base = normalizeTint(tint);
    vec3 deep = base * 0.22;
    vec3 mid = base * 0.95;
    vec3 peak = mix(base, vec3(1.0), 0.58) * 1.35;
    vec3 col = mix(deep, mid, body);
    col = mix(col, peak, crest);
    col += peak * crest * flow * 0.55;
    col += base * body * 0.12;
    return col;
}

void main() {
    float strength = max(Data.x, 0.01);
    float mode = Data.y;
    float time = Data.z;

    float edge = smoothstep(1e-5, 0.02, texture(Sampler0, texCoord).r);
    float sa = clamp(Color.a * edge, 0.0, 1.0);
    vec4 solid = vec4(Color.rgb * sa, sa);

    vec4 blurC = texture(Sampler1, texCoord);
    float ba = clamp(blurC.a, 0.0, 1.0);

    if (ba < 0.002 && sa < 0.002) discard;

    vec2 px = 1.0 / vec2(textureSize(Sampler1, 0));
    bool neon = mode > 0.5 && mode < 1.5;
    bool fire = mode > 1.5 && mode < 2.5;
    bool wave = mode > 2.5;

    if (neon) {
        vec2 off = px * 1.8 * (1.2 + 0.4 * sin(time * 2.4));
        float br = texture(Sampler1, texCoord + off).a;
        float bb = texture(Sampler1, texCoord - off).a;
        ba = max(ba, max(br, bb));
    }

    if (fire) {
        float n = vnoise(texCoord * 22.0 + time * 2.0);
        float n2 = vnoise(texCoord * 11.0 - time * 1.4);
        vec2 j = px * vec2((n - 0.5) * 1.2, (n2 - 0.5) * 0.8 - 0.6);
        vec4 b2 = texture(Sampler1, texCoord + j);
        blurC = max(blurC, b2 * 0.85);
        ba = max(ba, clamp(b2.a, 0.0, 1.0) * 0.85);
    }

    if (wave) {
        float wx = sin(texCoord.y * 42.0 + time * 3.2) * cos(texCoord.x * 30.0 - time * 2.1);
        float wy = sin(texCoord.x * 36.0 + time * 2.6);
        vec2 j = px * vec2(wx * 3.2, wy * 2.6);
        vec4 b2 = texture(Sampler1, texCoord + j);
        vec4 b3 = texture(Sampler1, texCoord - j * 0.7);
        blurC = max(blurC, max(b2 * 0.92, b3 * 0.75));
        ba = max(ba, max(clamp(b2.a, 0.0, 1.0) * 0.92, clamp(b3.a, 0.0, 1.0) * 0.75));
    }

    if (ba < 0.004 && sa < 0.004) discard;

    vec3 sc = sa > 0.001 ? solid.rgb / max(sa, 1e-4) : Color.rgb;
    vec3 bc = ba > 0.001 ? blurC.rgb / max(ba, 1e-4) : sc;
    bc = max(bc, sc);

    float outer = max(ba - sa, 0.0);
    float soft = pow(max(outer, 0.0), fire ? 0.38 : wave ? 0.36 : 0.40);
    float near = outer * exp(-outer * (fire ? 7.2 : wave ? 6.8 : 8.5));

    if (neon) {
        float nwave = 0.55 + 0.45 * cos(time * 1.7 + texCoord.y * 9.0);
        bc = mix(bc, bc * nwave + Color.rgb * 0.35, 0.55);
    }

    if (fire) {
        float dens = clamp(ba * 1.05 + outer * 0.7, 0.0, 1.0);
        float flicker = 0.88 + 0.12 * vnoise(texCoord * 16.0 + time * 6.0);
        float heat = dens * flicker;
        bc = firePalette(heat, Color.rgb);
        soft *= flicker;
        near *= 0.7 + 0.3 * flicker;
    }

    float body = 0.0;
    float crest = 0.0;
    float flow = 0.0;
    if (wave) {
        waveEnergy(texCoord, time, body, crest, flow);
        bc = waveTint(body, crest, flow, Color.rgb);
        soft *= 0.70 + 0.55 * body + 0.35 * crest;
        near *= 0.80 + 0.55 * crest + 0.20 * flow;
    }

    float glow = soft * strength * (fire ? 2.1 : wave ? 2.0 : 2.25);
    float white = near * strength * (fire ? 0.9 : wave ? 1.55 : 2.4);
    float rim = near * strength * (fire ? 1.15 : wave ? 1.75 : 1.65);

    float fill = 0.0;
    if (wave) {
        float mask = smoothstep(0.06, 0.68, sa);
        float ridge = 0.28 + body * 0.35 + crest * 0.85;
        fill = mask * strength * ridge;
    }

    float a = clamp(glow + white * 0.55 + rim * 0.3 + fill, 0.0, 1.0);
    if (a < 0.02) discard;
    if (!wave && sa > 0.92 && outer < 0.012) discard;

    vec3 col = bc * (glow * 1.15 + rim);
    if (!fire && !wave) col += vec3(1.0) * white;
    else if (fire) col += bc * white * 0.45;
    else {
        vec3 peak = mix(normalizeTint(Color.rgb), vec3(1.0), 0.55);
        col += bc * (white * 0.50 + fill * 1.15);
        col += peak * fill * crest * 1.4;
        col += peak * near * strength * crest * 0.85;
    }
    if (fire) {
        col = max(col, bc * a * 0.5);
    }
    if (wave) {
        col = max(col, bc * fill * 1.15);
        col += bc * fill * flow * 0.35;
    }
    fragColor = vec4(col, a);
}
