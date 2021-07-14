#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 uv;
layout(location = 2) in vec3 normal;

uniform float FXAA_SUBPIX_SHIFT = 1.0/4.0;
uniform vec2 resolution;

out vec2 vertexUV;
out vec4 fxaaPos;
out vec2 resFrame;

void main() {
    vertexUV = uv;
    gl_Position = vec4(position, 1.0);

    resFrame = vec2(1.0 / resolution.x, 1.0 / resolution.y);

    fxaaPos.xy = vertexUV.xy;
    fxaaPos.zw = vertexUV.xy - (resFrame * (0.5 + FXAA_SUBPIX_SHIFT));
}