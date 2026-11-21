#version 330

float sdRoundRect(vec2 p, vec2 b, float r) {
    vec2 d = abs(p) - b + vec2(r);
    return min(max(d.x, d.y), 0.0) + length(max(d, vec2(0.0))) - r;
}

float sdRoundRect(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x < 0.0) ? r.xw : r.yz;
    r.x  = (p.y < 0.0) ? r.x  : r.y;
    vec2 d = abs(p) - b + r.x;
    return min(max(d.x, d.y), 0.0) + length(max(d, vec2(0.0))) - r.x;
}