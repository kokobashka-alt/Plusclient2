#version 330

layout(std140) uniform TextureParams {
    mat4 Matrix;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    fragColor = texture(Sampler0, texCoord0) * vertexColor;
    if (fragColor.a < 0.01) discard;
}