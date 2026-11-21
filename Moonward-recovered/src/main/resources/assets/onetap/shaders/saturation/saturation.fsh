#version 330

layout(std140) uniform SatParams {
    vec4 Params;
};

uniform sampler2D Sampler0;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 rgb = texture(Sampler0, texCoord).rgb;
    float luma = dot(rgb, vec3(0.2126, 0.7152, 0.0722));
    vec3 sat = mix(vec3(luma), rgb, Params.x);
    vec3 con = mix(vec3(0.5), sat, Params.y);
    fragColor = vec4(con, 1.0);
}
