import rendering.Mesh;

abstract public class Model {
    protected Transform transform;
    protected Mesh mesh;
    protected ShaderProgram shaderProgram;

    public Model(ShaderProgram shaderProgram, Transform transform, Mesh mesh) {
        this.mesh = mesh;
        this.transform = transform;
        this.shaderProgram = shaderProgram;
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }
    public Transform getTransform() {
        return transform;
    }
    public Mesh getMesh() {
        return mesh;
    }

    public void update(float dt) {}

    public void setUniforms() {}

    public void cleanUp() {
        mesh.dispose();
    }
}
