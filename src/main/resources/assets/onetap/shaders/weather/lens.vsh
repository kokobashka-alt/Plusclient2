#version 330

#moj_import <onetap:weather_common.glsl>

out vec2 vQ;
out vec2 vUv;
flat out int vLayer;

const vec2 CORNERS[4] = vec2[](vec2(0.0, 0.0), vec2(1.0, 0.0), vec2(1.0, 1.0), vec2(0.0, 1.0));

void main() {
    int q = gl_VertexID >> 2;
    vec2 corner = CORNERS[gl_VertexID & 3];
    vQ = vec2(0.0);
    vUv = vec2(0.0);
    vLayer = 0;
    gl_Position = vec4(0.0, 0.0, 2.0, 1.0);

    int layer = 0;
    int start = 0;
    for (int i = 0; i < 2; i++) {
        int count = int(LensGrid[layer]) * int(LensGrid2[layer]);
        if (q < start + count) break;
        start += count;
        layer++;
    }
    int nx = int(LensGrid[layer]);
    if (q - start >= nx * int(LensGrid2[layer])) return;
    int local = q - start;
    float sc = layer == 0 ? 5.0 : (layer == 1 ? 9.0 : 15.0);
    uint salt = 101u + uint(layer) * 13u;
    vec2 off = vec2(float(layer) * 3.7, float(layer) * 1.3);
    ivec2 id = ivec2(floor(off * sc)) + ivec2(local % nx, local / nx);

    uint h = hash2(id, salt);
    float ha = u01(h);
    float hb = u01(pcg(h + 1u));
    float hc = u01(pcg(h + 2u));
    float hd = u01(pcg(h + 3u));
    float he = u01(pcg(h + 4u));
    float E = P2.x;
    float gate = smoothstep(hb * 0.6, hb * 0.6 + 0.2, E);
    if (gate <= 0.0) return;
    float life = fract((14.0 + floor(hd * 40.0)) * CamPos.w / 1024.0 + he);
    float sz = smoothstep(0.0, 0.12, life) * (1.0 - smoothstep(0.78, 1.0, life)) * gate;
    if (sz <= 0.001) return;

    float r = (0.045 + 0.07 * hc) * sz;
    vec2 ctr = (vec2(ha, he) - 0.5) * 0.36;
    vec2 lo;
    vec2 hi;
    if (hd > 0.62) {
        float lf = smoothstep(0.1, 0.95, life);
        ctr.y = 0.34 - 0.68 * (lf + 0.03 * sin(life * 31.0));
        ctr.x += 0.03 * sin(life * 17.0 + ha * 6.0);
        r = (0.04 + 0.04 * hc) * sz;
        float wx = max(r, 0.05);
        lo = vec2(ctr.x - wx, ctr.y - r);
        hi = vec2(ctr.x + wx, 0.36);
    } else {
        lo = ctr - r;
        hi = ctr + r;
    }
    lo = max(lo - 0.01, vec2(-0.5));
    hi = min(hi + 0.01, vec2(0.5));

    vec2 g = vec2(id) + 0.5 + mix(lo, hi, corner);
    vQ = g / sc;
    vec2 uv = vec2((vQ.x - off.x) / P2.w, vQ.y - off.y);
    vLayer = layer;
    vUv = uv;
    gl_Position = vec4(uv * 2.0 - 1.0, 0.0, 1.0);
}
