import ecs.ComponentRegistry;
import ecs.ComponentStore;
import ecs.Entity;
import math.Matrix3;
import math.Matrix4;
import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

public class TerrainCollisionDetectionSystem extends InstanceSystem {
    ComponentStore<RigidBody> rigidBodyStore = ComponentStore.of(RigidBody.class);
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);

    public TerrainCollisionDetectionSystem() {
        super(ComponentRegistry.getSignature(RigidBody.class, Transform.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        RigidBody rigidBody = rigidBodyStore.getComponent(entity).get();
        Transform transform = transformStore.getComponent(entity).get();

        Optional<TerrainCollision> maybeCollision = terrainCollision(transform, rigidBody.boundingBox);

        maybeCollision.ifPresent(entity::addComponent);
    }

    public Optional<TerrainCollision> terrainCollision(Transform transform, BoundingBox boundingBox) {
        World world = World.getInstance();

        for (Vector3 vertex : boundingBox.vertices()) {
            Vector3 blockPosition = Matrix3.mult(Matrix3.genRotationMatrix(transform.rotation), vertex).add(transform.position);
            float x = blockPosition.x;
            float y = blockPosition.y;
            float z = blockPosition.z;

            Vector3i worldBlock = new Vector3i(
                    (int) Math.floor(x),
                    (int) Math.floor(y),
                    (int) Math.floor(z)
            );
            if (world.blockIsActive(worldBlock)) {
                Vector3i blockAbove = Vector3i.add(worldBlock, new Vector3i(0, 1, 0));
                /**
                 * If the block above the collision is active then you must have collided
                 * from the side. i.e. the collision is not a ground collision.
                 */
                return Optional.of(new TerrainCollision(worldBlock, !world.blockIsActive(blockAbove)));
            }
        }
        return Optional.empty();
    }
}
