import ecs.Component;

public class DebugCameraConfig implements Component {
    public float pitch;
    public float yaw;

    public DebugCameraConfig() {
        this.pitch  = 0.0f;
        this.yaw    = (float) -Math.PI / 2 + 0.01f;
    }
}
