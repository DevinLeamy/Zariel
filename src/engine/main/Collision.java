package engine.main;

import engine.ecs.Component;
import engine.ecs.Entity;

public class Collision implements Component {
    public Entity other;

    public Collision(Entity other) {
        this.other = other;
    }
}
