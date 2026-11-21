#version 330

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    float d = abs(texCoord0.y);
    float coreEdge = texCoord0.x;
    float aa = max(fwidth(d), 1.0e-4);

    float core = 1.0 - smoothstep(coreEdge - aa, coreEdge + aa, d);
    float hot = 1.0 - smoothstep(0.0, coreEdge * 2.4, d);
    float glow = pow(clamp(1.0 - d, 0.0, 1.0), 2.4) * 0.5;

    vec3 rgb = mix(vertexColor.rgb, vec3(1.0), hot);
    float alpha = max(core, glow) * vertexColor.a;
    if (alpha < 0.004) discard;
    fragColor = vec4(rgb, alpha);
}
