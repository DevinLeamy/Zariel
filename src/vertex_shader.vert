#version 410 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;

uniform mat3 rotationM;

void main()
{
  vec3 finalPosition = rotationM * pos;
  gl_Position = vec4(finalPosition, 1.0);
}