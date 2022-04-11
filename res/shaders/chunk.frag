#version 410 core

in vec3 color;
in vec3 w_normal;

vec3 light = vec3(0.0f, -1.0f, 0.0f);
out vec4 fragColor;

void main()
{
    float diffuse_factor = dot(normalize(w_normal), -light);

    if (diffuse_factor <= 0) {
        diffuse_factor = 0;
    }
    fragColor = vec4(diffuse_factor * color, 1.0);
}