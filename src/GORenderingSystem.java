import ecs.*;

public class GORenderingSystem extends InstanceSystem {
    private static VertexShader vs = new VertexShader("res/shaders/voxel_renderable.vert", new Uniform[] {
            new Uniform("viewM", Uniform.UniformT.MATRIX_4F),
            new Uniform("projectionM", Uniform.UniformT.MATRIX_4F),
            new Uniform("modelM", Uniform.UniformT.MATRIX_4F)
    });
    private static FragmentShader fs = new FragmentShader("res/shaders/voxel_renderable.frag", new Uniform[] {});
    private static ShaderProgram shader = new ShaderProgram(vs, fs);
    private static Renderer renderer = new Renderer(shader);

    World world = World.getInstance();

    ComponentStore<VoxelModel> voxelModelStore = ComponentStore.of(VoxelModel.class);
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);

    public GORenderingSystem() {
        super(Component.VOXEL_MODEL | Component.TRANSFORM, 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        VoxelModel model = voxelModelStore.getComponent(entity).get();
        Transform transform = transformStore.getComponent(entity).get();

        Camera camera = world.getPerspective();
        VoxelMesh mesh = MeshGenerator.generateLocalVoxelMesh(new VoxelGeometry(model.voxels));

        renderer.shader.setUniform("viewM", camera.viewMatrix());
        renderer.shader.setUniform("modelM", transform.modelMatrix());
        renderer.shader.setUniform("projectionM", camera.projectionMatrix());

        renderer.renderMesh(mesh);
    }
}
