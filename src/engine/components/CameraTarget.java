package engine.components;

import engine.ecs.Component;
import math.Vector3;

public class CameraTarget implements Component {
    public Vector3 targetOffset;

    public CameraTarget(Vector3 targetOffset) {
        this.targetOffset = targetOffset;
    }
}
