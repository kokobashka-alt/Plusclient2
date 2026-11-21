#version 330

#moj_import <onetap:round_rect.glsl>

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;
in vec2 localPos;
flat in vec2 halfSize;
flat in vec2 radiusSmooth;

out vec4 fragColor;

void main() {
    vec2 texSize = vec2(textureSize(Sampler0, 0));
    vec2 uvPerX = dFdx(texCoord0) / dFdx(localPos.x);
    vec2 uvPerY = dFdy(texCoord0) / dFdy(localPos.y);
    vec2 uvCenter = texCoord0 - uvPerX * localPos.x - uvPerY * localPos.y;
    vec2 uvExtent = abs(uvPerX) * halfSize.x + abs(uvPerY) * halfSize.y;
    vec2 uv = texCoord0;
#ifdef SEAM
    vec2 pix = uv * texSize;
    vec2 seam = floor(pix + 0.5);
    vec2 texelsPerPixel = max(fwidth(pix), vec2(1e-4));
    uv = (seam + clamp((pix - seam) / texelsPerPixel, -0.5, 0.5)) / texSize;
#endif
    vec2 halfTexel = 0.5 / texSize;
    vec4 texColor = texture(Sampler0, clamp(uv, uvCenter - uvExtent + halfTexel, uvCenter + uvExtent - halfTexel));

    float dist = sdRoundRect(localPos, halfSize, radiusSmooth.x);
    float aa = radiusSmooth.y > 0.0 ? radiusSmooth.y : fwidth(dist);
    float alpha = 1.0 - smoothstep(-aa, aa, dist);
    vec4 color = texColor * vertexColor;
#ifdef PREMULTIPLIED
    if (texColor.a < 0.05) discard;
    fragColor = vec4(color.rgb * vertexColor.a * alpha, color.a * alpha);
#else
    float finalAlpha = color.a * alpha;
    fragColor = vec4(color.rgb * finalAlpha, finalAlpha);
#endif
}
