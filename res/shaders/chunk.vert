#version 410 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec3 aColor;

uniform mat4 viewM;
uniform mat4 projectionM;

out vec3 color;

void main()
{
    color = aColor;
    gl_Position = projectionM * viewM * vec4(pos, 1.0f);
}