package engine.main;

import engine.ecs.Component;

public class RigidBody implements Component {
    public BoundingBox boundingBox;
    public String objectType;
    public float mass;

    public RigidBody(BoundingBox boundingBox, String objectType, float mass) {
        this.boundingBox = boundingBox;
        this.objectType = objectType;
        this.mass = mass;
    }

    public RigidBody(BoundingBox boundingBox, String objectType) {
        this(boundingBox, objectType, 1);
    }
}
