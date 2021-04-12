#version 460 core

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation att;
};

struct DirectionalLight
{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct SpotLight
{
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

in vec2 vertexUV;
in vec3 vertexNormal;
in vec3 vertexPos;

out vec4 fragColor;

uniform sampler2D tex;
uniform vec3 ambientLight;
uniform vec3 cameraPos;
uniform float specularPower;
uniform float reflectance;
uniform vec4 meshColor;
uniform DirectionalLight directionalLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(vec2 uv) {
    ambientC = texture(tex, uv);
    diffuseC = ambientC;
    specularC = ambientC;
}

vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specColor = vec4(0, 0, 0, 0);

    float diffuseFactor = max(dot(normal, toLightDir), 0.0);
    diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    vec3 cameraDirection = normalize(-position);
    vec3 reflectedLight = normalize(reflect(-toLightDir, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColor = specularC * lightIntensity  * specularFactor * reflectance * vec4(lightColor, 1.0);

    return (diffuseColor + specColor);
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);

    float diffuseFactor = max(dot(normal, light.direction), 0.0);
    diffuseColor = diffuseC * vec4(light.color, 1.0) * light.intensity * diffuseFactor;

    return diffuseColor;
}


void main() {
    setupColors(vertexUV);

    vec4 specComp = calcDirectionalLight(directionalLight, vertexPos, vertexNormal);

    fragColor = ambientC * vec4(ambientLight, 1) * meshColor + specComp;
}