#version 330

#moj_import <onetap:sky_params.glsl>

in vec3 Position;
in vec2 UV0;

out vec3 vDir;
#ifdef SKY_UV
out vec2 texCoord;
#endif

void main() {
    vec4 far = InvProj * vec4(Position.xy, 1.0, 1.0);
    far /= far.w;
    vDir = (CamRot * vec4(far.xyz, 0.0)).xyz;
#ifdef SKY_UV
    texCoord = Position.xy * 0.5 + 0.5;
#endif
    gl_Position = vec4(Position.xy, 0.0, 1.0);
}
