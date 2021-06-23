#version 330 core

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gViewPosition;
layout (location = 2) out vec4 gNormal;
layout (location = 3) out vec4 gAlbedoSpec;

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

    gPosition = vec4(vertexPos, 1);
    gViewPosition = vec4(vertexViewPos, 1);

    vec3 normal = normalize(vertexNormal);
    vec3 tangent = normalize(vertexTangent);
    vec3 bitangent = normalize(vertexBitangent);

    mat3 tangentMatrix = mat3(tangent, bitangent, normal);

    gNormal = vec4((texture(texture_normal, vertexUV).rgb * 2.0 - 1.0) * tangentMatrix, 1);

    gAlbedoSpec = vec4(texture(texture_diffuse, vertexUV).rgb, texture(texture_specular, vertexUV).r);

}
