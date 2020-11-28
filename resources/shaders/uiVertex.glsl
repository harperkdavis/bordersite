#version 460 core

in vec3 position;
in vec3 color;
in vec2 uv;
in vec3 normals;

out vec3 passColor;
out vec2 passUV;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    gl_Position = projection * view * model * vec4(position, 1.0);
    passColor = color;
    passUV = uv;
}