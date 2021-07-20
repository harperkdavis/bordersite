#version 330 core

in vec2 vertexUV;
uniform sampler2D texture_diffuse;

void main() {
    if (texture(texture_diffuse, vertexUV).a < 2 / 255.0) {
        discard;
    }
}