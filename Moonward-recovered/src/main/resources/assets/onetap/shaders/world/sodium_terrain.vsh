#version 330 core

layout(std140) uniform u_Globals {
    mat4 u_ProjectionMatrix;
    mat4 u_ModelViewMatrix;

    vec4 u_FogColor;
    vec2 u_EnvironmentFog;
    vec2 u_RenderFog;

    vec2 u_TexelSize;
    vec2 u_TexCoordShrink;

    float u_FadePeriodInv;
    bool u_UseRGSS;
};

out vec4 v_Color;
out vec2 v_TexCoord;
out vec2 v_FragDistance;
out float v_Fade;
out vec3 v_RegionLocal;
out float v_FlickPhase;

uniform isamplerBuffer u_SectionTimeInfo;

#ifdef VULKAN
layout(push_constant) uniform PC {
    vec3 u_RegionOffset;
    int u_CurrentTime;
    uint u_RegionID;
};
#else
uniform vec3 u_RegionOffset;
uniform int u_CurrentTime;
uniform uint u_RegionID;
#endif

uniform sampler2D u_LightTex;

in uvec2 a_Position;
in vec4 a_Color;
in uvec2 a_TexCoord;
in uvec4 a_LightAndData;

const uint POSITION_BITS = 20u;
const uint POSITION_MAX_COORD = 1u << POSITION_BITS;

const uint TEXTURE_BITS = 15u;
const uint TEXTURE_MAX_COORD = 1u << TEXTURE_BITS;
const uint TEXTURE_MAX_VALUE = TEXTURE_MAX_COORD - 1u;

const float VERTEX_SCALE = 32.0 / float(POSITION_MAX_COORD);
const float VERTEX_OFFSET = -8.0;

uvec3 _deinterleave_u20x3(uvec2 data) {
    uvec3 hi = (uvec3(data.x) >> uvec3(0u, 10u, 20u)) & 0x3FFu;
    uvec3 lo = (uvec3(data.y) >> uvec3(0u, 10u, 20u)) & 0x3FFu;

    return (hi << 10u) | lo;
}

uvec3 _get_relative_chunk_coord(uint pos) {
    return uvec3(pos) >> uvec3(5u, 0u, 2u) & uvec3(7u, 3u, 7u);
}

vec3 _get_draw_translation(uint pos) {
    return _get_relative_chunk_coord(pos) * vec3(16.0);
}

void main() {
    vec3 _vert_position = (_deinterleave_u20x3(a_Position) * VERTEX_SCALE) + VERTEX_OFFSET;
    vec4 _vert_color = a_Color;
    vec2 _vert_tex_diffuse_coord = vec2(a_TexCoord & TEXTURE_MAX_VALUE) / float(TEXTURE_MAX_COORD);
    vec2 _vert_tex_diffuse_coord_bias = mix(vec2(-1.0), vec2(1.0), bvec2(a_TexCoord >> TEXTURE_BITS));
    vec2 _vert_tex_light_coord = vec2(a_LightAndData.xy) / vec2(256.0);
    uint _draw_id = a_LightAndData[3];

    vec3 drawTranslation = _get_draw_translation(_draw_id);
    vec3 position = _vert_position + u_RegionOffset + drawTranslation;

    int chunkFade = texelFetch(u_SectionTimeInfo, int((u_RegionID * 256u) + _draw_id)).r;
    float fade = clamp(float(u_CurrentTime - chunkFade) * u_FadePeriodInv, 0.0, 1.0);
    float f = (chunkFade < 0) ? 1.0 : fade;
    v_Fade = f;
    v_RegionLocal = _vert_position + drawTranslation;
    v_FlickPhase = floor(float(u_CurrentTime) * 0.05);

#ifdef ONETAP_LIFT
    position.y -= (1.0 - f) * (1.0 - f) * (1.0 - f) * 12.0;
#endif

    v_FragDistance = vec2(max(length(position.xz), abs(position.y)), length(position));

    gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * vec4(position, 1.0);

    v_Color = _vert_color * texture(u_LightTex, _vert_tex_light_coord);
    v_TexCoord = (_vert_tex_diffuse_coord_bias * u_TexCoordShrink) + _vert_tex_diffuse_coord;
}
