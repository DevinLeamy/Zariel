#version 410 core

in vec3 color;

out vec4 fragColor;

//float random(vec2 p)
//{
//    return cos(p.x);
//}

void main()
{
//    fragColor = vec4(color, 1.0f);
//    fragColor = vec4(1.0, 0.2, 0.6, 1.0);
    fragColor = vec4(0.5, sin(color.x), cos(color.y), 1.0);
}