#version 410 core

in vec2 textureCoord;

uniform sampler2D texture_sample;

out vec4 fragColor;

void main() {
    fragColor = texture(texture_sample, textureCoord);
}