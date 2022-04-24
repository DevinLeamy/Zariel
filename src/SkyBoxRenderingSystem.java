import ecs.System;

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
