import ecs.Component;
import math.Vector3;

public class Scale extends Vector3 implements Component {
    public Scale(float x, float y, float z) {
        super(x, y, z);
    }
}
