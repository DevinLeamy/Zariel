import ecs.*;

public class CameraTrackingSystem extends InstanceSystem {
    public CameraTrackingSystem() {
        super(Component.CAMERA_TARGET | Component.TRANSFORM, 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        Camera camera = World.getInstance().getPerspective();

        // the entity "is" the target
        Transform targetTransform = entity.getComponent(Transform.class).get();
//        camera.lookAt(targetTransform.position);
    }
}
