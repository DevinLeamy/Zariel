package engine.systems;

import engine.controller.Controller;
import engine.ecs.System;
import engine.main.Camera;
import engine.World;
import engine.util.Utils;

public class CameraInputSystem extends System {
    final private float scrollSensitivity = 0.03f;

    public CameraInputSystem() {
        super(0);
    }

    @Override
    public void update(float dt) {
        Camera camera = World.getPerspective();
        handleScrollUpdate(Controller.pollScrollDelta(), camera);
    }

    private void handleScrollUpdate(float scrollDelta, Camera camera) {
        camera.fov += scrollDelta * scrollSensitivity;
        camera.fov = Utils.clamp(0.1f * (float) Math.PI, 0.7f * (float) Math.PI, camera.fov);
    }
}
