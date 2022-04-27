package engine.components;

import engine.ecs.Component;

public class LifeTime implements Component {
    public Runnable onLifeTimeOver;
    public float timeRemaining;

    public LifeTime(float timeRemaining) {
        this(timeRemaining, () -> {});
    }

    public LifeTime(float timeRemaining, Runnable onLifeTimeOver) {
        this.timeRemaining = timeRemaining;
        this.onLifeTimeOver = onLifeTimeOver;
    }
}
