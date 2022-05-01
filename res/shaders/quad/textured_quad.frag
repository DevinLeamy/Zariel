#version 410 core

in vec2 textureCoord;
in vec4 pass_color;

uniform sampler2D texture_sample;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(texture_sample, textureCoord);
    vec4 mixed = textureColor * pass_color;
    fragColor = vec4(mixed.xyz, pass_color.w);
}