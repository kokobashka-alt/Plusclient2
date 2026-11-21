#version 330

#moj_import <onetap:sky_params.glsl>
#moj_import <onetap:sky_lib.glsl>
#moj_import <onetap:sky_simplex.glsl>
#moj_import <onetap:sky_tokyo.glsl>

uniform sampler2D Sampler0;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    if (Params.y == 5.0) {
        fragColor = texture(Sampler0, texCoord);
        vec4 far = InvProj * vec4(texCoord * 2.0 - 1.0, 1.0, 1.0);
        vec3 d = normalize((CamRot * vec4(far.xyz / far.w, 0.0)).xyz);
        gPx = max(length(fwidth(d)), 1e-5) * 0.7;
        float phase = Extra.x;
        wMorning = 1.0 - clamp(abs(phase), 0.0, 1.0);
        wDay = 1.0 - clamp(abs(phase - 1.0), 0.0, 1.0);
        wNight = 1.0 - clamp(abs(phase - 2.0), 0.0, 1.0);
        float proximity = dot(d, lightDir());
        if (proximity > cos(0.07)) {
            float blend = smoothstep(cos(0.07), cos(0.055), proximity);
            fragColor = mix(fragColor, vec4(renderTokyo(d, cloudNoise(d)), 1.0), blend);
        }
        return;
    }
    vec2 ts = vec2(textureSize(Sampler0, 0));
    vec2 texel = texCoord * ts;
    vec2 floored = floor(texel);
    vec2 s = fract(texel);
    vec2 center = s - 0.5;
    vec2 f = (center - clamp(center, vec2(-0.25), vec2(0.25))) * 2.0 + 0.5;
    fragColor = texture(Sampler0, (floored + f) / ts);
}
