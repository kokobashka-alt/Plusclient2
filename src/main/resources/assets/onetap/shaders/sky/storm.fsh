#version 330

#moj_import <onetap:sky_params.glsl>
#moj_import <onetap:sky_lib.glsl>
#moj_import <onetap:sky_simplex.glsl>

in vec3 vDir;
out vec4 fragColor;

#ifdef SKY_FIELD
void main() {
    vec3 dir = normalize(vDir);
    float t = Params.x * 3.0;
    vec3 np = dir * (2.2 / max(Params.z, 0.001)) + vec3(t * 0.045, 0.0, t * 0.018);
    fragColor = vec4(fbm3(np), fbm3(np * 3.1 + vec3(t * 0.06, t * 0.01, 0.0)), 0.0, 0.0);
}
#else
in vec2 texCoord;

uniform sampler2D Sampler0;

float boltSegment(vec2 p, vec2 a, vec2 b, float width) {
    vec2 pa = p - a;
    vec2 ba = b - a;
    float h = clamp(dot(pa, ba) / max(dot(ba, ba), 1e-5), 0.0, 1.0);
    vec2 d = pa - ba * h;
    return exp(-dot(d, d) / (width * width));
}

float bolt(vec2 uv, vec2 origin, float seed, float cell, float local) {
    vec2 p = uv - origin;
    if (p.y < 0.0 || p.y > 1.6) return 0.0;

    float xOff = 0.0;
    float drift = 0.0;
    float bright = 0.0;
    float core = 0.0;
    float tipX = 0.0;
    float segY = 0.0;
    for (int k = 0; k < 16; k++) {
        float y0 = float(k) * 0.1;
        if (y0 > p.y + 0.1) break;
        float jag = (hash12(vec2(float(k) * 1.37, seed * 2.9 + cell)) - 0.5) * 0.30;
        drift += (hash12(vec2(float(k) * 4.7, seed * 5.1 + cell)) - 0.5) * 0.035;
        float x1 = xOff + jag + drift;
        vec2 a = vec2(xOff + drift * y0, y0);
        vec2 b = vec2(x1 + drift * (y0 + 0.1), y0 + 0.1);
        vec2 pa = p - a;
        vec2 ba = b - a;
        float h = clamp(dot(pa, ba) / max(dot(ba, ba), 1e-5), 0.0, 1.0);
        vec2 d = pa - ba * h;
        float d2 = dot(d, d);
        bright = max(bright, exp(-d2 / 0.000324));
        core = max(core, exp(-d2 / 0.000036));
        if (y0 < p.y) {
            tipX = x1;
            segY = y0 + 0.1;
        }
        xOff = x1;
    }

    float b1 = boltSegment(vec2(p.x - tipX * 0.55, p.y - segY * 0.55), vec2(0.0), vec2(0.45, 0.55), 0.008) * 0.55;
    float b2 = boltSegment(vec2(p.x - tipX * 0.35, p.y - segY * 0.35), vec2(0.0), vec2(-0.35, 0.4), 0.006) * 0.4;
    float retrace = 0.6 + 0.4 * sin(local * 60.0 + seed * 25.0);
    return (bright + core * 1.7 + b1 + b2) * retrace;
}

float strikeEnvelope(float local) {
    float a = smoothstep(0.0, 0.015, local);
    float b = 1.0 - smoothstep(0.10, 0.38, local);
    float second = smoothstep(0.16, 0.19, local) * (1.0 - smoothstep(0.20, 0.42, local)) * 0.7;
    return max(a * b, second);
}

vec3 renderStorm(vec3 dir) {
    float t = Params.x * 3.0;
    vec3 accent = themeC();
    float sc = max(Params.z, 0.001);
    float horizFade = smoothstep(-0.12, 0.12, dir.y);

    vec3 deep = vec3(0.028, 0.035, 0.045);
    vec3 lead = vec3(0.11, 0.13, 0.15);
    vec3 sick = vec3(0.16, 0.19, 0.14);
    vec3 skyCol = mix(lead * 0.55, deep, smoothstep(-0.1, 0.85, dir.y));
    skyCol = mix(skyCol, sick * 0.8, exp(-pow(dir.y * 3.2, 2.0)) * 0.35);

    vec2 field = textureBicubic(Sampler0, texCoord).xy * 0.5 + 0.5;
    float n1 = field.x;
    float n2 = field.y;
    float torn = n1 - n2 * 0.55;
    float cloud = smoothstep(0.30, 0.58, torn);
    float dark = smoothstep(0.42, 0.78, torn);

    vec3 cloudCol = mix(lead, deep * 0.55, dark);
    cloudCol = mix(cloudCol, sick, pow(n2, 3.0) * 0.22 * (1.0 - dark));

    vec3 rain = vec3(0.10, 0.12, 0.14) * (0.6 + n2 * 0.5);
    float rainVeil = smoothstep(0.25, -0.35, dir.y);
    vec3 color = mix(skyCol, cloudCol, cloud * horizFade);
    color = mix(color, rain, rainVeil * 0.85);

    vec2 uv = vec2(atan(dir.z, dir.x) * 1.6, -dir.y * 1.15);
    float flashes = 0.0;
    float dome = 0.0;
    for (int i = 0; i < 3; i++) {
        float s = float(i) * 17.31 + 5.7;
        float cell = floor(t / 5.0 + s * 3.1);
        if (hash12(vec2(cell * 0.83, s * 5.17)) < 0.72) continue;
        float local = fract(t / 5.0 + s * 3.1);
        float env = strikeEnvelope(local);
        if (env < 0.01) continue;
        vec2 o = vec2(hash12(vec2(cell, s)) * 6.4 - 3.2, -(0.12 + hash12(vec2(s, cell)) * 0.4));
        flashes += bolt(uv, o, s, cell, local) * env;
        dome += env * exp(-length(uv - o) * 1.4);
    }

    color += vec3(0.75, 0.82, 1.0) * flashes * 5.5;
    color += mix(accent, vec3(0.65, 0.72, 1.0), 0.5) * dome * cloud * 1.4;
    color += vec3(0.7, 0.78, 1.0) * dome * rainVeil * 0.5;
    color *= Params.w * 1.15;
    color = color / (1.0 + color * 0.55);
    return pow(color, vec3(0.88));
}

void main() {
    fragColor = vec4(renderStorm(normalize(vDir)), 1.0);
}
#endif
