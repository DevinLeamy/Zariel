#version 410 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec3 aColor;
layout (location = 4) in float oaIndex;

uniform mat4 viewM;
uniform mat4 projectionM;
uniform mat4 modelM;

vec4 oaCurve = vec4(0.5, 0.65, 0.80, 1.0);

out vec3 color;
out vec3 w_normal; // world coordinate normals
out float ao;
out vec2 texture_coord;

void main()
{
    w_normal = normal;
    ao = oaCurve[int(oaIndex)];
    color = aColor;
    texture_coord = uv;

    gl_Position = projectionM * viewM * modelM * vec4(pos, 1.0f);
}