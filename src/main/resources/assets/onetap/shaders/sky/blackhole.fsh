#version 330

#moj_import <onetap:sky_params.glsl>
#moj_import <onetap:sky_lib.glsl>
#moj_import <onetap:sky_blackhole.glsl>

#ifdef SKY_FIELD
in vec2 texCoord;
#else
in vec3 vDir;

uniform sampler2D Sampler0;
#endif

out vec4 fragColor;

const float R_IN = 3.0;
const float R_OUT = 10.0;
const float B_CRIT = 2.598076;
const float SPIN_PERIOD = 24.0;

uint ihash(ivec3 c) {
    uvec3 v = uvec3(c + 65536) * 1664525u + 1013904223u;
    v.x += v.y * v.z;
    v.y += v.z * v.x;
    v.z += v.x * v.y;
    v ^= v >> 16u;
    v.x += v.y * v.z;
    v.y += v.z * v.x;
    v.z += v.x * v.y;
    return v.x ^ v.y ^ v.z;
}

float ihash01(ivec3 c) {
    return float(ihash(c) >> 8u) * (1.0 / 16777215.0);
}

float vnoise1(float x, int seed) {
    float fl = floor(x);
    int i = int(fl);
    float f = x - fl;
    float u = f * f * (3.0 - 2.0 * f);
    return mix(ihash01(ivec3(i, seed, 7)), ihash01(ivec3(i + 1, seed, 7)), u);
}

float vnoise3(vec3 p) {
    vec3 fl = floor(p);
    ivec3 i = ivec3(fl);
    vec3 f = p - fl;
    vec3 u = f * f * (3.0 - 2.0 * f);
    float x00 = mix(ihash01(i), ihash01(i + ivec3(1, 0, 0)), u.x);
    float x10 = mix(ihash01(i + ivec3(0, 1, 0)), ihash01(i + ivec3(1, 1, 0)), u.x);
    float x01 = mix(ihash01(i + ivec3(0, 0, 1)), ihash01(i + ivec3(1, 0, 1)), u.x);
    float x11 = mix(ihash01(i + ivec3(0, 1, 1)), ihash01(i + ivec3(1, 1, 1)), u.x);
    return mix(mix(x00, x10, u.y), mix(x01, x11, u.y), u.z);
}

float nebula(vec3 dir, float t) {
    vec3 q = dir * 2.6 + vec3(0.0, t * 0.004, 0.0);
    return vnoise3(q) * 0.62 + vnoise3(q * 2.1 + 3.7) * 0.38;
}

#ifdef SKY_FIELD
void main() {
    vec2 a = (texCoord * 2.0 - 1.0) * vec2(3.14159265, 1.57079633);
    fragColor = vec4(nebula(vec3(cos(a.y) * cos(a.x), sin(a.y), cos(a.y) * sin(a.x)), Params.x), 0.0, 0.0, 0.0);
}
#else
vec3 background(vec3 dir, float t) {
    vec3 a = themeA();
    vec3 b = themeB();
    vec3 c = themeC();
    vec2 uv = vec2(atan(dir.z, dir.x) * 0.15915494 + 0.5, asin(clamp(dir.y, -1.0, 1.0)) * 0.31830989 + 0.5);
    float neb = texture(Sampler0, uv).r;
    vec3 col = mix(vec3(0.004, 0.006, 0.014), a * 0.07, 0.5);
    col += mix(a, b, neb) * smoothstep(0.45, 0.9, neb) * 0.07;
    col += starLayer(dir, 170.0, 1500.0, t, c) * 1.6;
    col += starLayer(dir, 330.0, 2800.0, t, b) * 1.1;
    col += starLayer(dir, 560.0, 5200.0, t, c) * 0.35;
    return col;
}

float lensF(float s) {
    return 1.5 * (s - s * s * s / 3.0);
}

float rings(float r, float foot) {
    float v = 0.0;
    float amp = 0.5;
    float fr = 4.0;
    for (int i = 0; i < 4; i++) {
        float fade = 1.0 - smoothstep(0.2, 0.7, fr * foot);
        v += (vnoise1(r * fr, i) - 0.5) * amp * fade;
        fr *= 2.17;
        amp *= 0.62;
    }
    return v;
}

float clumps(float r, float phi, float phase, float seed) {
    float a = phi - 2.0 * pow(r, -1.5) * phase;
    vec3 q = vec3(cos(a) * r, sin(a) * r, seed) * 0.75;
    return vnoise3(q);
}

vec4 diskSample(vec3 hit, vec3 vel, vec3 n, vec3 t1, vec3 t2, float foot, float t) {
    float r = length(hit);
    float phi = atan(dot(hit, t2), dot(hit, t1));

    float ph1 = mod(t, SPIN_PERIOD);
    float ph2 = mod(t + SPIN_PERIOD * 0.5, SPIN_PERIOD);
    float wgt = abs(ph1 / SPIN_PERIOD * 2.0 - 1.0);
    float cl = mix(clumps(r, phi, ph1, 0.0), clumps(r, phi, ph2, 9.7), wgt);
    float dens = clamp(0.55 + rings(r, foot) * 1.2 + (cl - 0.5) * 0.8, 0.0, 1.4);

    float edge = smoothstep(R_IN, R_IN + 0.35, r) * (1.0 - smoothstep(R_OUT - 3.5, R_OUT, r));
    float temp = pow(R_IN / r, 0.75) * pow(max(1.0 - sqrt(R_IN / r) * 0.92, 0.0), 0.25) * 1.9;

    vec3 orbit = normalize(cross(n, hit));
    float beta = sqrt(0.5 / (r - 1.0));
    float cosT = dot(orbit, -normalize(vel));
    float g = sqrt(1.0 - beta * beta) / (1.0 - beta * cosT);
    g *= sqrt(1.0 - 1.0 / r);
    float beam = clamp(pow(g, 3.5), 0.08, 5.0);

    vec3 b = themeB();
    vec3 c = themeC();
    vec3 hot = mix(c, vec3(1.0, 0.97, 0.92), 0.3);
    vec3 col = mix(b * 0.9, hot, smoothstep(0.25, 1.1, temp));
    col = mix(col, mix(col, vec3(0.75, 0.88, 1.0), 0.6), smoothstep(1.1, 2.2, g));
    col = mix(col, col * vec3(1.0, 0.55, 0.35), smoothstep(0.9, 0.4, g));

    float lum = temp * temp * (0.4 + dens * 0.9) * beam;
    float alpha = clamp(edge * (0.55 + dens * 0.35), 0.0, 1.0);
    return vec4(col * lum * edge * 1.1, alpha);
}

vec3 renderBlackHole(vec3 rd) {
    float t = Params.x;
    float dist = holeDist();

    vec3 hDir = holeDir();
    vec3 center = hDir * dist;

    vec3 up = normalize(vec3(0.0, 1.0, 0.0) - hDir * hDir.y);
    vec3 side = normalize(cross(hDir, up));
    float roll = 0.32;
    vec3 nPerp = up * cos(roll) + side * sin(roll);
    float tilt = 0.13;
    vec3 n = normalize(nPerp * cos(tilt) - hDir * sin(tilt));
    vec3 t1 = normalize(cross(n, hDir));
    vec3 t2 = cross(n, t1);

    vec3 oc = -center;
    float along = dot(oc, rd);
    vec3 perpV = oc - rd * along;
    float b = max(length(perpV), 1e-4);
    vec3 toC = -perpV / b;
    float sCam = along / dist;
    float bw = max(fwidth(b), 1e-4);
    float pixAngle = length(fwidth(rd));

    vec3 ringCol = mix(themeC(), vec3(1.0), 0.5);

#ifdef SKY_BACKGROUND
    if (b < R_MARCH * 0.98 && along < 0.0) discard;
    bool marched = false;
#else
    bool marched = b < R_MARCH && along < 0.0;
    if (!marched) discard;
#endif
    float halfChord = sqrt(max(R_MARCH * R_MARCH - b * b, 0.0));
    float travelled = marched ? -along - halfChord : 0.0;
    vec3 p = oc + rd * travelled;
    vec3 vel = rd;
    float preBend = marched ? (lensF(-halfChord / R_MARCH) - lensF(sCam)) / b : 0.0;
    vec3 hv = cross(p, vel);
    float h2 = dot(hv, hv);

    vec3 col = vec3(0.0);
    float alpha = 0.0;
    bool escaped = !marched;
    int steps = marched ? 160 : 0;
    float r0 = length(p);
    vec3 acc = -1.5 * h2 * p / (r0 * r0 * r0 * r0 * r0);
    float h2Crit = B_CRIT * B_CRIT;

#ifndef SKY_BACKGROUND
    for (int i = 0; i < 160; i++) {
        if (i >= steps) break;
        float r = length(p);
        float radial = dot(p, vel);
        if (r < 1.0) break;
        if (r < R_IN && radial < 0.0 && h2 < h2Crit) break;
        if (r > R_MARCH && radial > 0.0) {
            escaped = true;
            break;
        }
        if (alpha > 0.985) break;

        float dt = clamp(r * mix(0.1, 0.2, smoothstep(3.0, 5.0, r)), 0.03, 2.0);
        vec3 prev = p;
        vec3 vPrev = vel;
        vec3 aPrev = acc;
        vel += acc * (0.5 * dt);
        p += vel * dt;
        float r2 = dot(p, p);
        acc = -1.5 * h2 * p / (r2 * r2 * sqrt(r2));
        vel += acc * (0.5 * dt);

        float rNew = sqrt(r2);
        bool leaving = rNew > R_MARCH && dot(p, vel) > 0.0;
        if (leaving) {
            dt *= clamp((R_MARCH - r) / max(rNew - r, 1e-4), 0.0, 1.0);
            vel = vPrev + aPrev * (0.5 * dt);
            p = prev + vel * dt;
            r2 = dot(p, p);
            acc = -1.5 * h2 * p / (r2 * r2 * sqrt(r2));
            vel += acc * (0.5 * dt);
        }
        travelled += dt;

        float s0 = dot(prev, n);
        float s1 = dot(p, n);
        if (s0 * s1 < 0.0) {
            vec3 hit = mix(prev, p, s0 / (s0 - s1));
            float hr = length(hit);
            if (hr > R_IN && hr < R_OUT) {
                float foot = pixAngle * travelled / max(abs(dot(normalize(vel), n)), 0.1);
                vec4 d = diskSample(hit, vel, n, t1, t2, foot, t);
                col += (1.0 - alpha) * d.rgb * d.a;
                alpha += (1.0 - alpha) * d.a;
            }
        }
        if (leaving) {
            escaped = true;
            break;
        }
    }
#endif

    float bb = marched ? sqrt(h2) : max(sqrt(h2), R_MARCH);
    float cov = smoothstep(B_CRIT + bw, B_CRIT - bw, bb);
    float ringW = max(0.03, bw * 1.5);
    float ring = exp(-pow((bb - B_CRIT - ringW * 0.5) / ringW, 2.0)) * 0.9;
    float halo = exp(-max(bb - B_CRIT, 0.0) * 0.7) * 0.1 * (1.0 - cov);

    vec3 bg = vec3(0.0);
    if (escaped) {
        vec3 v = normalize(vel);
        float pr = length(p);
        vec3 perpE = p - v * dot(p, v);
        float bE = max(length(perpE), 1e-4);
        vec3 bent = normalize(v - perpE / bE * ((1.0 - lensF(dot(p, v) / pr)) / bE) + toC * preBend);
        bg = background(bent, t) * (1.0 - cov);
    }

    vec3 outCol = col + (1.0 - alpha) * (bg + ringCol * (ring + halo));
    return tonemap(outCol * Params.w);
}

void main() {
    fragColor = vec4(renderBlackHole(normalize(vDir)), 1.0);
}
#endif
