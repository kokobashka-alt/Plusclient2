#version 330

layout(std140) uniform SkyParams {
    mat4 InvProj;
    mat4 CamRot;
    vec4 Params;
    vec4 ColorA;
    vec4 ColorB;
    vec4 ColorC;
    vec4 Extra;
};
