import rendering.Mesh;

import static org.lwjgl.opengl.GL41.*;

public class Cube extends Model {
    public Cube(VertexShader vs, FragmentShader fs, Transform transform, Mesh mesh) {
        super(vs, fs, transform, mesh);
    }

    @Override
    public void update(float dt) {
        transform.rotate(0.00f, 0.00f, 0.01f);
    }

    @Override
    protected void setUniforms() {
        int rotationMHandler = glGetUniformLocation(shaderProgram.getProgramHandle(),  "rotationM");
        glUniformMatrix3fv(rotationMHandler, false, transform.rotationMatrix().toFloatBuffer());
    }
}
