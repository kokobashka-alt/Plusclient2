#version 330

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float hash13(vec3 p) {
    p = fract(p * vec3(443.8975, 397.2973, 491.1871));
    p += dot(p, p.yxz + 19.19);
    return fract((p.x + p.y) * p.z);
}

vec3 starLayer(vec3 dir, float density, float size, float t, vec3 tint) {
    vec3 p = dir * density;
    vec3 cell = floor(p);
    float rnd = hash13(cell);
    if (rnd < 0.985) return vec3(0.0);

    vec3 local = fract(p) - 0.5;
    float disc = exp(-dot(local, local) * size);
    float twinkle = 0.72 + 0.28 * sin(t * 1.4 + rnd * 140.0);
    float spike = 0.0;
    if (rnd > 0.996) {
        spike = (exp(-abs(local.x) * 90.0) + exp(-abs(local.y) * 90.0) + exp(-abs(local.z) * 90.0)) * 0.35;
    }
    return mix(vec3(0.95, 0.97, 1.0), tint, 0.28) * (disc + spike * disc) * twinkle;
}

vec3 tonemap(vec3 col) {
    col = (col * (2.51 * col + 0.03)) / (col * (2.43 * col + 0.59) + 0.14);
    return pow(clamp(col, 0.0, 1.0), vec3(0.92));
}

vec3 themeA() { return max(ColorA.rgb, vec3(0.02)); }
vec3 themeB() { return max(ColorB.rgb, vec3(0.02)); }
vec3 themeC() { return max(ColorC.rgb, vec3(0.04)); }

vec4 textureBicubic(sampler2D tex, vec2 uv) {
    vec2 size = vec2(textureSize(tex, 0));
    vec2 pos = uv * size;
    vec2 p1 = floor(pos - 0.5) + 0.5;
    vec2 f = pos - p1;
    vec2 w0 = f * (-0.5 + f * (1.0 - 0.5 * f));
    vec2 w1 = 1.0 + f * f * (-2.5 + 1.5 * f);
    vec2 w2 = f * (0.5 + f * (2.0 - 1.5 * f));
    vec2 w3 = f * f * (-0.5 + 0.5 * f);
    vec2 w12 = w1 + w2;
    vec2 t0 = (p1 - 1.0) / size;
    vec2 t3 = (p1 + 2.0) / size;
    vec2 t12 = (p1 + w2 / w12) / size;
    vec4 r = (texture(tex, vec2(t0.x, t0.y)) * w0.x + texture(tex, vec2(t12.x, t0.y)) * w12.x + texture(tex, vec2(t3.x, t0.y)) * w3.x) * w0.y;
    r += (texture(tex, vec2(t0.x, t12.y)) * w0.x + texture(tex, vec2(t12.x, t12.y)) * w12.x + texture(tex, vec2(t3.x, t12.y)) * w3.x) * w12.y;
    r += (texture(tex, vec2(t0.x, t3.y)) * w0.x + texture(tex, vec2(t12.x, t3.y)) * w12.x + texture(tex, vec2(t3.x, t3.y)) * w3.x) * w3.y;
    return r;
}
