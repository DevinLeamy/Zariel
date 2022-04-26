import ecs.ComponentRegistry;
import ecs.ComponentStore;
import ecs.Entity;
import math.Vector3;

import java.util.Optional;

public class MovementSystem extends InstanceSystem {
    ComponentStore<Dynamics>  dynamicsStore = ComponentStore.of(Dynamics.class);
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);

    public MovementSystem() {
        super(ComponentRegistry.getSignature(Transform.class, Dynamics.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        Dynamics dynamics = dynamicsStore.getComponent(entity).get();
        Transform transform = transformStore.getComponent(entity).get();
        // TODO: compute updates based on force and mass
//        Optional<RigidBody> rigidBody = entity.getComponent(RigidBody.class);

//        Vector3 force = dynamics.force;
        Vector3 velocity = dynamics.velocity;
        Vector3 acceleration = dynamics.acceleration;
        float angularVelocity = dynamics.angularVelocity;
        Vector3 position = transform.position;

        Physics.applyAcceleration(dt, velocity, acceleration);
        Physics.applyVelocity(dt, position, velocity);
        Physics.applyAngularVelocity(dt, angularVelocity, transform);

        // reset force
        dynamics.force = Vector3.zeros();
    }
}
