#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV1;
in vec2 UV2;

out vec2 texCoord0;
out vec4 vertexColor;
out vec2 localPos;
flat out vec2 halfSize;
flat out vec2 radiusSmooth;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord0 = UV0;
    vertexColor = Color;
    localPos = UV1;
    radiusSmooth = UV2;
    halfSize = abs(UV1) - vec2(UV2.y);
}
