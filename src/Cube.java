import rendering.Mesh;

import static org.lwjgl.opengl.GL41.*;

public class Cube extends Model {
    public Cube(ShaderProgram shaderProgram, Transform transform, Mesh mesh) {
        super(shaderProgram, transform, mesh);
    }

    @Override
    public void update(float dt) {}
}
