#version 330

#moj_import <onetap:weather_common.glsl>

uniform sampler2D Sampler1;

in vec2 vUv;
in float vAmp;
in float vDepth;
in float vY;
flat in vec4 vData;
flat in float vCeil;

out vec4 fragColor;

void main() {
    float sceneDepth = -viewZ(texelFetch(Sampler1, ivec2(gl_FragCoord.xy), 0).r);
    float vis = vData.x > 2.5 ? clamp((sceneDepth - vDepth) * 60.0 + 1.0, 0.0, 1.0) : clamp((sceneDepth - vDepth) * 8.0, 0.0, 1.0);
    if (vis <= 0.0) discard;

    float a;
    vec3 col;
    if (vData.x < 0.5) {
        float f = vUv.y;
        float shape = pow(1.0 - f, 1.35) * smoothstep(0.0, 0.035, f);
        float cov = 1.0 - smoothstep(0.35, 1.2, abs(vUv.x));
        float occl = smoothstep(vCeil - 0.01, vCeil + 0.06, vY);
        a = cov * vAmp * shape * occl * vData.y;
        col = Color.rgb * (0.75 + 0.7 * shape) * vData.z;
    } else if (vData.x > 1.5) {
        vec2 p = vUv * 1.06;
        float t = p.y * 0.5 + 0.5;
        float d = length(vec2(p.x / (0.42 + 0.58 * t), p.y * 0.95 + 0.02));
        float notch = length(vec2(p.x * 1.3, p.y - 1.08));
        float aa = fwidth(d) + 0.02;
        a = (1.0 - smoothstep(1.0 - aa, 1.0, d)) * smoothstep(0.28 - aa, 0.28 + aa, notch) * vData.y;
        vec3 deep = mix(vec3(0.95, 0.52, 0.68), vec3(0.98, 0.66, 0.80), vData.w);
        col = mix(deep, vec3(1.0, 0.89, 0.93), smoothstep(0.0, 0.65, t));
        col *= 1.0 - 0.1 * exp(-p.x * p.x * 50.0) * (1.0 - t);
        col *= Color.rgb * vData.z;
    } else {
        float cov = 1.0 - smoothstep(vData.w, 1.0, length(vUv));
        a = cov * vData.y;
        col = Color.rgb * vData.z;
    }
    a = min(a * vis, 1.0);
    if (a < 0.002) discard;
    fragColor = vec4(col * a, a);
}
