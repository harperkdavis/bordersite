#version 330 core

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;

in vec2 vertexUV;
in vec3 vertexNormal;
in vec3 vertexPos;

uniform sampler2D texture_diffuse;
uniform sampler2D texture_specular;

void main() {

    gPosition = vec4(vertexPos, 1);
    gNormal = vec4(vertexNormal, 1);

    gAlbedoSpec = texture(texture_diffuse, vertexUV);

}
