import ecs.Component;
import math.Vector3;

public class Position extends Vector3 implements Component {
    public Position(float x, float y, float z) {
        super(x, y, z);
    }
}
