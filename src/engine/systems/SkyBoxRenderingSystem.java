package engine.systems;

import engine.ecs.System;
import engine.main.SkyBox;
import engine.World;
import engine.renderers.SkyBoxRenderer;

public class SkyBoxRenderingSystem extends System {
    SkyBoxRenderer renderer;

    public SkyBoxRenderingSystem() {
        super(0);
        renderer = new SkyBoxRenderer();
    }

    @Override
    public void update(float dt) {
        SkyBox skyBox = World.skyBox;
        renderer.setRenderContext(World.getPerspective(), skyBox.texture());
        renderer.render(skyBox.mesh());
    }
}
