package engine.systems;

import engine.ecs.System;
import engine.World;

public class TerrainSystem extends System {
    World world = World.getInstance();

    public TerrainSystem() {
        super(0);
    }

    @Override
    public void update(float dt) {
        world.chunkManager.update(world.getPerspective());
    }
}
