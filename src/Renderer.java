import math.Matrix4;
import math.Vector3;

import static org.lwjgl.opengl.GL41.*;

class Renderer {
    private ShaderProgram shader;
    public Renderer(ShaderProgram shader) {
        this.shader = shader;
    }

    public void renderMesh(Camera perspective, VoxelMesh mesh, Vector3 location) {
//        System.out.println("RENDER");
        shader.link();
        int shaderHandle = shader.getProgramHandle();

        // set uniforms
        int locationHandler = glGetUniformLocation(shaderHandle, "location");
        int viewMHandler         = glGetUniformLocation(shaderHandle, "viewM");
        int projectionMHandler  = glGetUniformLocation(shaderHandle, "projectionM");

        Vector3 cameraPos = perspective.transform.position;
        Vector3 playerPos = World.getInstance().player.transform.position;
        Matrix4 viewMatrix = Camera.lookAt(cameraPos, playerPos, new Vector3(0, 1, 0));

        glUniformMatrix4fv(viewMHandler, true, viewMatrix.toFloatBuffer());
        glUniformMatrix4fv(projectionMHandler, true, perspective.projectionMatrix().toFloatBuffer());
        glUniform3fv(locationHandler, location.toArray());


//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
//        glFrontFace(GL_CCW);

        mesh.link();

        glDrawArrays(GL_TRIANGLES, 0, mesh.vertices());

        mesh.unlink();
    }
}