float wMorning;
float wDay;
float wNight;
float gPx;
float gTime;

vec3 phased(vec3 m, vec3 d, vec3 n) {
    return m * wMorning + d * wDay + n * wNight;
}

float phasedF(float m, float d, float n) {
    return m * wMorning + d * wDay + n * wNight;
}

vec3 lightDir() {
    float el = phasedF(0.075, 1.05, 0.62);
    float az = phasedF(0.72, -0.35, 2.25);
    return vec3(cos(el) * cos(az), sin(el), cos(el) * sin(az));
}

vec3 skyColor(vec3 d, vec3 sd) {
    float h = max(d.y, 0.0);
    float t = pow(h, 0.45);
    vec3 zen = phased(vec3(0.17, 0.23, 0.50), vec3(0.12, 0.33, 0.78), vec3(0.004, 0.007, 0.026));
    vec3 mid = phased(vec3(0.68, 0.50, 0.66), vec3(0.40, 0.61, 0.92), vec3(0.030, 0.026, 0.075));
    vec3 hor = phased(vec3(1.00, 0.58, 0.38), vec3(0.72, 0.84, 0.97), vec3(0.17, 0.065, 0.18));
    vec3 col = t < 0.5 ? mix(hor, mid, t * 2.0) : mix(mid, zen, t * 2.0 - 1.0);
    float mu = max(dot(d, sd), 0.0);
    col += phased(vec3(0.95, 0.42, 0.16), vec3(0.28, 0.26, 0.22), vec3(0.04, 0.045, 0.07)) * pow(mu, phasedF(5.0, 9.0, 14.0));
    col += vec3(1.0, 0.48, 0.24) * wMorning * exp(-h * 9.0) * (0.2 + 0.5 * mu);
    col += vec3(0.55, 0.16, 0.42) * wNight * exp(-h * 10.0) * 0.32;
    return col;
}

vec3 celestial(vec3 col, vec3 d, vec3 sd) {
    float mu = dot(d, sd);
    float ang = acos(clamp(mu, -1.0, 1.0));
    float r = phasedF(0.036, 0.022, 0.026);
    float disc = 1.0 - smoothstep(r - gPx, r + gPx, ang);
    float m = max(mu, 0.0);
    if (wNight > 0.5) {
        float limb = 1.0 - smoothstep(0.0, 1.0, ang / r);
        vec3 moon = vec3(0.86, 0.88, 0.95) * (0.8 + 0.2 * limb);
        col = mix(col, moon * 0.75, disc);
        col += vec3(0.55, 0.62, 0.85) * (pow(m, 900.0) * 0.35 + pow(m, 60.0) * 0.05);
    } else {
        vec3 sun = phased(vec3(1.0, 0.52, 0.22) * 3.2, vec3(1.0, 0.96, 0.88) * 4.5, vec3(0.0));
        col = mix(col, sun, disc);
        col += phased(vec3(1.0, 0.5, 0.2), vec3(1.0, 0.9, 0.7), vec3(0.0)) * (pow(m, 1400.0) * 1.2 + pow(m, 120.0) * 0.3);
    }
    return col;
}

vec3 stars(vec3 col, vec3 d) {
    if (wNight < 0.5 || d.y <= 0.0) return col;
    float vis = smoothstep(0.08, 0.5, d.y);
    vec3 s = starLayer(d, 150.0, 2200.0, gTime, vec3(0.8, 0.85, 1.0));
    s += starLayer(d, 290.0, 3600.0, gTime, vec3(1.0, 0.85, 0.8)) * 0.5;
    return col + s * vis * 0.55;
}

vec2 cloudNoise(vec3 d) {
    float sc = clamp(Params.z, 0.5, 3.0);
    vec2 p = d.xz / (max(d.y, 0.0) + 0.06);
    vec3 q = vec3(p * (0.42 / sc) + vec2(Params.x * 0.010, Params.x * 0.004), Params.x * 0.002);
    return vec2(fbm3(q), fbm3(q * 3.1 + vec3(4.0, 1.0, 2.0)));
}

vec3 clouds(vec3 col, vec3 d, vec3 sd, vec2 noise) {
    if (d.y < 0.004) return col;
    float n = noise.x * 0.5 + 0.5;
    float n2 = noise.y * 0.5 + 0.5;
    float dens = smoothstep(phasedF(0.50, 0.53, 0.52), 0.86, n * 0.82 + n2 * 0.24);
    dens *= smoothstep(0.0, 0.2, d.y);
    if (dens <= 0.0) return col;
    float lit = clamp(0.5 + (n2 - 0.5) * 1.6 + (n - 0.6), 0.0, 1.0);
    float mu = max(dot(d, sd), 0.0);
    vec3 litCol = phased(vec3(1.0, 0.68, 0.58), vec3(1.0, 1.0, 1.0), vec3(0.085, 0.07, 0.11));
    vec3 shadeCol = phased(vec3(0.44, 0.30, 0.48), vec3(0.62, 0.68, 0.78), vec3(0.022, 0.02, 0.04));
    vec3 c = mix(shadeCol, litCol, lit);
    c += phased(vec3(1.0, 0.5, 0.22), vec3(0.25, 0.25, 0.22), vec3(0.06, 0.06, 0.09)) * pow(mu, 6.0) * 0.7;
    c += vec3(0.32, 0.09, 0.26) * wNight * exp(-d.y * 5.0) * (1.0 - lit * 0.5);
    return mix(col, c, dens * phasedF(0.85, 0.82, 0.75));
}

vec3 renderTokyo(vec3 d, vec2 noise) {
    float phase = Extra.x;
    wMorning = 1.0 - clamp(abs(phase), 0.0, 1.0);
    wDay = 1.0 - clamp(abs(phase - 1.0), 0.0, 1.0);
    wNight = 1.0 - clamp(abs(phase - 2.0), 0.0, 1.0);
    gTime = Params.x;

    vec3 sd = lightDir();

    vec3 col = skyColor(d, sd);
    col = stars(col, d);
    col = celestial(col, d, sd);
    col = clouds(col, d, sd, noise);
    return tonemap(col * (0.8 + 0.2 * Params.w));
}
