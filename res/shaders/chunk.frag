#version 410 core

in vec3 color;

out vec4 fragColor;

void main()
{
//    fragColor = vec4(0.5, color.x / (color.x + color.y), color.y / (color.x + color.y), 1.0);
    fragColor = vec4(color, 1.0);
}