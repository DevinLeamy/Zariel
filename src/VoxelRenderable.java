import math.Matrix4;
import math.Vector3;

import java.util.ArrayList;

abstract public class VoxelRenderable {
    private static VertexShader vs = new VertexShader("res/shaders/voxel_renderable.vert", new Uniform[] {
            new Uniform("viewM", Uniform.UniformT.MATRIX_4F),
            new Uniform("projectionM", Uniform.UniformT.MATRIX_4F),
            new Uniform("modelM", Uniform.UniformT.MATRIX_4F)
    });
    private static FragmentShader fs = new FragmentShader("res/shaders/voxel_renderable.frag", new Uniform[] {});
    private static ShaderProgram shader = new ShaderProgram(vs, fs);

    Transform transform;
    VoxelGeometry shape;
    VoxelMesh mesh;
    Renderer renderer;
    final public long id;

    private static long NEXT_ID = 0;

    public VoxelRenderable(Transform transform, VoxelGeometry shape) {
        this.transform = transform;
        this.shape = shape;
        this.mesh = MeshGenerator.generateVoxelMesh(shape, transform.position.toVector3i());
        this.renderer = new Renderer(shader);
        this.id = NEXT_ID;

        NEXT_ID += 1;
    }

    abstract public ArrayList<Action> update(float dt);

    public void render() {
        World world = World.getInstance();
        Camera camera = world.getPerspective();

        Vector3 cameraPos = camera.transform.position;
        Vector3 playerPos = world.player.transform.position;
//
        Matrix4 viewMatrix = Camera.lookAt(cameraPos, playerPos, new Vector3(0, 1, 0));

        renderer.shader.setUniform("viewM", viewMatrix);
        renderer.shader.setUniform("modelM", transform.modelMatrix());
        renderer.shader.setUniform("projectionM", camera.projectionMatrix());

        renderer.renderMesh(mesh);
    }
}
