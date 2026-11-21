#version 330

#moj_import <onetap:sky_params.glsl>
#moj_import <onetap:sky_lib.glsl>

in vec3 vDir;
out vec4 fragColor;

mat2 mm2(float a) {
    float c = cos(a), s = sin(a);
    return mat2(c, s, -s, c);
}

const mat2 M2 = mat2(0.95534, 0.29552, -0.29552, 0.95534);

float tri(float x) {
    return clamp(abs(fract(x) - 0.5), 0.01, 0.49);
}

vec2 tri2(vec2 p) {
    return vec2(tri(p.x) + tri(p.y), tri(p.y + tri(p.x)));
}

float triNoise2d(vec2 p, mat2 rot) {
    float z = 1.8;
    float z2 = 2.5;
    float rz = 0.0;
    p *= mm2(p.x * 0.06);
    vec2 bp = p;
    for (int i = 0; i < 3; i++) {
        vec2 dg = tri2(bp * 1.85) * 0.75;
        dg *= rot;
        p -= dg / z2;
        bp *= 1.3;
        z2 *= 0.45;
        z *= 0.42;
        p *= 1.21 + (rz - 1.0) * 0.02;
        rz += tri(p.x + tri(p.y)) * z;
        p *= -M2;
    }
    return clamp(1.0 / pow(max(rz * 29.0, 1e-4), 1.3), 0.0, 0.55);
}

vec4 auroraVol(vec3 ro, vec3 rd, mat2 rot, vec3 colB, vec3 colC, float sc) {
    vec4 col = vec4(0.0);
    vec4 avgCol = vec4(0.0);
    float dither = hash12(gl_FragCoord.xy);
    for (int i = 0; i < 8; i++) {
        float fi = float(i);
        float of = 0.012 * dither * smoothstep(0.0, 4.0, fi);
        float pt = ((0.8 + pow(fi, 1.35) * 0.012) - ro.y) / (rd.y * 2.0 + 0.4) - of;
        vec3 bpos = ro + pt * rd;
        float rzt = triNoise2d(bpos.zx * (0.85 / sc), rot);
        vec3 spectral = sin(1.0 - vec3(2.15, -0.5, 1.2) + fi * 0.16) * 0.5 + 0.5;
        vec3 themeCol = mix(colB, colC, fi * (1.0 / 7.0));
        vec4 col2 = vec4(mix(spectral, themeCol, 0.5) * rzt, rzt);
        avgCol = mix(avgCol, col2, 0.5);
        col += avgCol * exp2(-fi * 0.20 - 2.2) * smoothstep(0.0, 2.0, fi);
    }
    col *= clamp(rd.y * 15.0 + 0.4, 0.0, 1.0);
    return col * 3.4;
}

vec4 auroraReflect(vec3 ro, vec3 rd, mat2 rot, vec3 colB, vec3 colC, float sc) {
    vec4 col = vec4(0.0);
    vec4 avgCol = vec4(0.0);
    for (int i = 0; i < 4; i++) {
        float fi = float(i);
        float pt = ((0.82 + fi * 0.03) - ro.y) / (rd.y * 2.0 + 0.4);
        vec3 bpos = ro + pt * rd;
        float rzt = triNoise2d(bpos.zx * (0.85 / sc), rot);
        vec3 spectral = sin(1.0 - vec3(2.15, -0.5, 1.2) + fi * 0.3) * 0.5 + 0.5;
        vec4 col2 = vec4(mix(spectral, mix(colB, colC, fi * 0.25), 0.5) * rzt, rzt);
        avgCol = mix(avgCol, col2, 0.5);
        col += avgCol * exp2(-fi * 0.4 - 2.2);
    }
    col *= clamp(rd.y * 15.0 + 0.4, 0.0, 1.0);
    return col * 3.4;
}

vec3 bgSky(vec3 rd, vec3 a, vec3 b) {
    float sd = pow(dot(normalize(vec3(-0.45, -0.55, 0.7)), rd) * 0.5 + 0.5, 4.0);
    vec3 col = mix(vec3(0.015, 0.035, 0.08), vec3(0.045, 0.02, 0.10), sd);
    col = mix(col, a * 0.15, 0.4);
    col += mix(vec3(0.04, 0.12, 0.22), b * 0.35, 0.5) * exp(-pow(rd.y * 3.5, 2.0)) * 0.4;
    return col;
}

vec3 renderAurora(vec3 dir) {
    float t = Params.x;
    float sc = clamp(Params.z, 0.25, 3.5);
    vec3 ndir = normalize(dir);
    vec3 a = themeA();
    vec3 b = themeB();
    vec3 c = themeC();
    vec3 ro = vec3(0.0);
    float fade = smoothstep(0.0, 0.015, abs(ndir.y)) * 0.1 + 0.9;
    mat2 rot = mm2(t * 0.04);
    vec3 col;

    if (ndir.y >= 0.0) {
        col = bgSky(ndir, a, b) * fade;
        vec4 aur = smoothstep(0.0, 1.5, auroraVol(ro, ndir, rot, b, c, sc)) * fade;
        if (aur.a < 0.92) col += starLayer(ndir, 150.0, 2100.0, t, c) * 0.7;
        col = col * (1.0 - aur.a) + aur.rgb;
    } else {
        vec3 rDir = vec3(ndir.x, abs(ndir.y), ndir.z);
        col = bgSky(rDir, a, b) * fade * 0.5;
        vec4 aur = smoothstep(0.0, 2.2, auroraReflect(ro, rDir, rot, b, c, sc));
        col += starLayer(rDir, 90.0, 1600.0, t, c) * 0.12;
        col = col * (1.0 - aur.a) + aur.rgb * 0.7;
        float ice = 1.0 - smoothstep(0.0, 0.8, abs(ndir.y));
        float n = triNoise2d(ndir.xz / (abs(ndir.y) + 0.15) * 0.55, rot);
        col += mix(vec3(0.03, 0.09, 0.18), vec3(0.12, 0.32, 0.48), n * 0.7) * ice * 0.4;
    }

    vec3 dustCoord = ndir * (70.0 / sc) + vec3(sin(t * 0.15) * 0.3, t * 0.25, cos(t * 0.15) * 0.3);
    float dustRnd = hash13(floor(dustCoord));
    if (dustRnd > 0.985) {
        vec3 local = fract(dustCoord) - 0.5;
        float sparkle = exp(-dot(local, local) * 1400.0) * (0.6 + 0.4 * sin(t * 3.0 + dustRnd * 100.0));
        col += mix(vec3(0.95, 0.98, 1.0), c, 0.3) * sparkle * 1.8;
    }

    return tonemap(col * Params.w * 1.15);
}

void main() {
    fragColor = vec4(renderAurora(vDir), 1.0);
}
