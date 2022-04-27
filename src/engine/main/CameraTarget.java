package engine.main;

import engine.ecs.Component;
import math.Vector3;

public class CameraTarget implements Component {
    public Vector3 targetOffset;

    CameraTarget(Vector3 targetOffset) {
        this.targetOffset = targetOffset;
    }
}
