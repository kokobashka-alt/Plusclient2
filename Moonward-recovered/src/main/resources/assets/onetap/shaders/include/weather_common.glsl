layout(std140) uniform WeatherParams {
    mat4 View;
    mat4 InvView;
    vec4 ProjInfo;
    vec4 CamPos;
    vec4 WindRange;
    vec4 P0;
    vec4 P1;
    vec4 P2;
    vec4 Color;
    vec4 Sky;
    vec4 Wind12;
    vec4 Gust;
    vec4 Grid;
    vec4 Grid2;
    vec4 LensGrid;
    vec4 LensGrid2;
};

uniform sampler2D Sampler2;

const float H = 14.0;
const float TAU = 6.28318530718;
const float HMAP = 128.0;

uint pcg(uint v) {
    uint s = v * 747796405u + 2891336453u;
    uint w = ((s >> ((s >> 28u) + 4u)) ^ s) * 277803737u;
    return (w >> 22u) ^ w;
}

uint hash2(ivec2 c, uint salt) {
    return pcg(pcg(uint(c.x) ^ (salt * 2654435761u)) + uint(c.y));
}

float u01(uint h) {
    return float(h >> 8) * (1.0 / 16777216.0);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);
    ivec2 ii = ivec2(i);
    float a = u01(hash2(ii, 11u));
    float b = u01(hash2(ii + ivec2(1, 0), 11u));
    float c = u01(hash2(ii + ivec2(0, 1), 11u));
    float d = u01(hash2(ii + ivec2(1, 1), 11u));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float sheetMod(vec2 c) {
    vec2 q = (c - Gust.yz) * 0.045;
    float n = vnoise(q) * 0.65 + vnoise(q * 2.3 + 7.0) * 0.35;
    return mix(1.0, 0.3 + 1.5 * smoothstep(0.25, 0.75, n), Gust.x);
}

vec2 heightAt(vec2 xzAbs) {
    vec2 s = textureLod(Sampler2, fract(xzAbs / HMAP), 0.0).rg;
    return vec2(s.r * 4096.0 - 512.0, s.g);
}

bool isWater(vec2 h) {
    return h.y > 0.99999;
}

float surfaceAt(vec2 xzAbs, vec2 h) {
    if (isWater(h)) return h.x;
    vec2 f = fract(xzAbs);
    uint drop = (uint(h.y * 65535.0 + 0.5) >> ((f.y >= 0.5 ? 8u : 0u) + (f.x >= 0.5 ? 4u : 0u))) & 15u;
    return drop == 15u ? -1e4 : h.x - float(drop) * (1.0 / 16.0);
}

float viewZ(float d) {
    float ndc = WindRange.w > 0.5 ? d : d * 2.0 - 1.0;
    return -ProjInfo.y / max(ndc + ProjInfo.x, 1e-7);
}
