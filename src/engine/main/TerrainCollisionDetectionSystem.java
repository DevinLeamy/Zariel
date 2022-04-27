package engine.main;

import engine.ecs.ComponentRegistry;
import engine.ecs.ComponentStore;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import math.Matrix3;
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

        Optional<TerrainCollision> maybeCollision = terrainCollision(transform, rigidBody.boundingBox);

        maybeCollision.ifPresent(entity::addComponent);
    }

    public Optional<TerrainCollision> terrainCollision(Transform transform, BoundingBox boundingBox) {
        World world = World.getInstance();

        Matrix3 rotationM = Matrix3.genRotationMatrix(transform.rotation);
        for (Face face : boundingBox.faces) {
            Vector3 vertexWorldPosition = Matrix3.mult(rotationM, face.center()).add(transform.position);
            Vector3i blockCoordinate = vertexWorldPosition.toVector3i(true);

            if (world.blockIsActive(blockCoordinate)) {
                return Optional.of(new TerrainCollision(blockCoordinate, face));
            }
        }

        return Optional.empty();
    }
}
