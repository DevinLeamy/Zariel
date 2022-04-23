import ecs.Component;
import math.Vector3i;

public class TerrainCollision implements Component {
    public Vector3i location;

    public TerrainCollision(Vector3i location) {
        this.location = location;
    }
}
