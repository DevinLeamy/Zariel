package engine.main;

import engine.ecs.Component;

public class LifeTime implements Component {
    Runnable onLifeTimeOver;
    float timeRemaining;

    public LifeTime(float timeRemaining) {
        this(timeRemaining, () -> {});
    }

    public LifeTime(float timeRemaining, Runnable onLifeTimeOver) {
        this.timeRemaining = timeRemaining;
        this.onLifeTimeOver = onLifeTimeOver;
    }
}
