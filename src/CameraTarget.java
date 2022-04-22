import ecs.Component;
import ecs.Entity;
import math.Vector3;

public class CameraTarget implements Component {
    public Vector3 targetOffset;

    CameraTarget(Vector3 targetOffset) {
        this.targetOffset = targetOffset;
    }
}
