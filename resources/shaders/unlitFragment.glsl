#version 460 core

in vec2 passUV;

out vec4 fragColor;

uniform sampler2D tex;

void main() {
    fragColor = texture(tex, passUV);
}