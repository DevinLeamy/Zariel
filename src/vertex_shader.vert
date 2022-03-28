#version 410 core

layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 aColor;

out vec3 color;

void main()
{
  color = aColor;
  gl_Position = vec4(vPosition, 1.0);
}