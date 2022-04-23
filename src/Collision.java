import ecs.Component;
import ecs.Entity;

public class Collision implements Component {
    public Entity other;

    public Collision(Entity other) {
        this.other = other;
    }
}
