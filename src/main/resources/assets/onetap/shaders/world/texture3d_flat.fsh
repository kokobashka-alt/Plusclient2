#version 330

layout(std140) uniform TextureParams {
    mat4 Matrix;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    float coverage = texture(Sampler0, texCoord0).a * vertexColor.a;
    if (coverage < 0.1) discard;
    fragColor = vec4(vertexColor.rgb, coverage);
}
