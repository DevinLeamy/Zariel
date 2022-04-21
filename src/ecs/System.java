package ecs;

abstract public class System {
    final private long signature;

    public System(long signature) {
        this.signature = signature;
    }

    abstract void update(EntityManager ecs, float dt);
}
