import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

public class Bullet extends VoxelRenderable {
    final private static VoxelGeometry BULLET_GEOMETRY = VoxelGeometry.loadFromFile("res/voxels/bomb.vox");
    final private static float BULLET_SPEED = 10.0f;

    private RigidBody rigidBody;

    public Bullet(Transform transform) {
        super(transform, BULLET_GEOMETRY);

        Vector3 direction = transform.direction();
        BoundingBox boundingBox = new BoundingBox(transform.position, 1, 1, 2);
        this.rigidBody = new RigidBody(direction.normalize().scale(BULLET_SPEED), Vector3.zeros(), boundingBox);
    }

    @Override
    public ArrayList<Action> update(float dt) {
        ArrayList<Action> actions = new ArrayList<>();

        transform.position = rigidBody.update(dt, transform.position);
        Optional<RigidBody.Collision> maybeCollision = rigidBody.collide();

        maybeCollision.ifPresent(collision -> {
            actions.addAll(collision.collide());
            actions.add(new DespawnGameObjectAction(id));
        });

        if (rigidBody.onGround()) {
            actions.add(new ExplodeBlockAction(transform.position.toVector3i().add(new Vector3i(0, -1, 0))));
            actions.add(new DespawnGameObjectAction(id));
        }

        return actions;
    }
}
