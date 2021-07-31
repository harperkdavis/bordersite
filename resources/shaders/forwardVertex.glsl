#version 460 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec3 normal;

out vec2 vertexUV;
out vec3 vertexNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 cameraRotation;
uniform mat4 projection;

void main() {
    gl_Position = projection * view * model * vec4(position, 1.0);
    vertexUV = uv;

    vertexNormal = vec3(vec4(normal, 1) * cameraRotation);

}