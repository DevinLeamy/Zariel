#version 410 core

// direction representating a 3D cube coordinate
in vec3 textureDirection;

// sample from the CUBE_MAP texture
uniform samplerCube skybox_sample;

out vec4 fragColor;

void main() {
   fragColor = texture(skybox_sample, textureDirection);
}