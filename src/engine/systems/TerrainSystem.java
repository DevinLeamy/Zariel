package engine.systems;

import engine.ecs.System;
import engine.World;

public class TerrainSystem extends System {
    public TerrainSystem() {
        super(0);
    }

    @Override
    public void update(float dt) {
        World.chunkManager.update(World.getPerspective());
    }
}
