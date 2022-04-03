import rendering.Mesh;
import rendering.MeshLoader;

public class CarModel extends Model {
    private static VertexShader vs = new VertexShader("res/shaders/car.vert");
    private static FragmentShader fs = new FragmentShader("res/shaders/car.frag");
    private static ShaderProgram carShader = new ShaderProgram(vs, fs);
    private static Mesh carMesh = MeshLoader.loadMesh("res/cube.obj");
    public CarModel(Transform transform) {
        super(carShader, transform, carMesh);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void setUniforms() {

    }
}
