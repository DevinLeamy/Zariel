#version 410 core

in vec3 position;

uniform mat4 scalingM;
uniform mat4 screenCoordM;

out vec2 textureCoord;

void main() {
    gl_Position = screenCoordM * scalingM * vec4(position, 1.0);
    textureCoord = position.xy;
}