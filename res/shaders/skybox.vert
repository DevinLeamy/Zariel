#version 410 core

layout (location = 0) in vec3 pos;

uniform mat4 viewM;
uniform mat4 projectionM;

out vec3 textureDirection;

void main() {
    /**
     * OpenGL is smart enough to associate 3D cube position
     * coords with 3D texture coords, so we can set the texture
     * coords to the vertex coords.
     */
    textureDirection = pos;

    /**
     * Orients the camera in the screen so that we're
     * shown the correct part of the skybox.
     */
    gl_Position = projectionM * viewM * vec4(pos, 1.0);
}