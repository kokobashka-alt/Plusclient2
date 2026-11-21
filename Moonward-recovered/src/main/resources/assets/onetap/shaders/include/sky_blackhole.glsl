#version 330

const float R_MARCH = 10.5;

vec3 holeDir() {
    return normalize(vec3(0.42, 0.3, -1.0));
}

float holeDist() {
    return 20.0 / clamp(Params.z, 0.5, 1.4);
}
