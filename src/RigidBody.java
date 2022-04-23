import ecs.Component;

public class RigidBody implements Component {
    public BoundingBox boundingBox;
    public String objectType;

    public RigidBody(BoundingBox boundingBox, String objectType) {
        this.boundingBox = boundingBox;
        this.objectType = objectType;
    }
}
