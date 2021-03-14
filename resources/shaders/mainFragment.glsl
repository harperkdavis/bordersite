#version 460 core

in vec3 passColor;
in vec2 passUV;
in float passNormal;

out vec4 outColor;

uniform sampler2D tex;

void main() {
    vec4 normalShading = vec4(passNormal, passNormal, passNormal, 1);
    outColor = texture(tex, passUV) * normalShading;
}