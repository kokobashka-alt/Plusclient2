#version 330

layout(std140) uniform BoxParams {
    mat4 MVP;
};

in vec3 Position;
in vec4 Color;

out vec4 vertexColor;

void main() {
    gl_Position = MVP * vec4(Position, 1.0);
    vertexColor = Color;
}