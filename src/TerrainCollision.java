import ecs.Component;
import math.Vector3i;

public class TerrainCollision implements Component {
    public Vector3i location;
    // face of the object that collided with the terrain
    public Face collisionFace;

    public TerrainCollision(Vector3i location, Face collisionFace) {
        this.location = location;
        this.collisionFace = collisionFace;
    }
}
