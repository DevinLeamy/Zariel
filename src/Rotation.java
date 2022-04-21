import ecs.Component;
import math.Vector3;

public class Rotation extends Vector3 implements Component {
    public Rotation(float x, float y, float z) {
        super(x, y, z);
    }
}
