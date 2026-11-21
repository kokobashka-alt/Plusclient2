#version 330

#moj_import <onetap:round_rect.glsl>

in vec4 vertexColor;
in vec2 localPos;
flat in vec2 halfSize;
flat in vec4 params;
flat in vec4 extra;

out vec4 fragColor;

vec3 hue(float value) {
    return clamp(abs(mod(value * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
}

void main() {
    float dist = sdRoundRect(localPos, halfSize, vec4(params.xy, extra.xy));
    if (params.w > 0.0) dist = abs(dist) - params.w;
    float aa = params.z > 0.0 ? params.z : fwidth(dist);
    vec4 color = vertexColor;
#ifdef HUE
    color.rgb = hue(localPos.x / (halfSize.x * 2.0) + 0.5);
#endif
    float alpha = color.a * (1.0 - smoothstep(-aa, aa, dist));
    if (alpha <= 0.0) discard;
    fragColor = vec4(color.rgb * alpha, alpha);
}
