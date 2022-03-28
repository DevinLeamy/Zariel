#version 410 core

layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 aColor;

uniform mat3 rotationM;

out vec3 color;

void main()
{
  color = aColor;
  vec3 finalPosition = rotationM * vPosition;
  gl_Position = vec4(finalPosition, 1.0);
}