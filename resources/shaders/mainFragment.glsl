#version 460 core

const int MAX_POINT_LIGHTS = 64;

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
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

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D ssao;
uniform sampler2D shadowMap;

uniform DirectionalLight directionalLight;
uniform vec3 ambientLight;
uniform vec3 cameraPos;
uniform Fog fog;

uniform mat4 view;
uniform mat4 lightSpaceMatrix;

uniform PointLight pointLights[MAX_POINT_LIGHTS];

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);

    float diffuseFactor = max(dot(normal, -light.direction), 0.0);
    diffuseColor = vec4(diffuse, 1) * vec4(light.color, 1.0) * light.intensity * diffuseFactor;

    return diffuseColor;
}

vec4 calcFog(vec3 pos, vec4 color, Fog fog, vec3 ambientLight, DirectionalLight dirLight) {
    vec3 fogColor = fog.color * (ambientLight + dirLight.color * dirLight.intensity);
    float distance = length(-pos);
    float fogFactor = 1.0 / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0.0, 1.0);

    vec3 resultColour = mix(fogColor, color.xyz, fogFactor);
    return vec4(resultColour.xyz, color.w);
}

float calcShadow(vec4 position) {

    vec3 projCoords = position.xyz / position.w;
    projCoords = projCoords * 0.5 + 0.5;

    float closestDepth = texture(shadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);

    for(int x = -2; x <= 2; ++x) {
        for(int y = -2; y <= 2; ++y) {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - 0.0005 > pcfDepth ? 1.0 : 0.0;
        }
    }
    shadow /= 25.0;

    if(projCoords.z > 1.0)
    shadow = 0.0;

    return shadow;
}

void main() {

    vertexPos = texture(gPosition, vertexUV).xyz;
    vertexNormal = texture(gNormal, vertexUV).xyz;
    diffuse = texture(gAlbedoSpec, vertexUV).rgb;
    float ao = texture(ssao, vertexUV).r;


    vec4 specComp = calcDirectionalLight(directionalLight, vertexPos, vertexNormal);
    vec3 viewDir = normalize(cameraPos - vertexPos);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0) {
            PointLight light = pointLights[i];

            float distance = length(light.position - vertexPos);
            if (distance < light.intensity) {

                vec3 lightDir = normalize(light.position - vertexPos);
                vec3 lightDiffuse = max(dot(vertexNormal, lightDir), 0.0) * diffuse * light.color;

                vec3 halfwayDir = normalize(lightDir + viewDir);
                float spec = pow(max(dot(vertexNormal, halfwayDir), 0.0), 16.0);
                vec3 lightSpecular = light.color * spec;

                float attentuation = 1.0 / (1.0 + light.linear * distance + light.quadratic * distance * distance);
                lightDiffuse *= attentuation;
                lightSpecular *= attentuation;

                specComp += vec4(lightDiffuse + lightSpecular, 1);
            }
        }
    }

    float shadow = calcShadow(lightSpaceMatrix * vec4(vertexPos, 1));
    fragColor = clamp(vec4(ambientLight * diffuse * ao, 1) + specComp * (1 - shadow), 0, 1);
    fragColor = calcFog(vertexPos, fragColor, fog, ambientLight, directionalLight);

    float brightness = (fragColor.x * 2 + fragColor.y * 1.6 + fragColor.z * 2.4) / 3.2;

    if(brightness > 1.0)
        brightColor = vec4(fragColor.rgb, 1.0);
    else
        brightColor = vec4(0.0, 0.0, 0.0, 1.0);
}