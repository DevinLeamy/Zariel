#version 410 core

in vec3 color;
in vec3 w_normal;
in float ao;
in vec2 texture_coord;

vec3 light = vec3(0.5f, -1.0f, 0.0f);
float diffuse_intensity = 0.6f;
float ambient_intensity = 0.4f;

out vec4 fragColor;

uniform sampler2D texture_sample;

void main()
{
    float diffuse_factor = dot(normalize(w_normal), -light) * diffuse_intensity;

    if (diffuse_factor <= 0) {
        diffuse_factor = 0;
    }

//    fragColor = vec4((ambient_intensity + diffuse_factor) * color * ao, 1.0);
    fragColor = ((ambient_intensity + diffuse_factor) * ao) * texture(texture_sample, texture_coord);
}