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

struct Fog
{
    int activeFog;
    vec3 color;
    float density;
};

in vec2 vertexUV;
in vec3 vertexNormal;
in vec3 vertexPos;

out vec4 fragColor;

uniform sampler2D tex;
uniform vec3 ambientLight;
uniform vec3 cameraPos;
uniform Fog fog;
uniform float specularPower;
uniform float reflectance;
uniform vec4 meshColor;
uniform DirectionalLight directionalLight;
uniform PointLight pointLight;

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

    vec3 cameraDirection = normalize(cameraPos-position);
    vec3 reflectedLight = normalize(reflect(-toLightDir, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColor = specularC * lightIntensity  * specularFactor * reflectance * vec4(lightColor, 1.0);

    return (diffuseColor + specColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 light_direction = light.position - position;
    vec3 toLightDir  = normalize(light_direction);
    vec4 lightColor = calcLightColor(light.color, light.intensity, position, toLightDir, normal);

    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
    light.att.exponent * distance * distance;
    return lightColor / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);

    float diffuseFactor = max(dot(normal, light.direction), 0.0);
    diffuseColor = diffuseC * vec4(light.color, 1.0) * light.intensity * diffuseFactor;

    return diffuseColor;
}

vec4 calcFog(vec3 pos, vec4 color, Fog fog, vec3 ambientLight, DirectionalLight dirLight)
{
    vec3 fogColor = fog.color * (ambientLight + dirLight.color * dirLight.intensity);
    float distance = length(cameraPos - pos);
    float fogFactor = 1.0 / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0.0, 1.0);

    vec3 resultColour = mix(fogColor, color.xyz, fogFactor);
    return vec4(resultColour.xyz, color.w);
}


void main() {
    setupColors(vertexUV);

    vec4 specComp = calcDirectionalLight(directionalLight, vertexPos, vertexNormal);
    specComp += calcPointLight(pointLight, vertexPos, vertexNormal);

    fragColor = ambientC * vec4(ambientLight, 1) * meshColor + specComp;
    fragColor = calcFog(vertexPos, fragColor, fog, ambientLight, directionalLight);
}