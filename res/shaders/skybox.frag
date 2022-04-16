#version 410 core

in vec3 textureCoord;

uniform sampler2D skybox_sample;

out vec4 fragColor;

void main() {
   fragColor = texture(skybox_sample, textureCoord);
}