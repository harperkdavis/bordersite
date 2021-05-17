#version 460 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec3 normal;

out vec2 vertexUV;
out vec3 vertexNormal;
out vec3 vertexPos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {

    vec4 mvPos = model * vec4(position, 1.0);
    vertexPos = mvPos.xyz;
    vertexUV = uv;

    mat3 normalMatrix = transpose(inverse(mat3(model)));
    vertexNormal = normalMatrix * normal;

    gl_Position = projection * view * mvPos;
}