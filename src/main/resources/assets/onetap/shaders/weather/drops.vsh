#version 330

#moj_import <onetap:weather_common.glsl>

out vec2 vUv;
out float vAmp;
out float vDepth;
out float vY;
flat out vec4 vData;
flat out float vCeil;

const vec2 CORNERS[4] = vec2[](vec2(-1.0, 0.0), vec2(1.0, 0.0), vec2(1.0, 1.0), vec2(-1.0, 1.0));

float gPix;
float gAnchor;
float gTime;
vec3 gCam;

void emit(vec3 rel) {
    vec3 pv = (View * vec4(rel, 1.0)).xyz;
    gl_Position = vec4(pv.xy * ProjInfo.zw, pv.z * ProjInfo.x + ProjInfo.y, -pv.z);
    vDepth = -pv.z;
}

void cull() {
    gl_Position = vec4(0.0, 0.0, 2.0, 1.0);
}

float layerCs(int layer, float s0) {
    return P2.z * s0 * (layer == 0 ? 1.0 : (layer == 1 ? 2.0 : 4.0));
}

vec2 layerWind(int layer) {
    return layer == 0 ? WindRange.xy : (layer == 1 ? Wind12.xy : Wind12.zw);
}

float layerOut(int layer) {
    float r = WindRange.z;
    return layer == 0 ? min(9.0, r) : (layer == 1 ? min(24.0, r) : r);
}

float layerIn(int layer) {
    return layer == 0 ? 0.15 : (layer == 1 ? 9.0 : 24.0);
}

const float RING_BAND = 2.0;

float layerReach(int layer) {
    return layerOut(layer) + (layer < 2 ? RING_BAND : 0.0);
}

float ringFade(int layer, float dist) {
    float inner = layer == 0 ? smoothstep(0.15, 0.4, dist)
        : smoothstep(layerIn(layer) - RING_BAND, layerIn(layer) + RING_BAND, dist);
    float outer = layer == 2 ? 1.0 - step(layerOut(layer), dist)
        : 1.0 - smoothstep(layerOut(layer) - RING_BAND, layerOut(layer) + RING_BAND, dist);
    return inner * outer;
}

float spawnFade(uint h, float presence) {
    return clamp((clamp(presence, 0.0, 1.0) - u01(h)) * 25.0, 0.0, 1.0);
}

ivec2 columnCell(int n, int col, vec2 o2, float cs) {
    ivec2 base = ivec2(floor(o2 / cs));
    return base + ivec2(col % n - n / 2, col / n - n / 2);
}

void rainStreak(int layer, int local, vec2 corner) {
    int n = int(Grid[layer]);
    int kN = int(Grid2[layer]);
    int col = local / kN;
    int k = local - col * kN;
    float cs = layerCs(layer, 1.0);
    vec2 wind = layerWind(layer);
    float reach = layerReach(layer);
    vec2 o2 = gCam.xz - wind * (gCam.y - gAnchor);
    ivec2 cell = columnCell(n, col, o2, cs);

    uint h1 = hash2(cell, uint(layer));
    float spawn = spawnFade(h1, P0.w);
    if (spawn <= 0.0) { cull(); return; }
    uint h2 = pcg(h1 + 1u);
    vec2 jit = vec2(float(h2 & 1023u), float((h2 >> 10u) & 1023u)) * (1.0 / 1023.0);
    vec2 c = (vec2(cell) + 0.2 + 0.6 * jit) * cs;
    if (length(c - o2) > reach + cs) { cull(); return; }

    uint h3 = pcg(h2 + 1u);
    float ph = float((h2 >> 20u) & 2047u) * (1.0 / 2047.0);
    float vN = (800.0 + float(h3 % 300u)) * (1.0 / 1024.0);
    float head = H * fract(ph - vN * gTime);
    float yb = head + H * (floor((gCam.y - reach - head) / H) + float(k));
    float L = P1.x;
    float yt = yb + L;

    if (layer == 0) {
        float famp = 0.12 * cs * Gust.x;
        float kf = 1800.0 + float((h3 >> 9u) % 1400u);
        float tk = gTime * (1.0 / 1024.0);
        c += famp * vec2(sin(TAU * (kf * tk + ph)), cos(TAU * ((kf + 137.0) * tk + ph * 7.0)));
    }

    vec2 bxz = c + wind * (yb - gAnchor);
    vec2 txz = c + wind * (yt - gAnchor);
    vec3 B = vec3(bxz.x, yb, bxz.y) - gCam;
    vec3 T = vec3(txz.x, yt, txz.y) - gCam;
    vec3 M = 0.5 * (B + T);
    float dist = length(M);
    float ring = ringFade(layer, dist) * spawn;
    if (ring <= 0.0) { cull(); return; }
    float fade = ring * smoothstep(0.25, 2.2, dist) * (1.0 - smoothstep(0.7 * WindRange.z, WindRange.z, dist));
    if (fade <= 0.0) { cull(); return; }
    float ceilY = heightAt(bxz).x;
    if (yt < ceilY) { cull(); return; }

    float w = (layer == 0 ? 0.012 : (layer == 1 ? 0.020 : 0.034)) * P1.y;
    vec3 P = mix(B, T, corner.y);
    float fw = max(length(P) * gPix, 1e-5);
    float hwCore = 0.5 * max(w, fw);
    float hwQuad = hwCore * 1.25 + 0.5 * fw;
    vec3 side = cross(T - B, P);
    float sl = length(side);
    side = sl > 1e-6 ? side / sl : normalize(cross(vec3(0.0, 1.0, 0.0), InvView[2].xyz));
    P += side * hwQuad * corner.x;

    float glint = 1.0 + 1.2 * pow(float((h3 >> 20u) & 255u) * (1.0 / 255.0), 5.0);
    vUv = vec2(corner.x * hwQuad / hwCore, corner.y);
    vAmp = min(1.0, w / fw);
    vY = mix(yb, yt, corner.y);
    vCeil = ceilY;
    vData = vec4(0.0, Color.a * fade * sheetMod(c), glint, 0.0);
    emit(P);
}

void rainBead(int local, vec2 corner) {
    int n = int(Grid.x);
    int col = local / 3;
    int k = local - col * 3;
    float cs = layerCs(0, 1.0);
    vec2 wind = WindRange.xy;
    vec2 o2 = gCam.xz - wind * (gCam.y - gAnchor);
    ivec2 cell = columnCell(n, col, o2, cs);

    uint h1 = hash2(cell, 0u);
    float spawn = spawnFade(h1, P0.w);
    if (spawn <= 0.0) { cull(); return; }
    uint h2 = pcg(h1 + 1u);
    vec2 jit = vec2(float(h2 & 1023u), float((h2 >> 10u) & 1023u)) * (1.0 / 1023.0);
    vec2 c = (vec2(cell) + 0.2 + 0.6 * jit) * cs;
    if (length(c - o2) > 9.0 + cs) { cull(); return; }

    uint h3 = pcg(h2 + 1u);
    float ph = float((h2 >> 20u) & 2047u) * (1.0 / 2047.0);
    float vN = (800.0 + float(h3 % 300u)) * (1.0 / 1024.0);
    float v = H * vN;
    float head = H * fract(ph - vN * gTime);

    float y0 = heightAt(c).x;
    float yImp = heightAt(c + wind * clamp(y0 - gAnchor, -4.0, 4.0)).x;
    if (yImp < -400.0) { cull(); return; }
    float age = mod(mod(yImp, H) - head, H) / v;
    if (age > 0.45) { cull(); return; }
    vec2 cr = c + wind * clamp(yImp - gAnchor, -4.0, 4.0);

    uint hk = pcg(h3 + 10u + uint(k));
    float ang = TAU * u01(hk);
    float sp = 0.2 + 0.2 * u01(pcg(hk + 1u));
    float vy = 1.3 + 1.6 * u01(pcg(hk + 2u));
    vec3 pos = vec3(cr.x + cos(ang) * sp * age, yImp + 0.012 + vy * age - 4.9 * age * age, cr.y + sin(ang) * sp * age);
    if (pos.y < yImp) { cull(); return; }
    vec3 rel = pos - gCam;
    float dist = length(rel);
    if (dist >= 9.0) { cull(); return; }

    float rb = 0.016 * P1.y;
    float fw = max(dist * gPix, 1e-5);
    float hw = 0.5 * max(2.0 * rb, fw);
    float qh = hw * 1.1 + 0.5 * fw;
    float cy = corner.y * 2.0 - 1.0;
    rel += (InvView[0].xyz * corner.x + InvView[1].xyz * cy) * qh;

    float a = min(1.0, 2.0 * rb / fw) * (1.0 - age / 0.45) * 0.85 * Color.a * spawn * (1.0 - smoothstep(7.0, 9.0, dist))
        * (1.0 - smoothstep(0.55 * WindRange.z, 0.85 * WindRange.z, dist));
    vUv = vec2(corner.x, cy) * (qh / hw);
    vAmp = 1.0;
    vY = 1e4;
    vCeil = -1e4;
    vData = vec4(1.0, a, 1.5, 0.3);
    emit(rel);
}

void snowFlake(int layer, int local, vec2 corner) {
    int n = int(Grid[layer]);
    int kN = int(Grid2[layer]);
    int col = local / kN;
    int k = local - col * kN;
    float cs = layerCs(layer, 0.62);
    vec2 wind = layerWind(layer);
    float reach = layerReach(layer);
    vec2 o2 = gCam.xz - wind * (gCam.y - gAnchor);
    ivec2 cell = columnCell(n, col, o2, cs);

    uint h1 = hash2(cell, uint(layer));
    float spawn = spawnFade(h1, P0.w);
    if (spawn <= 0.0) { cull(); return; }
    uint h2 = pcg(h1 + 1u);
    vec2 jit = vec2(float(h2 & 1023u), float((h2 >> 10u) & 1023u)) * (1.0 / 1023.0);
    vec2 base = (vec2(cell) + 0.3 + 0.4 * jit) * cs;
    if (length(base - o2) > reach + cs) { cull(); return; }

    uint h3 = pcg(h2 + 1u);
    float ph = float((h2 >> 20u) & 2047u) * (1.0 / 2047.0);
    float vN = (160.0 + float(h3 % 160u)) * (1.0 / 1024.0);
    float mA = 500.0 + float((h3 >> 8u) % 700u);
    float mB = 500.0 + float((h3 >> 18u) % 700u);
    float tk = gTime * (1.0 / 1024.0);
    vec2 c = base + 0.22 * cs * vec2(sin(TAU * (mA * tk + ph)), cos(TAU * (mB * tk + ph * 3.7)));

    float head = H * fract(ph - vN * gTime);
    float fy = head + H * (floor((gCam.y - reach - head) / H) + float(k));
    vec2 pxz = c + wind * (fy - gAnchor);
    vec3 rel = vec3(pxz.x, fy, pxz.y) - gCam;
    float dist = length(rel);
    float ring = ringFade(layer, dist) * spawn;
    if (ring <= 0.0) { cull(); return; }
    float ceilY = heightAt(pxz).x;
    float occl = smoothstep(ceilY - 0.01, ceilY + 0.06, fy);
    if (occl <= 0.0) { cull(); return; }

    float w = (layer == 0 ? 0.012 : (layer == 1 ? 0.020 : 0.034)) * P1.y * 2.2;
    float hr = float((h2 >> 12u) & 255u) * (1.0 / 255.0);
    float rs = (0.5 + 0.9 * hr) * w;
    float fw = max(dist * gPix, 1e-5);
    float hw = max(rs, 0.5 * fw);
    float qh = hw * 1.15 + 0.5 * fw;
    float cy = corner.y * 2.0 - 1.0;
    rel += (InvView[0].xyz * corner.x + InvView[1].xyz * cy) * qh;

    float tw = step(0.9, float((h3 >> 16u) & 255u) * (1.0 / 255.0));
    float twinkle = 1.0 + tw * 2.2 * pow(0.5 + 0.5 * sin(TAU * (mA * tk) * 2.0 + ph * 40.0), 6.0);
    float a = min(1.0, rs / (0.5 * fw)) * occl * Color.a * 0.9 * ring
        * (1.0 - smoothstep(0.7 * WindRange.z, WindRange.z, dist)) * sheetMod(c);
    vUv = vec2(corner.x, cy) * (qh / hw);
    vAmp = 1.0;
    vY = 1e4;
    vCeil = -1e4;
    vData = vec4(1.0, a, twinkle, 0.2);
    emit(rel);
}

void sakuraPetal(int local, vec2 corner) {
    int n = int(Grid.x);
    int kN = int(Grid2.x);
    int col = local / kN;
    int k = local - col * kN;
    float cs = layerCs(0, 2.0);
    vec2 wind = WindRange.xy;
    float reach = WindRange.z;
    vec2 o2 = gCam.xz - wind * (gCam.y - gAnchor);
    ivec2 cell = columnCell(n, col, o2, cs);

    uint h1 = hash2(cell, 7u);
    uint h2 = pcg(h1 + 1u);
    vec2 jit = vec2(float(h2 & 1023u), float((h2 >> 10u) & 1023u)) * (1.0 / 1023.0);
    vec2 base = (vec2(cell) + 0.2 + 0.6 * jit) * cs;
    float spread = length(base - o2);
    if (spread > reach + cs) { cull(); return; }
    float spawn = spawnFade(h1, P0.w * 2.0 * mix(1.0, 0.25, smoothstep(8.0, reach, spread)));
    if (spawn <= 0.0) { cull(); return; }

    uint h3 = pcg(h2 + 1u);
    uint h4 = pcg(h3 + 1u);
    float ph = float((h2 >> 20u) & 2047u) * (1.0 / 2047.0);
    float vN = (90.0 + float(h3 % 110u)) * (1.0 / 1024.0);
    float mA = 260.0 + float((h3 >> 8u) % 380u);
    float mB = 260.0 + float((h3 >> 18u) % 380u);
    float mS = (300.0 + float(h4 % 600u)) * ((h4 & 1024u) != 0u ? 1.0 : -1.0);
    float tk = gTime * (1.0 / 1024.0);
    vec2 c = base + 0.45 * cs * vec2(sin(TAU * (mA * tk + ph)), cos(TAU * (mB * tk + ph * 3.7)));

    float head = H * fract(ph - vN * gTime);
    float fy = head + H * (floor((gCam.y - reach - head) / H) + float(k));
    vec2 pxz = c + wind * (fy - gAnchor);
    vec3 rel = vec3(pxz.x, fy, pxz.y) - gCam;
    float dist = length(rel);
    if (dist >= reach) { cull(); return; }
    float ceilY = surfaceAt(pxz, heightAt(pxz));
    float occl = smoothstep(ceilY - 0.01, ceilY + 0.06, fy);
    if (occl <= 0.0) { cull(); return; }

    float spin = TAU * (mS * tk + ph * 5.0);
    float tilt = 1.15 * sin(TAU * (mA * tk) + ph * 11.0);
    float roll = 0.7 * cos(TAU * (mB * tk) + ph * 5.0);
    float sy = sin(spin), cyw = cos(spin);
    float sx = sin(tilt), cx = cos(tilt);
    float sz = sin(roll), cz = cos(roll);
    mat3 ry = mat3(cyw, 0.0, -sy, 0.0, 1.0, 0.0, sy, 0.0, cyw);
    mat3 rx = mat3(1.0, 0.0, 0.0, 0.0, cx, sx, 0.0, -sx, cx);
    mat3 rz = mat3(cz, sz, 0.0, -sz, cz, 0.0, 0.0, 0.0, 1.0);
    mat3 rot = ry * rx * rz;
    vec3 e1 = rot[0];
    vec3 e2 = rot[2];
    vec3 nrm = rot[1];

    float hr = float((h2 >> 12u) & 255u) * (1.0 / 255.0);
    float len = (0.055 + 0.035 * hr) * P1.y;
    float fw = max(dist * gPix, 1e-5);
    float grow = max(1.0, 0.9 * fw / len);
    len *= grow;
    float cy = corner.y * 2.0 - 1.0;
    rel += (e1 * corner.x * 0.78 + e2 * cy) * len;

    float facing = abs(dot(nrm, rel / max(length(rel), 1e-4)));
    float shade = (0.78 + 0.3 * abs(nrm.y)) * (0.85 + 0.25 * facing);
    float a = occl * Color.a / (grow * grow) * smoothstep(0.4, 1.2, dist) * spawn
        * (1.0 - smoothstep(0.7 * WindRange.z, WindRange.z, dist)) * sheetMod(c);
    vUv = vec2(corner.x, cy);
    vAmp = 1.0;
    vY = 1e4;
    vCeil = -1e4;
    vData = vec4(2.0, a, shade, float((h4 >> 12u) & 255u) * (1.0 / 255.0));
    emit(rel);
}

void groundPetal(int local, vec2 corner) {
    int n = int(Grid2.w);
    int cellIdx = local >> 1;
    ivec2 cell = ivec2(floor(gCam.xz)) + ivec2(cellIdx % n - n / 2, cellIdx / n - n / 2);

    uint h1 = hash2(cell, 101u + uint(local & 1));
    float spawn = spawnFade(h1, Grid.w * P0.w * 1.2);
    if (spawn <= 0.0) { cull(); return; }
    uint h2 = pcg(h1 + 1u);
    uint h3 = pcg(h2 + 1u);
    vec2 jit = vec2(float(h2 & 1023u), float((h2 >> 10u) & 1023u)) * (1.0 / 1023.0);
    vec2 pxz = vec2(cell) + jit;
    float gr = float(n / 2);
    float hd = length(pxz - gCam.xz);
    if (hd > gr) { cull(); return; }
    vec2 g = heightAt(pxz);
    float gy = surfaceAt(pxz, g);
    if (gy < -400.0) { cull(); return; }

    float f = float(9u + h3 % 9u) * (1.0 / 1024.0);
    float life = fract(u01(h3) + f * gTime);
    float settle = clamp(life / f / 1.2, 0.0, 1.0);
    float fall = (1.0 - settle) * (1.0 - settle);
    float y = gy + 0.02 + fall * 0.6;

    float ang = TAU * u01(pcg(h3 + 1u));
    float tilt = fall * 1.1 + 0.1 * (u01(pcg(h3 + 2u)) - 0.5);
    if (isWater(g)) {
        float t = gTime + 64.0 * u01(pcg(h3 + 3u));
        pxz = clamp(pxz + settle * 0.18 * vec2(sin(t * 0.37), cos(t * 0.29)), vec2(cell) + 0.05, vec2(cell) + 0.95);
        y = gy + 0.008 + 0.012 * settle * sin(t * 1.6) + fall * 0.6;
        ang += settle * 0.4 * sin(t * 0.21);
        tilt += settle * 0.07 * sin(t * 1.3 + 1.0);
    }
    vec3 e1 = vec3(cos(ang), 0.0, sin(ang));
    vec3 e2 = vec3(-sin(ang), 0.0, cos(ang)) * cos(tilt) + vec3(0.0, sin(tilt), 0.0);

    vec3 rel = vec3(pxz.x, y, pxz.y) - gCam;
    float dist = length(rel);
    float hr = float((h2 >> 20u) & 255u) * (1.0 / 255.0);
    float len = (0.055 + 0.035 * hr) * P1.y;
    float fw = max(dist * gPix, 1e-5);
    float grow = max(1.0, 0.9 * fw / len);
    len *= grow;
    float cy = corner.y * 2.0 - 1.0;
    rel += (e1 * corner.x * 0.78 + e2 * cy) * len;

    float a = Color.a * spawn / (grow * grow)
        * smoothstep(0.0, 0.004, life) * (1.0 - smoothstep(0.8, 1.0, life))
        * (1.0 - smoothstep(0.7 * gr, gr, hd));
    vUv = vec2(corner.x, cy);
    vAmp = 1.0;
    vY = 1e4;
    vCeil = -1e4;
    vData = vec4(3.0, a, 0.8 + 0.15 * hr, float((h3 >> 12u) & 255u) * (1.0 / 255.0));
    emit(rel);
}

void main() {
    vUv = vec2(0.0);
    vAmp = 0.0;
    vDepth = 0.0;
    vY = 0.0;
    vCeil = 0.0;
    vData = vec4(0.0);

    gPix = P1.w;
    gAnchor = Gust.w;
    gTime = CamPos.w;
    gCam = CamPos.xyz;

    int q = gl_VertexID >> 2;
    vec2 corner = CORNERS[gl_VertexID & 3];
    int c0 = int(Grid.x * Grid.x * Grid2.x);
    int c1 = int(Grid.y * Grid.y * Grid2.y);
    int c2 = int(Grid.z * Grid.z * Grid2.z);
    bool snow = P0.z > 0.5;
    bool petals = P0.z > 1.5;
    int layer = q < c0 ? 0 : (q < c0 + c1 ? 1 : 2);
    int local = q - (layer == 0 ? 0 : (layer == 1 ? c0 : c0 + c1));

    if (q < c0 + c1 + c2) {
        if (petals) sakuraPetal(local, corner);
        else if (snow) snowFlake(layer, local, corner);
        else rainStreak(layer, local, corner);
    } else if (petals) {
        groundPetal(q - c0 - c1 - c2, corner);
    } else if (!snow) {
        rainBead(q - c0 - c1 - c2, corner);
    } else {
        cull();
    }
}
