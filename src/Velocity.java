import ecs.Component;
import math.Vector3;

public class Velocity extends Vector3 implements Component {

    public Velocity(float x, float y, float z) {
        super(x, y, z);
    }
}
