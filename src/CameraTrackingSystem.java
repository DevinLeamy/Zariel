import ecs.*;
import math.Vector3;

public class CameraTrackingSystem extends InstanceSystem {
    public CameraTrackingSystem() {
        super(ComponentRegistry.getSignature(CameraTarget.class, Transform.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        Camera camera = World.getInstance().getPerspective();

        // the entity "is" the target
        Transform targetTransform = entity.getComponent(Transform.class).get();
        Vector3 targetOffset = entity.getComponent(CameraTarget.class).get().targetOffset;

        Vector3 offsetBack = Vector3.scale(targetTransform.direction(), targetOffset.z);
        Vector3 offsetUp   = Vector3.scale(targetTransform.up(), targetOffset.y);
        Vector3 offsetRight = Vector3.scale(targetTransform.right(), targetOffset.x);

        Vector3 newPosition = targetTransform.position.clone();
        newPosition.add(offsetBack);
                newPosition.add(offsetUp).add(offsetRight);

        camera.transform.position = newPosition;
        camera.lookAt(targetTransform.position);
    }
}
