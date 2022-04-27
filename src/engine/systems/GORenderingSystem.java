package engine.systems;

import engine.World;
import engine.components.Transform;
import engine.components.VoxelModel;
import engine.ecs.ComponentRegistry;
import engine.ecs.ComponentStore;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import engine.graphics.*;
import engine.main.Camera;
import engine.renderers.GameObjectRenderer;

public class GORenderingSystem extends InstanceSystem {
    private static GameObjectRenderer renderer = new GameObjectRenderer();

    World world = World.getInstance();

    ComponentStore<VoxelModel> voxelModelStore = ComponentStore.of(VoxelModel.class);
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);

    public GORenderingSystem() {
        super(ComponentRegistry.getSignature(VoxelModel.class, Transform.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        VoxelModel model = voxelModelStore.getComponent(entity).get();
        Transform transform = transformStore.getComponent(entity).get();

        Camera camera = world.getPerspective();
        Mesh mesh = model.mesh();

        renderer.setRenderContext(camera, transform);
        renderer.render(mesh);
    }
}
