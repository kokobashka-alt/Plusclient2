#version 330

#moj_import <onetap:weather_common.glsl>

uniform sampler2D Sampler0;

in vec2 vQ;
in vec2 vUv;
flat in int vLayer;

out vec4 fragColor;

vec2 dropLayer(vec2 q, float sc, uint salt, float E, float t, out float spec, out float edge) {
    spec = 0.0;
    edge = 0.0;
    vec2 g = q * sc;
    ivec2 id = ivec2(floor(g));
    vec2 f = fract(g) - 0.5;
    uint h = hash2(id, salt);
    float ha = u01(h);
    float hb = u01(pcg(h + 1u));
    float hc = u01(pcg(h + 2u));
    float hd = u01(pcg(h + 3u));
    float he = u01(pcg(h + 4u));

    float gate = smoothstep(hb * 0.6, hb * 0.6 + 0.2, E);
    if (gate <= 0.0) return vec2(0.0);

    float n = 14.0 + floor(hd * 40.0);
    float life = fract(n * t / 1024.0 + he);
    float grow = smoothstep(0.0, 0.12, life);
    float die = 1.0 - smoothstep(0.78, 1.0, life);
    float sz = grow * die * gate;
    if (sz <= 0.001) return vec2(0.0);

    float r = (0.045 + 0.07 * hc) * sz;
    vec2 ctr = (vec2(ha, he) - 0.5) * 0.36;
    bool slider = hd > 0.62;
    if (slider) {
        float lf = smoothstep(0.1, 0.95, life);
        ctr.y = 0.34 - 0.68 * (lf + 0.03 * sin(life * 31.0));
        ctr.x += 0.03 * sin(life * 17.0 + ha * 6.0);
        r = (0.04 + 0.04 * hc) * sz;
    }

    vec2 res = vec2(0.0);
    float msk = 0.0;
    vec2 d = f - ctr;
    float l = length(d) / max(r, 1e-4);
    if (l < 1.0) {
        msk = 1.0 - smoothstep(0.82, 1.0, l);
        vec2 nxy = d / max(r, 1e-4);
        res = -nxy * r * msk;
        spec = msk * (1.0 - smoothstep(0.0, 0.45, length(nxy - vec2(-0.38, 0.42))));
        edge = msk * smoothstep(0.55, 1.0, l);
    }
    if (slider) {
        float ty = f.y - ctr.y;
        float trailLen = 0.34 - ctr.y;
        if (ty > 0.0 && ty < trailLen) {
            float seg = floor(ty / 0.075);
            float lf2 = (fract(ty / 0.075) - 0.5) * 0.075;
            uint hs = hash2(id * 31 + ivec2(int(seg), 7), salt + 5u);
            float pr = u01(hs);
            if (pr < 0.65) {
                float br = (0.012 + 0.014 * u01(pcg(hs + 1u))) * (1.0 - ty / max(trailLen, 1e-3)) * sz;
                float bx = (u01(pcg(hs + 2u)) - 0.5) * 0.04;
                vec2 bd = vec2(f.x - ctr.x - bx, lf2);
                float bl = length(bd) / max(br, 1e-4);
                if (bl < 1.0) {
                    float bm = 1.0 - smoothstep(0.8, 1.0, bl);
                    res += -(bd / max(br, 1e-4)) * br * bm;
                    spec = max(spec, bm * (1.0 - smoothstep(0.0, 0.5, length(bd / max(br, 1e-4) - vec2(-0.35, 0.4)))));
                    edge = max(edge, bm * smoothstep(0.55, 1.0, bl));
                    msk = max(msk, bm);
                }
            }
            float wx = abs(f.x - ctr.x - 0.004 * sin(ty * 90.0));
            float wl = (1.0 - smoothstep(0.004, 0.013, wx)) * (1.0 - ty / max(trailLen, 1e-3));
            edge = max(edge, wl * 0.18);
        }
    }
    return res / sc;
}

void main() {
    float sc = vLayer == 0 ? 5.0 : (vLayer == 1 ? 9.0 : 15.0);
    float spec;
    float edge;
    vec2 off = dropLayer(vQ, sc, 101u + uint(vLayer) * 13u, P2.x, CamPos.w, spec, edge);
    vec2 offUv = vec2(off.x / P2.w, off.y) * 1.6;
    float inside = clamp(length(offUv) * 90.0 + spec + edge, 0.0, 1.0);
    if (inside <= 0.001) discard;
    vec2 uv = vUv;
    vec3 refr = texture(Sampler0, clamp(uv + offUv, vec2(0.001), vec2(0.999))).rgb;
    float m = clamp(length(offUv) * 120.0, 0.0, 1.0);
    float dim = 1.0 - 0.22 * edge;
    fragColor = vec4(refr * m * dim + vec3(0.9, 0.95, 1.0) * spec * 0.55, 1.0 - (1.0 - m) * dim);
}
