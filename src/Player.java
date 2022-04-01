import math.Vector3;
import rendering.Mesh;

import static org.lwjgl.opengl.GL41.*;

public class Player extends Model {
    private Camera camera;
    public Player(VertexShader vs, FragmentShader fs, Transform transform, Mesh mesh) {
        super(vs, fs, transform, mesh);
        camera = new Camera(
        (float) Math.PI / 2,
            new Vector3(0, 0.5f, -1.5f),
            new Vector3(0, 0, 0)
        );
    }

    @Override
    public void update(float dt) {
        transform.rotate(0.00f, 0.01f, 0.00f);
    }

    @Override
    protected void setUniforms() {
        int shaderHandle = shaderProgram.getProgramHandle();

        int transformMHandler   = glGetUniformLocation(shaderHandle,  "transformM");
        int viewMHander         = glGetUniformLocation(shaderHandle, "viewM");
        int projectionMHandler  = glGetUniformLocation(shaderHandle, "projectionM");

        // transpose: true! This implies that the matrix will be read row by row (not column by column!)
        glUniformMatrix4fv(transformMHandler, true, transform.toMatrix().toFloatBuffer());
        glUniformMatrix4fv(viewMHander, true, camera.viewMatrix().toFloatBuffer());
        glUniformMatrix4fv(projectionMHandler, true, camera.projectionMatrix().toFloatBuffer());
    }
}
