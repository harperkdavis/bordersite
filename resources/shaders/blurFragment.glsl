#version 330 core
out vec4 fragColor;

in vec2 vertexUV;

uniform sampler2D image;

uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main() {
    vec2 tex_offset = 1.0 / textureSize(image, 0); // gets size of single texel
    vec3 result = texture(image, vertexUV).rgb * weight[0]; // current fragment's contribution
    for (int i = 1; i < 4; ++i) {
        result += texture(image, vertexUV + vec2(tex_offset.x * i, 0.0)).rgb * (0.4 / i);
        result += texture(image, vertexUV - vec2(tex_offset.x * i, 0.0)).rgb * (0.4 / i);
    }
    for (int i = 1; i < 4; ++i) {
        result += texture(image, vertexUV + vec2(0.0, tex_offset.y * i)).rgb * (0.4 / i);
        result += texture(image, vertexUV - vec2(0.0, tex_offset.y * i)).rgb * (0.4 / i);
    }
    fragColor = vec4(result / 4, 1);
}