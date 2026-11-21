#version 330

#moj_import <onetap:sky_params.glsl>
#moj_import <onetap:sky_lib.glsl>
#moj_import <onetap:sky_simplex.glsl>

in vec3 vDir;
out vec4 fragColor;

#ifdef SKY_FIELD
void main() {
    vec3 dir = normalize(vDir);
    float t = Params.x * 3.0;
    float sc = max(Params.z, 0.001);
    vec3 np = warp(dir * (3.0 / sc) + vec3(t * 0.015, 0.0, t * 0.01));
    float dust = fbm3(dir * (4.0 / sc) + vec3(0.0, t * 0.008, 0.0));
    fragColor = vec4(fbm3(np), fbm3(np * 1.9 + vec3(13.0, 5.0, 7.0)), dust, fbm3(np * 0.6));
}
#else
in vec2 texCoord;

uniform sampler2D Sampler0;

vec3 renderSpace(vec3 dir) {
    float t = Params.x * 3.0;
    vec3 secondary = themeB();
    vec3 accent = themeC();
    float sc = max(Params.z, 0.001);
    vec4 field = textureBicubic(Sampler0, texCoord);

    float n1 = field.x * 0.5 + 0.5;
    float ridges = 1.0 - abs(field.y);
    ridges *= ridges;

    float mask = smoothstep(0.35, 0.9, n1);
    vec3 nebColor = mix(vec3(0.0), secondary * 0.85, mask);
    nebColor = mix(nebColor, accent * 1.15, smoothstep(0.6, 0.98, n1) * 0.85);
    nebColor += accent * pow(ridges, 2.5) * 0.6 * mask;

    float galactic = exp(-pow(dir.y * 2.4, 2.0));
    float dust = field.z * 0.5 + 0.5;
    float core = pow(1.0 - abs(field.w), 3.0);
    vec3 galCol = mix(secondary * 0.55, accent * 0.85, smoothstep(0.4, 0.8, core));
    galCol += accent * core * 0.4;
    nebColor += galCol * galactic * (0.4 + dust * 0.7);

    vec3 stars = starLayer(dir, 180.0 / sc, 1400.0, t, accent);
    stars += starLayer(dir, 340.0 / sc, 2600.0, t, secondary) * 0.65;
    stars += starLayer(dir, 520.0 / sc, 5200.0, t, accent) * 0.45;

    float horizon = 0.72 + smoothstep(-0.3, 0.9, dir.y) * 0.28;
    vec3 color = nebColor * horizon + stars;
    color *= Params.w;
    color = color / (1.0 + color * 0.7);
    return pow(color, vec3(0.90));
}

void main() {
    fragColor = vec4(renderSpace(normalize(vDir)), 1.0);
}
#endif
