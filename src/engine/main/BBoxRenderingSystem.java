package engine.main;

import engine.ecs.ComponentRegistry;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import math.Vector3;

public class BBoxRenderingSystem extends InstanceSystem {
    private static VertexShader vs = new VertexShader("res/shaders/boundingbox/vs.vert", new Uniform[] {
            new Uniform("viewM", Uniform.UniformT.MATRIX_4F),
            new Uniform("projectionM", Uniform.UniformT.MATRIX_4F),
            new Uniform("modelM", Uniform.UniformT.MATRIX_4F)
    });
    private static FragmentShader fs = new FragmentShader("res/shaders/boundingbox/fs.frag", new Uniform[] {});
    private static ShaderProgram shader = new ShaderProgram(vs, fs);
    private static Renderer renderer = new Renderer(shader);

    public BBoxRenderingSystem() {
        super(ComponentRegistry.getSignature(Transform.class, RigidBody.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        Transform transform = entity.getComponent(Transform.class).get();
        BoundingBox bbox = entity.getComponent(RigidBody.class).get().boundingBox;

        Transform bboxTransform = new Transform(
                transform.position,
                transform.rotation,
                new Vector3(
                        bbox.getWidth(),
                        bbox.getHeight(),
                        bbox.getDepth()
                )
        );

        Camera camera = world.getPerspective();
        VoxelMesh mesh = Cube.mesh;

        renderer.shader.setUniform("viewM", camera.viewMatrix());
        renderer.shader.setUniform("modelM", bboxTransform.modelMatrix());
        renderer.shader.setUniform("projectionM", camera.projectionMatrix());

        renderer.renderWireMesh(mesh);
    }
}
