#version 330 core
out vec4 fragColor;

in vec2 vertexUV;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D ssaoNoise;

uniform vec3 samples[64];

int KERNEL_SIZE = 64;
float RADIUS = 2.5;
float BIAS = 0.025;

uniform vec2 noiseScale;

uniform mat4 projection;
uniform mat4 view;

void main() {

    vec3 vertexNormal = texture(gNormal, vertexUV).rgb;
    vertexNormal.y = -vertexNormal.y;

    if (vertexNormal == vec3(0.0)) discard;

    vec3 fragPos = texture(gPosition, vertexUV).xyz;
    vec3 normal = normalize(mat3(view) * vertexNormal);
    vec3 randomVec = normalize(texture(ssaoNoise, vertexUV * noiseScale).xyz);

    vec3 tangent = normalize(randomVec - normal * dot(randomVec, normal));
    vec3 bitangent = cross(normal, tangent);
    mat3 TBN = mat3(tangent, bitangent, normal);

    float occlusion = 0.0;
    for (int i = 0; i < KERNEL_SIZE; ++i) {
        vec3 samplePos = TBN * samples[i];
        samplePos = fragPos + samplePos * RADIUS;

        vec4 offset = vec4(samplePos, 1.0);
        offset = projection * offset;
        offset.xyz /= offset.w;
        offset.xyz = offset.xyz * 0.5 + 0.5;

        float sampleDepth = texture(gPosition, offset.xy).z;

        float rangeCheck = smoothstep(0.0, 1.0, RADIUS / abs(fragPos.z - sampleDepth));
        occlusion += (sampleDepth >= samplePos.z + BIAS ? 1.0 : 0.0) * rangeCheck;
    }
    occlusion = 1.0 - (occlusion / KERNEL_SIZE);

    fragColor = vec4(vec3(occlusion), 1);
}