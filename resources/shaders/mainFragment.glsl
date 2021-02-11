#version 460 core

in vec3 passColor;
in vec2 passUV;
in vec3 passNormal;

out vec4 outColor;

uniform sampler2D tex;

void main() {
    vec4 normalShading = vec4(1, 1, 1, 1);
    if (passNormal.x > 0) {
        normalShading = vec4(0.85, 0.85, 0.85, 1);
    }
    if (passNormal.x < 0) {
        normalShading = vec4(0.9, 0.9, 0.9, 1);
    }
    if (passNormal.z > 0) {
        normalShading = vec4(0.8, 0.8, 0.8, 1);
    }
    if (passNormal.z < 0) {
        normalShading = vec4(0.95, 0.95, 0.95, 1);
    }
    if (passNormal.y < 0) {
        normalShading = vec4(0.75, 0.75, 0.75, 1);
    }
    outColor = texture(tex, passUV) * normalShading;
}