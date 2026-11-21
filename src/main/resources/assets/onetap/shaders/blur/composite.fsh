#version 330

#moj_import <minecraft:globals.glsl>
#moj_import <onetap:round_rect.glsl>

uniform sampler2D Sampler0;

in vec4 vertexColor;
in vec2 localPos;
flat in vec2 halfSize;
flat in vec4 params;
flat in vec4 extra;

out vec4 fragColor;

#ifdef GLASS
vec3 glassColor(vec2 p, float dist, vec2 unitsPerPixel, out float light) {
    vec4 radii = vec4(params.x);
    float glass = params.y;
    float band = min(18.0, min(halfSize.x, halfSize.y));
    float edge = 1.0 - clamp(-dist / band, 0.0, 1.0);
    vec2 e = vec2(0.5, 0.0);
    vec2 n = vec2(sdRoundRect(p + e.xy, halfSize, radii) - sdRoundRect(p - e.xy, halfSize, radii),
                  sdRoundRect(p + e.yx, halfSize, radii) - sdRoundRect(p - e.yx, halfSize, radii));
    n = n / max(length(n), 1e-4);

    vec2 bend = -n * edge * edge * edge * band * 0.9 * glass / unitsPerPixel / ScreenSize;
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec3 col = vec3(
        texture(Sampler0, uv + bend * 1.08).r,
        texture(Sampler0, uv + bend).g,
        texture(Sampler0, uv + bend * 0.92).b);

    vec2 toMouse = p - extra.xy;
    float mouseSq = dot(toMouse, toMouse);
    float glow = exp(-mouseSq / (45.0 * 45.0));
    float halo = exp(-mouseSq / (80.0 * 80.0));
    float line = 1.0 - smoothstep(0.0, 1.2, -dist);
    float rim = pow(edge, 3.0) * 0.4 * glow;
    float spot = halo * 0.06;
    light = (line * (0.04 + 0.55 * glow) + rim + spot) * glass;
    return col;
}
#endif

void main() {
    vec2 unitsPerPixel = vec2(dFdx(localPos.x), dFdy(localPos.y));
#ifdef GLASS
    float dist = sdRoundRect(localPos, halfSize, params.x);
#else
    float dist = sdRoundRect(localPos, halfSize, vec4(params.xy, extra.xy));
#endif
    float aa = params.z > 0.0 ? params.z : fwidth(dist);
    float strength = clamp(params.w, 0.0, 1.0);
    float alpha = 1.0 - smoothstep(-aa, aa, dist);
    if (alpha < 0.01) discard;
    vec3 tint = mix(vec3(1.0), vertexColor.rgb, vertexColor.a * strength);
    float light = 0.0;
#ifdef GLASS
    vec3 base = params.y > 0.0 ? glassColor(localPos, dist, unitsPerPixel, light) : texture(Sampler0, gl_FragCoord.xy / ScreenSize).rgb;
#else
    vec3 base = texture(Sampler0, gl_FragCoord.xy / ScreenSize).rgb;
#endif
    float opacity = extra.z > 0.0 ? extra.z : 1.0;
    fragColor = vec4(base * tint + light, alpha * strength * opacity);
}
