import rendering.Mesh;

import static org.lwjgl.opengl.GL41.*;

public class Cube extends Model {
    public Cube(VertexShader vs, FragmentShader fs, Transform transform, Mesh mesh) {
        super(vs, fs, transform, mesh);
    }

    @Override
    public void update(float dt) {}

    @Override
    protected void setUniforms() {}
}
