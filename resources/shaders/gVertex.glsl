#version 460 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec3 normal;
layout(location = 3) in vec3 tangent;
layout(location = 4) in vec3 bitangent;

out vec2 vertexUV;
out vec3 vertexPos;
out vec3 vertexViewPos;

out vec3 vertexNormal;
out vec3 vertexTangent;
out vec3 vertexBitangent;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {

    vec4 mvPos = model * vec4(position, 1.0);
    vertexViewPos = (model * view * vec4(position, 1.0)).xyz;
    vertexPos = mvPos.xyz;

    vertexUV = uv;

    mat3 normalMatrix = transpose(inverse(mat3(model)));
    vertexNormal = normalMatrix * normal;
    vertexTangent = normalMatrix * tangent;
    vertexBitangent = normalMatrix * bitangent;

    gl_Position = projection * view * mvPos;
}