#version 410 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec3 aColor;

uniform mat4 viewM;
uniform mat4 projectionM;

out vec3 color;
out vec3 w_normal; // world coordinate normals

void main()
{
    color = aColor;
    w_normal = normal;
    gl_Position = projectionM * viewM * vec4(pos, 1.0f);
}