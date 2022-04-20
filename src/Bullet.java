import math.Vector3;

import java.util.ArrayList;

public class Bullet extends VoxelRenderable {
    private static VertexShader vs = new VertexShader("res/shaders/voxel_renderable.vert", new Uniform[] {
            new Uniform("viewM", Uniform.UniformT.MATRIX_4F),
            new Uniform("projectionM", Uniform.UniformT.MATRIX_4F),
            new Uniform("modelM", Uniform.UniformT.MATRIX_4F)
    });
    private static FragmentShader fs = new FragmentShader("res/shaders/voxel_renderable.frag", new Uniform[] {});
    private static ShaderProgram shader = new ShaderProgram(vs, fs);

    final private static VoxelGeometry BULLET_GEOMETRY = new VoxelGeometry(new Block[][][] {
            {{new Block(true, BlockType.STONE), new Block(true, BlockType.STONE)}},
    });
    final private static float BULLET_SPEED = 10.0f;

    private RigidBody rigidBody;

    public Bullet(Transform transform) {
        super(transform, BULLET_GEOMETRY, new Renderer(shader));

        Vector3 direction = transform.direction();
        BoundingBox boundingBox = new BoundingBox(transform.position, 1, 1, 2);
        this.rigidBody = new RigidBody(direction.normalize().scale(BULLET_SPEED), Vector3.zeros(), boundingBox);
    }

    @Override
    public ArrayList<Action> update(float dt) {
        ArrayList<Action> actions = new ArrayList<>();

        transform.position = rigidBody.update(dt, transform.position);

        /*
        if (collides with something) {
           BOOM!
        }
        */

        return actions;
    }
}
