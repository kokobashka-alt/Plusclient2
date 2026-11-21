#version 330

#moj_import <onetap:sky_params.glsl>
#moj_import <onetap:sky_lib.glsl>
#moj_import <onetap:sky_simplex.glsl>
#moj_import <onetap:sky_tokyo.glsl>

in vec3 vDir;
out vec4 fragColor;

#ifdef SKY_FIELD
void main() {
    fragColor = vec4(cloudNoise(normalize(vDir)), 0.0, 0.0);
}
#else
in vec2 texCoord;

uniform sampler2D Sampler0;

void main() {
    vec3 d = normalize(vDir);
    gPx = max(length(fwidth(d)), 1e-5) * 0.7;
    fragColor = vec4(renderTokyo(d, textureBicubic(Sampler0, texCoord).xy), 1.0);
}
#endif
