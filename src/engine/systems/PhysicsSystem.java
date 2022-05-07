package engine.systems;

import engine.World;
import engine.components.Dynamics;
import engine.components.RigidBody;
import engine.components.Transform;
import engine.config.Config;
import engine.ecs.ComponentRegistry;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import engine.main.BoundingBox;
import engine.main.BoundingBox2D;
import math.Vector2;
import math.Vector3;

public class PhysicsSystem extends InstanceSystem {
    final private static float MASS = 1.0f;
    public PhysicsSystem() {
        super(ComponentRegistry.getSignature(RigidBody.class, Transform.class, Dynamics.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        RigidBody rigidBody = entity.getComponent(RigidBody.class).get();
        Transform transform = entity.getComponent(Transform.class).get();
        Dynamics dynamics = entity.getComponent(Dynamics.class).get();

        Vector3 fg = computeForceOfGravity(rigidBody, transform);

        Vector3 netForces = Vector3.zeros();
        netForces.add(fg);

        applyForces(dt, transform, rigidBody, dynamics, netForces);
        detectCollisions(transform, rigidBody);
//        resolveCollisions();
    }

    private void detectCollisions(Transform t, RigidBody rb) {

    }


    private void applyForces(float dt, Transform t, RigidBody rb, Dynamics d, Vector3 forces) {
        Vector3 acceleration = forces.scale(1 / MASS);

        d.velocity.add(acceleration.scale(dt));
        t.position.add(d.velocity.scale(dt));
    }

    private Vector3 computeForceOfGravity(RigidBody rb, Transform t) {
        if (onGround(rb.boundingBox, t)) {
            return Vector3.zeros();
        }
        return new Vector3(0, -Config.GRAVITY * MASS, 0);
    }

    private boolean onGround(BoundingBox bbox, Transform t) {
        // naive (assumes bounding box's lowest point is the object's height)
        float height = t.position.y;
        float groundLevel = World.chunkManager.groundLevel(new Vector2(t.position.x, t.position.z));

        return height == groundLevel;
    }
}
