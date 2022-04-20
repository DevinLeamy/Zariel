import math.Vector3;

public class RigidBody {
//    private float mass;
    private Vector3 velocity;
    private Vector3 acceleration;
    private BoundingBox boundingBox;

    public RigidBody(Vector3 velocity, Vector3 acceleration, BoundingBox boundingBox) {
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.boundingBox = boundingBox;
    }
    public RigidBody(BoundingBox boundingBox) {
        this(Vector3.zeros(), Vector3.zeros(), boundingBox);
    }

    public Vector3 update(float dt, Vector3 position) {
        boundingBox.setOrigin(position.clone());

        Vector3 deltaPosition = Vector3.scale(velocity, dt);
        Vector3 deltaVelocity = Vector3.scale(acceleration, dt);
        velocity.add(deltaVelocity);

        return Vector3.add(position, deltaPosition);
    }

    // TODO: not sure how to implement this yet
//    public boolean colliding(BoundingBox other) {
//        return false;
//    }
//
//    public boolean collidingWithWorld() {
//        for (Vector3 vertex : boundingBox.vertices()) {
//
//        }
//    }
}
