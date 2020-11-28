#version 460 core

in vec3 passColor;
in vec2 passUV;

out vec4 outColor;

uniform sampler2D tex;

void main() {
    outColor = texture(tex, passUV) * passColor;
}