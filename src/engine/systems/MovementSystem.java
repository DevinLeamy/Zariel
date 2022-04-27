package engine.systems;

import engine.components.Dynamics;
import engine.components.Transform;
import engine.ecs.ComponentRegistry;
import engine.ecs.ComponentStore;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import engine.physics.Physics;
import math.Vector3;

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
//        Optional<engine.main.components.RigidBody> rigidBody = entity.getComponent(engine.main.components.RigidBody.class);

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
