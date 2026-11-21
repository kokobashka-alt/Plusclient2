#version 330

#moj_import <onetap:weather_common.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
#ifdef USE_PUDDLE_MAP
uniform sampler2D Sampler3;
#endif

in vec2 texCoord;
out vec4 fragColor;

vec2 gWind;
float gTime;
float gPix;
float gAnchor;

const float PUDDLE_SPAN = 160.0;
const float PUDDLE_WRAP = 2048.0;
const int SSR_STEPS = 48;
const float SSR_DIST = 48.0;

vec2 landShift(float y) {
    return gWind * clamp(y - gAnchor, -4.0, 4.0);
}

float puddleNoise(vec2 p) {
    p *= 0.11;
    vec2 warp = vec2(vnoise(p * 1.6 + 3.1), vnoise(p * 1.6 + 11.7)) - 0.5;
    p += warp * 1.6;
    mat2 rot = mat2(0.8, -0.6, 0.6, 0.8);
    float s = 0.0;
    float a = 0.5;
    for (int i = 0; i < 4; i++) {
        s += a * vnoise(p);
        p = rot * p * 2.03 + 5.3;
        a *= 0.5;
    }
    return s;
}

vec2 rippleGrad(vec2 pAbs, float yImp, float cs, float pres) {
    vec2 sp = pAbs - landShift(yImp);
    ivec2 base = ivec2(floor(sp / cs));
    vec2 g = vec2(0.0);
    for (int j = -1; j <= 1; j++) {
        for (int i = -1; i <= 1; i++) {
            ivec2 cell = base + ivec2(i, j);
            uint h = hash2(cell, 0u);
            if (u01(h) > pres) continue;
            float ph = u01(pcg(h + 3u));
            float nn = 800.0 + floor(u01(pcg(h + 4u)) * 300.0);
            float vN = nn / 1024.0;
            float head = H * fract(ph - vN * gTime);
            float age = mod(mod(yImp, H) - head, H) / (H * vN);
            if (age > 0.9) continue;
            vec2 c = (vec2(cell) + 0.2 + 0.6 * vec2(u01(pcg(h + 1u)), u01(pcg(h + 2u)))) * cs;
            vec2 dv = sp - c;
            float r = length(dv);
            if (r > 0.6) continue;
            float R = age * 0.4 + 0.012;
            float sg = 0.012 + 0.012 * age;
            float x = r - R;
            float env = exp(-(x * x) / (sg * sg));
            float amp = exp(-age * 2.4) * (1.0 - smoothstep(0.45, 0.85, age)) * smoothstep(0.0, 0.03, age);
            float fr = 3.14159265 / (1.4 * sg);
            float dh = amp * env * (fr * cos(fr * x) - 2.0 * x / (sg * sg) * sin(fr * x));
            g += dh * dv / max(r, 1e-4);
        }
    }
    return g * 0.0011;
}

vec2 viewToUv(vec3 p) {
    return p.xy * ProjInfo.zw / -p.z * 0.5 + 0.5;
}

float rayEdge(float s, float d) {
    if (d > 0.0) return (1.0 - s) / d;
    if (d < 0.0) return -s / d;
    return 1.0;
}

vec4 ssr(vec3 prel, vec3 R, vec3 N) {
    vec3 o = (View * vec4(prel + N * 0.03, 1.0)).xyz;
    vec3 Rv = mat3(View) * R;
    float facing = 1.0 - smoothstep(0.0, 0.3, Rv.z);
    if (facing <= 0.0) return vec4(0.0);
    float len = SSR_DIST;
    if (Rv.z > 0.0) len = min(len, (-0.1 - o.z) / Rv.z);
    if (len <= 0.0) return vec4(0.0);
    vec3 e = o + Rv * len;
    vec2 s0 = viewToUv(o);
    vec2 d = viewToUv(e) - s0;
    float iz0 = -1.0 / o.z;
    float iz1 = -1.0 / e.z;
    float tMax = min(1.0, min(rayEdge(s0.x, d.x), rayEdge(s0.y, d.y)));
    float pixels = length(d * vec2(textureSize(Sampler1, 0))) * tMax;
    int steps = int(clamp(pixels / 3.0, 4.0, float(SSR_STEPS)));
    float jitter = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    float tPrev = 0.0;
    float zPrev = o.z;
    for (int i = 1; i <= SSR_STEPS; i++) {
        if (i > steps) break;
        float t = tMax * (float(i) - jitter) / float(steps);
        float z = -1.0 / mix(iz0, iz1, t);
        float sz = viewZ(texture(Sampler1, s0 + d * t).r);
        float thick = abs(z - zPrev) + 0.08 - z * 0.01;
        if (z < sz && z > sz - thick) {
            float lo = tPrev;
            float hi = t;
            for (int k = 0; k < 6; k++) {
                float mid = 0.5 * (lo + hi);
                if (-1.0 / mix(iz0, iz1, mid) < viewZ(texture(Sampler1, s0 + d * mid).r)) hi = mid; else lo = mid;
            }
            vec2 uvh = s0 + d * hi;
            vec2 eb = min(uvh, 1.0 - uvh);
            float edge = smoothstep(0.0, 0.08, min(eb.x, eb.y));
            float reach = 1.0 - smoothstep(0.7, 1.0, hi);
            return vec4(texture(Sampler0, uvh).rgb, edge * facing * reach);
        }
        tPrev = t;
        zPrev = z;
    }
    return vec4(0.0);
}

vec2 openWarp(vec2 pxz) {
    return (vec2(vnoise(pxz * 0.8 + 21.0), vnoise(pxz * 0.8 + 47.0)) - 0.5) * 0.9;
}

float openFraction(vec2 pxz, vec2 warp, float py) {
    vec2 q = pxz + warp;
    vec2 c = floor(q - 0.5) + 0.5;
    float s = 0.0;
    float ws = 0.0;
    for (int j = -1; j <= 2; j++) {
        for (int i = -1; i <= 2; i++) {
            vec2 tp = c + vec2(float(i), float(j));
            vec2 dv = tp - q;
            float w = exp(-dot(dv, dv) * 0.9);
            float h = heightAt(tp).x;
            s += w * (1.0 - smoothstep(0.2, 0.55, h - py));
            ws += w;
        }
    }
    return s / ws;
}

float skyVisibility(vec3 p, vec3 R) {
    float t = 0.4;
    float vis = 1.0;
    for (int i = 0; i < 14; i++) {
        vec3 q = p + R * t;
        vis = min(vis, smoothstep(-0.6, 0.2, q.y - heightAt(q.xz).x));
        if (vis <= 0.0 || q.y > P1.z + 1.0) break;
        t *= 1.35;
    }
    return vis;
}

#ifdef USE_PUDDLE_MAP
vec3 wetShade(vec3 scene, vec3 prel, vec3 rd, float ts, vec3 dPdx, vec3 dPdy) {
    vec3 pabs = prel + CamPos.xyz;
    vec2 hi = heightAt(pabs.xz);

    vec3 N = normalize(cross(dPdx, dPdy));
    if (dot(N, rd) > 0.0) N = -N;
    float pixSize = ts * gPix;
    float ratio = max(length(dPdx), length(dPdy)) / max(pixSize, 1e-5);
    float planar = 1.0 - smoothstep(10.0, 16.0, ratio);
    float lim = min(WindRange.z * 1.1, 58.0);
    float dfade = 1.0 - smoothstep(0.5 * lim, lim, ts);
    float upward = smoothstep(0.82, 0.96, N.y) * planar * dfade;
    if (upward <= 0.001) return scene;
    vec3 baked = texture(Sampler3, pabs.xz * (1.0 / PUDDLE_SPAN)).rgb;
    float openFrac = openFraction(pabs.xz, baked.gb, pabs.y);
    float expo = upward * smoothstep(0.05, 0.5, openFrac);
    if (expo <= 0.001) return scene;

    float wet = P0.y;
    float water = isWater(hi) ? 1.0 : 0.0;
    float pn = baked.r;
    float thr = mix(0.80, 0.47, wet);
    float puddle = smoothstep(thr, thr + 0.13, pn) * smoothstep(0.25, 0.7, wet);
    puddle = max(puddle, water * smoothstep(0.02, 0.2, wet));
    float gloss = max(puddle, 0.32 * wet);

    vec2 grad = vec2(0.0);
    float rain = P0.x;
    if (rain > 0.02 && P0.z < 0.5) {
        float rw = max(puddle, 0.22 * wet) * smoothstep(0.35, 0.8, openFrac);
        rw *= 1.0 - smoothstep(18.0, 28.0, ts);
        if (rw > 0.01) grad = rippleGrad(pabs.xz, pabs.y, P2.z, clamp(P0.w * 0.6, 0.0, 1.0)) * rw;
    }
    vec3 Np = normalize(vec3(-grad.x, 1.0, -grad.y));

    vec3 dark = scene * (1.0 - 0.3 * wet * (0.45 + 0.55 * gloss));
    float cosT = clamp(dot(-rd, Np), 0.0, 1.0);
    float fres = 0.03 + 0.97 * pow(1.0 - cosT, 5.0);
    float wgt = gloss * (0.25 + 0.75 * fres) * mix(0.9, 1.0, puddle);
    wgt = clamp(wgt * (0.35 + 0.65 * smoothstep(0.1, 0.9, wet)) * 1.15, 0.0, 0.92);
    wgt *= mix(1.0, 0.8, water);

    vec3 R = reflect(rd, Np);
    vec3 refl = Sky.rgb * (0.8 + 0.3 * clamp(R.y, 0.0, 1.0));
    if (wgt * expo > 0.03) refl = mix(dark * 0.55, refl, skyVisibility(pabs, R));
    if (P2.y > 0.5 && wgt * expo > 0.03) {
        vec4 s = ssr(prel, R, Np);
        refl = mix(refl, s.rgb, s.a);
    }

    vec3 col = mix(dark, refl, wgt * expo);
    float glint = length(grad) * 95.0 * (0.3 + Sky.a) * 0.14;
    col += vec3(0.85, 0.92, 1.0) * glint * expo * 0.35 * clamp(rain * 3.0, 0.0, 1.0);
    return mix(scene, col, expo);
}
#endif

void main() {
    vec2 uv = texCoord;
    gWind = WindRange.xy;
    gTime = CamPos.w;
    gPix = P1.w;
    gAnchor = Gust.w;

#if defined(PUDDLE_BAKE)
    vec2 m = uv * PUDDLE_SPAN;
    vec2 world = mod(m + PUDDLE_SPAN * floor((CamPos.xz - m) / PUDDLE_SPAN + 0.5), PUDDLE_WRAP);
    fragColor = vec4(puddleNoise(world) + 0.05 * (vnoise(world * 1.3) - 0.5), openWarp(world), 1.0);
#else
    float depth = texture(Sampler1, uv).r;
    vec3 dirV = vec3((uv * 2.0 - 1.0) / ProjInfo.zw, -1.0);
    float ts = -viewZ(depth) * length(dirV);
    vec3 rd = normalize(mat3(InvView) * dirV);
    vec3 prel = InvView[3].xyz + rd * ts;
    float range = WindRange.z;
    float mist = P0.x > 0.01 ? (1.0 - exp(-min(ts, range) * 0.018)) * P0.x * (P0.z > 0.5 ? 0.10 : 0.06) : 0.0;
#ifdef MIST_ONLY
    fragColor = vec4(Sky.rgb * mist, mist);
#else
    vec3 dPdx = dFdx(prel);
    vec3 dPdy = dFdy(prel);
    vec3 col = texture(Sampler0, uv).rgb;
    if (P0.y > 0.01 && ts < min(range * 1.1, 58.0)) col = wetShade(col, prel, rd, ts, dPdx, dPdy);
    fragColor = vec4(mix(col, Sky.rgb, mist), 1.0);
#endif
#endif
}
