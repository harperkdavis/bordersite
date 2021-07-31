#version 460 core

in vec2 passUV;

out vec4 fragColor;

uniform sampler2D tex;
uniform vec4 meshColor = vec4(1, 1, 1, 1);

void main() {
    fragColor = texture(tex, passUV) * meshColor;
}