package engine.systems;

import engine.ecs.System;
import engine.main.SkyBox;
import engine.World;

public class SkyBoxRenderingSystem extends System {
    World world = World.getInstance();

    public SkyBoxRenderingSystem() {
        super(0);
    }

    @Override
    public void update(float dt) {
        SkyBox skyBox = world.skyBox;
        skyBox.render(world.getPerspective());
    }
}
