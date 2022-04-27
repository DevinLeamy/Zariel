package engine.main;

import engine.ecs.ComponentRegistry;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;

public class AnimationSystem extends InstanceSystem {
    public AnimationSystem() {
        super(ComponentRegistry.getSignature(Animation.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        Animation animation = entity.getComponent(Animation.class).get();

        entity.removeComponent(VoxelModel.class);
        entity.addComponent(new VoxelModel(animation.frame()));

        animation.next();
    }
}
