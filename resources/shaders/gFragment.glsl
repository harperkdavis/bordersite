#version 330 core

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;

in vec2 vertexUV;
in vec3 vertexPos;
in vec3 vertexViewPos;

in vec3 vertexNormal;
in vec3 vertexTangent;
in vec3 vertexBitangent;

uniform sampler2D texture_diffuse;
uniform sampler2D texture_specular;
uniform sampler2D texture_normal;

void main() {

    gPosition = vertexPos;

    vec3 normal = normalize(vertexNormal);
    vec3 tangent = normalize(vertexTangent);
    vec3 bitangent = normalize(vertexBitangent);

    mat3 tangentMatrix = mat3(tangent, bitangent, normal);

    gNormal = normal;

    if (texture(texture_diffuse, vertexUV).a < 2 / 255.0) {
        discard;
    }

    gAlbedoSpec = vec4(texture(texture_diffuse, vertexUV).rgb, (texture(texture_specular, vertexUV).r + 0.02) / (1 / 1.02));

}
