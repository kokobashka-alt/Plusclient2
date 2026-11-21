#version 330

#moj_import <onetap:sky_params.glsl>
#moj_import <onetap:sky_blackhole.glsl>

in vec3 Position;
in vec2 UV0;

out vec3 vDir;

void main() {
    vec3 h = holeDir();
    float d = holeDist();
    vec3 side = normalize(cross(h, vec3(0.0, 1.0, 0.0)));
    vec3 up = cross(side, h);
    float reach = 2.2 * R_MARCH / sqrt(d * d - R_MARCH * R_MARCH);
    float a = float(gl_VertexID) * 2.0943951;
    vDir = h + (side * cos(a) + up * sin(a)) * reach;
    gl_Position = inverse(InvProj) * vec4(transpose(mat3(CamRot)) * vDir, 1.0);
}
