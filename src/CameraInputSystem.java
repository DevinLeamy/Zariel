import ecs.System;

public class CameraInputSystem extends System {
    final private float scrollSensitivity = 0.03f;

    Camera camera = World.getInstance().getPerspective();
    Controller controller = Controller.getInstance();

    public CameraInputSystem() {
        super(0);
    }

    @Override
    public void update(float dt) {
        handleScrollUpdate(controller.pollScrollDelta());
    }

    private void handleScrollUpdate(float scrollDelta) {
        camera.fov += scrollDelta * scrollSensitivity;
        camera.fov = Utils.clamp(0.1f * (float) Math.PI, 0.7f * (float) Math.PI, camera.fov);
    }
}
