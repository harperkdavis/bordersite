#version 460 core

const int MAX_POINT_LIGHTS = 64;

struct PointLight {
    vec3 color;
    vec3 position;
    float radius;
    float linear;
    float quadratic;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Fog {
    int activeFog;
    vec3 color;
    float density;
};

layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 brightColor;

in vec2 vertexUV;

vec3 vertexPos;
vec3 vertexNormal;
vec3 diffuse;
vec3 ambient;

float specular;
vec3 viewDir;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D shadowMap;

uniform DirectionalLight directionalLight;
uniform vec3 ambientLight;
uniform vec3 viewPos;
uniform Fog fog;

uniform mat4 view;
uniform mat4 lightSpaceMatrix;

uniform PointLight pointLights[MAX_POINT_LIGHTS];

vec3 calculateDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {

    vec3 lightDir = -normalize(light.direction);
    lightDir.y = -lightDir.y;

    float diffuseFactor = max(dot(normal, lightDir), 0.0);
    vec3 diffuseColor = diffuse * light.color * light.intensity * diffuseFactor;

    vec3 cameraDirection = normalize(viewPos - position);
    vec3 fromLightDir = -lightDir;
    vec3 reflectedLight = normalize(reflect(fromLightDir , normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, 8);
    vec3 specColor = specular * light.intensity * specularFactor * light.color;

    return (diffuseColor + specColor);
}

vec4 calcFog(vec3 pos, vec4 color, Fog fog, vec3 ambientLight, DirectionalLight dirLight) {
    vec3 fogColor = fog.color * (ambientLight + dirLight.color * dirLight.intensity);
    float distance = length(viewPos - pos);
    float fogFactor = 1.0 / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0.0, 1.0);

    vec3 resultColor = mix(fogColor, color.xyz, fogFactor);
    return vec4(resultColor.xyz, color.w);
}

float calcShadow(vec4 position) {

    vec3 projCoords = position.xyz / position.w;
    projCoords = projCoords * 0.5 + 0.5;

    float closestDepth = texture(shadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);

    for(int x = -1; x <= 1; ++x) {
        for(int y = -1; y <= 1; ++y) {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - 0.001 > pcfDepth ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    if(projCoords.z > 1.0)
    shadow = 0.0;

    return shadow;
}

void main() {

    vertexPos = texture(gPosition, vertexUV).xyz;
    vertexNormal = texture(gNormal, vertexUV).xyz;
    diffuse = texture(gAlbedoSpec, vertexUV).rgb;
    specular = texture(gAlbedoSpec, vertexUV).a;

    viewDir = normalize(viewPos - vertexPos);
    vec3 cameraDir = normalize((vec4(0, 0, 1, 1) * view).xyz);

    if (vertexNormal != vec3(0)) { // Normal Pixel

        ambient = diffuse * ambientLight;
        float shadow = calcShadow(lightSpaceMatrix * vec4(vertexPos, 1));

        vec3 lighting = calculateDirectionalLight(directionalLight, vertexPos, vertexNormal) * (1 - shadow);

        for (int i = 0; i < 128; ++i) {

            if (pointLights[i].radius == 0) {
                break;
            }

            float distance = length(pointLights[i].position - vertexPos);

            if (distance < pointLights[i].radius) {

                vec3 lightDir = normalize(pointLights[i].position - vertexPos);

                vec3 lightDiffuse = max(dot(vertexNormal, lightDir), 0.0) * diffuse * pointLights[i].color;

                vec3 halfwayDir = normalize(lightDir + viewDir);
                float spec = pow(max(dot(vertexNormal, halfwayDir), 0.0), 16.0);
                vec3 lightSpecular = specular * pointLights[i].color * spec * 4;

                float attenuation = 1.0 / (1.0 + pointLights[i].linear * distance + pointLights[i].quadratic * distance * distance);
                lightDiffuse *= attenuation;
                lightSpecular *= attenuation;
                lighting += lightDiffuse + lightSpecular;

            }
        }

        fragColor = vec4((ambient + lighting), 1.0);
        fragColor = calcFog(vertexPos, fragColor, fog, ambientLight, directionalLight);

    } else { // Sky pixel

    }

    float brightness = (fragColor.x * 2.2 + fragColor.y * 1.6 + fragColor.z * 2.4) / 8;

    brightColor = vec4(0.0, 0.0, 0.0, 1.0);
    if (brightness > 1.0) {
        brightColor = vec4(fragColor.rgb, 1.0);
    } else {
        brightColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}
