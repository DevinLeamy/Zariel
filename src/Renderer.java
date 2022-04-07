import rendering.Mesh;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer {
    static void renderScene(Scene scene) {
        ArrayList<Model> models = scene.getModels();
        Camera perspective = scene.getPerspective();

        for (Model model : models) {
            renderModel(perspective, model);
        }
    }

    static void renderModel(Camera perspective, Model model) {
        ShaderProgram shader = model.getShaderProgram();
        Transform transform = model.getTransform();
        Mesh mesh = model.getMesh();

        shader.link();

        int shaderHandle = shader.getProgramHandle();

        int transformMHandler   = glGetUniformLocation(shaderHandle,  "transformM");
        int viewMHander         = glGetUniformLocation(shaderHandle, "viewM");
        int projectionMHandler  = glGetUniformLocation(shaderHandle, "projectionM");

        // transpose: true! This implies that the matrix will be read row by row (not column by column!)
        glUniformMatrix4fv(transformMHandler, true, transform.toMatrix().toFloatBuffer());
        glUniformMatrix4fv(viewMHander, true, perspective.viewMatrix().toFloatBuffer());
        glUniformMatrix4fv(projectionMHandler, true, perspective.projectionMatrix().toFloatBuffer());

        model.setUniforms();

        // bind vertex array
        glBindVertexArray(mesh.vao);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CW);

        // draw 'vertices' many vertices
        glDrawElements(GL_TRIANGLES, mesh.indices.size(), GL_UNSIGNED_INT, 0);

        // unbind
        glBindVertexArray(0);
    }
}
