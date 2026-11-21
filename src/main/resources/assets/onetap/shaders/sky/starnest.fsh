#version 330

#moj_import <onetap:sky_params.glsl>
#moj_import <onetap:sky_lib.glsl>

in vec3 vDir;
out vec4 fragColor;

const int NEAR_LAYERS = 3;

vec3 starNest(vec3 dir, int first, int last) {
    float t = Params.x;
    float sc = clamp(Params.z, 0.25, 4.0);
    vec3 rd = normalize(dir) * (0.8 / sc);
    vec3 b = themeB();
    vec3 c = themeC();

    float s = 0.1 + float(first) * 0.18;
    float fade = pow(0.70, float(first));
    vec3 v = vec3(0.0);
    vec3 from = vec3(1.0, 0.5, 0.5) + vec3(t * 0.0021, t * 0.00105, t * 0.0007);

    for (int r = first; r < last; r++) {
        vec3 p = from + s * rd * 0.5;
        p = abs(vec3(0.85) - mod(p, vec3(1.7)));

        float pa = 0.0;
        float acc = 0.0;
        for (int i = 0; i < 8; i++) {
            p = abs(p) / max(dot(p, p), 1e-4) - 0.53;
            float len = length(p);
            acc += abs(len - pa);
            pa = len;
        }

        float dm = max(0.0, 0.35 - acc * acc * 0.001);
        acc = acc * acc * acc;
        if (r > 2) fade *= 1.0 - dm;

        vec3 col = mix(vec3(s, s * s, s * s * s * s), mix(b, c, clamp(s, 0.0, 1.0)), 0.55);
        col = mix(col, vec3(1.0, 0.97, 0.92), clamp(acc * 0.02, 0.0, 1.0));
        v += col * acc * 0.0024 * fade;
        fade *= 0.70;
        s += 0.18;
    }
    return v;
}

#ifdef SKY_FIELD
void main() {
    fragColor = vec4(starNest(normalize(vDir), 0, NEAR_LAYERS), 0.0);
}
#else
in vec2 texCoord;

uniform sampler2D Sampler0;

vec3 renderStarNest(vec3 dir) {
    float t = Params.x;
    float sc = clamp(Params.z, 0.25, 4.0);
    vec3 a = themeA();
    vec3 c = themeC();
    vec3 v = textureBicubic(Sampler0, texCoord).rgb + starNest(dir, NEAR_LAYERS, 6);
    v = mix(vec3(length(v)), v, 0.85) * 0.01;
    vec3 col = v * 2.5 + a * 0.03;
    col += starLayer(dir, 130.0 / sc, 2200.0, t, c) * 0.65;
    return tonemap(col * Params.w);
}

void main() {
    fragColor = vec4(renderStarNest(normalize(vDir)), 1.0);
}
#endif
