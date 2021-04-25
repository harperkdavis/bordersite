#version 460 core

const int MAX_POINT_LIGHTS = 16;
const int MAX_SPOT_LIGHTS = 16;

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

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct SpotLight {
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

struct Fog {
    int activeFog;
    vec3 color;
    float density;
};

in vec2 vertexUV;
in vec3 vertexNormal;
in vec3 vertexPos;

out vec4 fragColor;

uniform sampler2D tex;
uniform DirectionalLight directionalLight;
uniform vec3 ambientLight;
uniform vec3 cameraPos;
uniform Fog fog;

uniform float specularPower;
uniform float reflectance;

uniform vec4 meshColor;

uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

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

    vec3 cameraDirection = normalize(position);
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

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.pl.position - position;
    vec3 toLightDir  = normalize(lightDirection);
    vec3 fromLightDir  = -toLightDir;
    float spotAlpha = dot(fromLightDir, normalize(light.conedir));

    vec4 color = vec4(0, 0, 0, 0);

    if ( spotAlpha > light.cutoff )
    {
        color = calcPointLight(light.pl, position, normal);
        color *= (1.0 - (1.0 - spotAlpha)/(1.0 - light.cutoff));
    }
    return color;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);

    float diffuseFactor = max(dot(normal, light.direction), 0.0);
    diffuseColor = diffuseC * vec4(light.color, 1.0) * light.intensity * diffuseFactor;

    return diffuseColor;
}

vec4 calcFog(vec3 pos, vec4 color, Fog fog, vec3 ambientLight, DirectionalLight dirLight) {
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

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0 ) {
            specComp += calcPointLight(pointLights[i], vertexPos, vertexNormal);
        }
    }

    for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
        if (spotLights[i].pl.intensity > 0){
            specComp += calcSpotLight(spotLights[i], vertexPos, vertexNormal);
        }
    }

    fragColor = ambientC * vec4(ambientLight, 1) * meshColor + specComp;
    fragColor = calcFog(vertexPos, fragColor, fog, ambientLight, directionalLight);
}