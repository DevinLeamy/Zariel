import ecs.Component;
import math.Vector3;
import math.Vector3i;

import java.util.ArrayList;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;

public class DebugCameraConfig implements Component {
    public float pitch;
    public float yaw;

    public DebugCameraConfig() {
        this.pitch  = 0.0f;
        this.yaw    = (float) -Math.PI / 2 + 0.01f;
    }
}
