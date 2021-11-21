#version 330 core
out vec4 fragColor;

in vec2 vertexUV;
uniform sampler2D screen;
uniform float deltaTime;

const float bayerMatrix[] = float[64](
0, 32, 8, 40, 2, 34, 10, 42,
48, 16, 56, 24, 50, 18, 58, 26,
12, 44, 4, 36, 14, 46, 6, 38,
60, 28, 52, 20, 62, 30, 54, 22,
3, 35, 11, 43, 1, 33, 9, 41,
51, 19, 59, 27, 49, 17, 57, 25,
15, 47, 7, 39, 13, 45, 5, 37,
63, 31, 55, 23, 61, 29, 53, 21
);

const float dz = 0.2;

vec4 nearestColor(vec4 color) {
    return floor(color / 0.05) * 0.05;
}

void main() {

    vec2 pixCoords = floor(vertexUV * textureSize(screen, 0));
    vec4 color = texture(screen, vertexUV);

    float vignette = (pow(vertexUV.x - 0.5, 2) + pow(vertexUV.y - 0.5, 2)) * 2 + 1;

    fragColor = vec4(nearestColor(color + dz * (bayerMatrix[int(pixCoords.x + deltaTime * 4) % 8 + (int(pixCoords.y + deltaTime * 4) % 8) * 8] / 64.0f)).xyz / vignette, color.a);
}

