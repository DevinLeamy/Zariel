import ecs.Component;
import math.Vector3i;

public class TerrainCollision implements Component {
    public Vector3i location;
    public boolean ground;

    public TerrainCollision(Vector3i location, boolean ground) {
        this.location = location;
        this.ground = ground;
    }
}
