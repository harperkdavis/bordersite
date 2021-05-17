#version 330 core

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;

in vec2 vertexUV;
in vec3 vertexNormal;
in vec3 vertexPos;

uniform sampler2D texture_diffuse;
uniform sampler2D texture_specular;

void main() {

    gPosition = vertexPos;
    gNormal = normalize(vertexNormal);

    gAlbedoSpec.rgb = texture(texture_diffuse, vertexUV).rgb;
    gAlbedoSpec.a = texture(texture_specular, vertexUV).r;

}
