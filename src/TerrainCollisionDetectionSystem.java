import ecs.ComponentRegistry;
import ecs.ComponentStore;
import ecs.Entity;
import math.Vector3;
import math.Vector3i;

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

        Optional<TerrainCollision> maybeCollision = terrainCollision(transform.position, rigidBody.boundingBox);

        maybeCollision.ifPresent(entity::addComponent);
    }

    public Optional<TerrainCollision> terrainCollision(Vector3 worldOffset, BoundingBox boundingBox) {
        World world = World.getInstance();

        for (Vector3 vertex : boundingBox.vertices()) {
            Vector3i blockPosition = vertex.add(worldOffset).toVector3i(true);

            if (world.blockIsActive(blockPosition)) {
                return Optional.of(new TerrainCollision(blockPosition));
            }
        }
        return Optional.empty();
    }
}
