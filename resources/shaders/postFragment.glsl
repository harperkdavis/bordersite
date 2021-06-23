#version 330 core
out vec4 fragColor;

in vec2 vertexUV;

uniform sampler2D hdrBuffer;
uniform sampler2D brightBuffer;

uniform float exposure;

void main() {
    const float gamma = 1.2;
    vec3 hdrColor = texture(hdrBuffer, vertexUV).rgb;

    vec3 mapped = vec3(1.0) - exp(-hdrColor * exposure);
    mapped = pow(mapped, vec3(1.0 / gamma));

    fragColor = vec4(mapped, 1.0);
}
