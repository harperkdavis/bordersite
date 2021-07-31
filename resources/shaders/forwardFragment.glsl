#version 460 core

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

in vec2 vertexUV;
in vec3 vertexNormal;

out vec4 fragColor;

uniform DirectionalLight directionalLight;
uniform vec3 ambientLight;
uniform sampler2D texture_diffuse;
uniform vec4 meshColor = vec4(1, 1, 1, 1);

vec4 diffuse;

vec4 calculateDirectionalLight(DirectionalLight light, vec3 normal) {

    vec3 lightDir = -normalize(light.direction);
    lightDir.y = -lightDir.y;

    float diffuseFactor = max(dot(normal, lightDir), 0.0);
    vec4 diffuseColor = diffuse * vec4(light.color * light.intensity * diffuseFactor, 1);

    return diffuseColor;
}

void main() {
    diffuse = texture(texture_diffuse, vertexUV);
    vec4 ambient = diffuse * vec4(ambientLight, 1);
    vec4 lighting = calculateDirectionalLight(directionalLight, vertexNormal);
    fragColor = (ambient + lighting) * meshColor;
}