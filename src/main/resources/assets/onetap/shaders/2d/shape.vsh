#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV1;
#ifdef EFFECT
in vec4 UV2;
#else
in vec2 UV2;
#endif
in vec2 LineWidth;

out vec4 vertexColor;
out vec2 localPos;
flat out vec2 halfSize;
flat out vec4 params;
flat out vec4 extra;
#ifdef EFFECT
flat out float phase;
#endif

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position.xy, 0.0, 1.0);
    vertexColor = Color;
    localPos = UV0;
    params = vec4(UV1, LineWidth);
#ifdef EFFECT
    extra = UV2;
    phase = Position.z;
#else
    extra = vec4(UV2, 0.0, 0.0);
#endif
#ifdef BLUR
    extra.z = Position.z;
#endif
#if defined(RING)
    float expand = UV1.y + UV1.x * 0.5;
#elif defined(FLOW)
    float expand = UV1.y;
#elif defined(BLUR)
    float expand = max(LineWidth.x, 1.0);
#else
    float expand = LineWidth.x + LineWidth.y;
#endif
    halfSize = abs(UV0) - vec2(expand);
}
