import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL41.*;

import math.Matrix4;
import math.Vector3;
import rendering.Mesh;

abstract public class Model {
    protected Transform transform;
    protected Mesh mesh;
    protected ShaderProgram shaderProgram;

    public Model(VertexShader vs, FragmentShader fs, Transform transform, Mesh mesh) {
        this.mesh = mesh;
        this.transform = transform;
        this.shaderProgram = new ShaderProgram(vs, fs);
    }

    final public void render(Camera camera) {
        // bind shader program
        shaderProgram.link();

        _setUniforms(camera);

        // bind buffer array
        glBindVertexArray(mesh.vao);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CW);

        // draw 'vertices' many vertices
        glDrawElements(GL_TRIANGLES, mesh.indices.size(), GL_UNSIGNED_INT, 0);

        // unbind
        glBindVertexArray(0);
    }

    private void _setUniforms(Camera camera) {
        int shaderHandle = shaderProgram.getProgramHandle();

        int transformMHandler   = glGetUniformLocation(shaderHandle,  "transformM");
        int viewMHander         = glGetUniformLocation(shaderHandle, "viewM");
        int projectionMHandler  = glGetUniformLocation(shaderHandle, "projectionM");

        // debugging
//        int cameraTransMHandler = glGetUniformLocation(shaderHandle, "cameraTransM");


//        float radius = 5.0f;
//        float camX = (float) Math.sin(glfwGetTime()) * radius;
//        float camZ = (float) Math.cos(glfwGetTime()) * radius;
//
//        camera.position = new Vector3(camX, 0, camZ);
////        glUniformMatrix4fv(
////                cameraTransMHandler, true,
////                Matrix4.genTranslationMatrix(-camX, 0, -camZ).toFloatBuffer()
////        );
//        glUniformMatrix4fv(viewMHander, true,
//                camera.lookAt(
//                        new Vector3(camX, 0, camZ),
//                        new Vector3(0, 0, 0),
//                        new Vector3(0, 1, 0)
//                ).toFloatBuffer());

        // -----

        // transpose: true! This implies that the matrix will be read row by row (not column by column!)
        glUniformMatrix4fv(transformMHandler, true, transform.toMatrix().toFloatBuffer());
        glUniformMatrix4fv(viewMHander, true, camera.viewMatrix().toFloatBuffer());
        glUniformMatrix4fv(projectionMHandler, true, camera.projectionMatrix().toFloatBuffer());
        setUniforms();
    }

    protected void setUniforms() {}

    public void update(float dt) {}

    public void cleanUp() {
        mesh.dispose();
    }
}
