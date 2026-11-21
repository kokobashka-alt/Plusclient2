#version 330

#moj_import <onetap:round_rect.glsl>

in vec4 vertexColor;
in vec2 localPos;
flat in vec2 halfSize;
flat in vec4 params;
flat in vec4 extra;
flat in float phase;

out vec4 fragColor;

void main() {
#ifdef RING
    float angle = atan(localPos.y, localPos.x);
    float dist = abs(length(localPos) - halfSize.x) - params.x * 0.5;
    float aa = params.y > 0.0 ? params.y : fwidth(dist);
    float alpha = 1.0 - smoothstep(-aa, aa, dist);
    if (mod(angle - params.w, 6.2831853) / 6.2831853 > clamp(params.z, 0.0, 1.0)) alpha = 0.0;
    vec4 color = vertexColor;
#ifdef GRADIENT
    color = mix(vertexColor, extra, sin(phase + angle) * 0.5 + 0.5);
#endif
#else
    float dist = sdRoundRect(localPos, halfSize, params.x);
    float aa = params.y > 0.0 ? params.y : fwidth(dist);
    float alpha = 1.0 - smoothstep(-aa, aa, dist);
    float axis = params.z > 0.5
        ? clamp(localPos.y / max(halfSize.y * 2.0, 1e-4) + 0.5, 0.0, 1.0)
        : clamp(localPos.x / max(halfSize.x * 2.0, 1e-4) + 0.5, 0.0, 1.0);
    vec4 color = mix(vertexColor, extra, sin(phase - axis * 6.2831853) * 0.5 + 0.5);
#endif
    alpha *= color.a;
    if (alpha <= 0.0) discard;
    fragColor = vec4(color.rgb * alpha, alpha);
}
