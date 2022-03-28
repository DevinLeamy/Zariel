#version 410 core

in vec3 color;

out vec4 fragColor;

void main()
{
    fragColor = vec4(color, 1.0);
//    fragColor = vec4(0.2, 0.6, 0.7, 1.0);
}