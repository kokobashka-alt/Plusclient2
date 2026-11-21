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

in vec4 v_Color;
in vec2 v_TexCoord;
in vec2 v_FragDistance;
in float v_Fade;
in vec3 v_RegionLocal;
in float v_FlickPhase;

uniform sampler2D u_BlockTex;

out vec4 fragColor;

vec4 sampleNearest(sampler2D source, vec2 uv, vec2 pixelSize, vec2 du, vec2 dv, vec2 texelScreenSize) {
    // Convert our UV back up to texel coordinates and find out how far over we are from the center of each pixel
    vec2 uvTexelCoords = uv / pixelSize;
    vec2 texelCenter = round(uvTexelCoords) - 0.5f;
    vec2 texelOffset = uvTexelCoords - texelCenter;

    // Move our offset closer to the texel center based on texel size on screen
    texelOffset = (texelOffset - 0.5f) * pixelSize / texelScreenSize + 0.5f;
    texelOffset = clamp(texelOffset, 0.0f, 1.0f);

    uv = (texelCenter + texelOffset) * pixelSize;
    return textureGrad(source, uv, du, dv);
}

vec4 sampleNearest(sampler2D source, vec2 uv, vec2 pixelSize) {
    vec2 du = dFdx(uv);
    vec2 dv = dFdy(uv);
    vec2 texelScreenSize = sqrt(du * du + dv * dv);
    return sampleNearest(source, uv, pixelSize, du, dv, texelScreenSize);
}

// Rotated Grid Super-Sampling
vec4 sampleRGSS(sampler2D source, vec2 uv, vec2 pixelSize) {
    vec2 du = dFdx(uv);
    vec2 dv = dFdy(uv);

    vec2 texelScreenSize = sqrt(du * du + dv * dv);
    float maxTexelSize = max(texelScreenSize.x, texelScreenSize.y);

    float minPixelSize = min(pixelSize.x, pixelSize.y);

    float transitionStart = minPixelSize * 1.0;
    float transitionEnd = minPixelSize * 2.0;
    float blendFactor = smoothstep(transitionStart, transitionEnd, maxTexelSize);

    float duLength = length(du);
    float dvLength = length(dv);
    float minDerivative = min(duLength, dvLength);
    float maxDerivative = max(duLength, dvLength);

    float effectiveDerivative = sqrt(minDerivative * maxDerivative);

    float mipLevelExact = max(0.0, log2(effectiveDerivative / minPixelSize));

    const vec2 offsets[4] = vec2[](
    vec2(0.125, 0.375),
    vec2(-0.125, -0.375),
    vec2(0.375, -0.125),
    vec2(-0.375, 0.125)
    );

    vec4 rgssColor = vec4(0.0);
    for (int i = 0; i < 4; ++i) {
        vec2 sampleUV = uv + offsets[i] * pixelSize;
        rgssColor += textureLod(source, sampleUV, mipLevelExact);
    }
    rgssColor *= 0.25;

    vec4 nearestColor = sampleNearest(source, uv, pixelSize, du, dv, texelScreenSize);

    return mix(nearestColor, rgssColor, blendFactor);
}

float linear_fog_value(float vertexDistance, float fogStart, float fogEnd) {
    if (vertexDistance <= fogStart) {
        return 0.0;
    } else if (vertexDistance >= fogEnd) {
        return 1.0;
    }

    return (vertexDistance - fogStart) / (fogEnd - fogStart);
}

float total_fog_value(float sphericalVertexDistance, float cylindricalVertexDistance, float environmentalStart, float environmentalEnd, float renderDistanceStart, float renderDistanceEnd) {
    return max(linear_fog_value(sphericalVertexDistance, environmentalStart, environmentalEnd), linear_fog_value(cylindricalVertexDistance, renderDistanceStart, renderDistanceEnd));
}

vec4 _linearFog(vec4 fragColor, vec2 fragDistance, vec4 fogColor, vec2 environmentFog, vec2 renderFog, float fadeFactor) {
    float fogValue = max(1.0 - fadeFactor, total_fog_value(fragDistance.y, fragDistance.x, environmentFog.x, environmentFog.y, renderFog.x, renderFog.y));
    return vec4(mix(fragColor.rgb, fogColor.rgb, fogValue * fogColor.a), fragColor.a);
}

float animHash(vec3 p) {
    p = fract(p * vec3(443.897, 441.423, 437.195));
    p += dot(p, p.yzx + 19.19);
    return fract((p.x + p.y) * p.z);
}

vec3 onetap_particleColor(vec3 blockColor, float spark) {
#ifdef ONETAP_COLOR_BLOCK
    return blockColor * 0.35 + blockColor * spark * 1.6;
#elif defined(ONETAP_COLOR_CUSTOM)
    return vec3(ONETAP_CR, ONETAP_CG, ONETAP_CB) / 255.0 * (0.35 + 0.65 * spark);
#else
    return blockColor * 0.35 + vec3(1.0, 0.97, 0.88) * spark;
#endif
}

void main() {
    vec4 color = u_UseRGSS ? sampleRGSS(u_BlockTex, v_TexCoord, u_TexelSize) : sampleNearest(u_BlockTex, v_TexCoord, u_TexelSize);
    color *= v_Color;

#ifdef ONETAP_PARTICLES
    float v = clamp(v_Fade, 0.0, 1.0);
    if (v < 1.0) {
        float localY = mod(v_RegionLocal.y, 16.0);
        float front = v * 20.0 - 2.0;
        float d = localY - front;
        vec3 cell = floor(v_RegionLocal * 8.0);
        float flick = animHash(cell + v_FlickPhase * 0.37);
        if (d > 0.0) {
            float density = 0.32 * exp(-d * 0.45);
            if (flick < 1.0 - density) discard;
            float heightFade = clamp(1.0 - d / 10.0, 0.0, 1.0);
            float spark = 0.55 + 0.45 * animHash(cell + 7.7);
            color = vec4(onetap_particleColor(color.rgb, spark), heightFade);
        } else {
            float flash = exp(d * 1.4);
            float glitter = step(0.965, flick) * exp(d * 0.9);
#ifdef ONETAP_COLOR_BLOCK
            color.rgb += color.rgb * (flash * 0.55 + glitter * 0.5);
#elif defined(ONETAP_COLOR_CUSTOM)
            color.rgb += vec3(ONETAP_CR, ONETAP_CG, ONETAP_CB) / 255.0 * (flash * 0.55 + glitter * 0.5);
#else
            color.rgb += vec3(1.0, 0.97, 0.9) * (flash * 0.55 + glitter * 0.5);
#endif
        }
    }
#endif

#ifdef ALPHA_CUTOUT
    if (color.a < ALPHA_CUTOUT) {
        discard;
    }
#endif

    fragColor = _linearFog(color, v_FragDistance, u_FogColor, u_EnvironmentFog, u_RenderFog, v_Fade);
}
