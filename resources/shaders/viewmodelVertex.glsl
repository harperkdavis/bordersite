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
    mat4 modelViewMatrix = view * model;
    vec4 mvPos = modelViewMatrix * vec4(position, 1.0);

    gl_Position = projection * mvPos;
    vertexUV = uv;

    vertexNormal = normalize(vec4(normal, 0.0) * model).xyz;
    vertexPos = (vec4(position, 1.0) * model).xyz;
}