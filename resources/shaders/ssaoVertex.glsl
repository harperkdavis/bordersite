#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec3 normal;

out vec2 vertexUV;

void main() {
    vertexUV = uv;
    gl_Position = vec4(position, 1.0);
}