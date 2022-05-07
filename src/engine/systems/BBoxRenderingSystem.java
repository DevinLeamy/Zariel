package engine.systems;

import engine.World;
import engine.components.RigidBody;
import engine.components.Transform;
import engine.ecs.ComponentRegistry;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import engine.main.BoundingBox;
import engine.main.Camera;
import engine.main.Cube;
import engine.main.Debug;
import engine.renderers.BoundingBoxRenderer;

public class BBoxRenderingSystem extends InstanceSystem {
    private static BoundingBoxRenderer renderer = new BoundingBoxRenderer();

    public BBoxRenderingSystem() {
        super(ComponentRegistry.getSignature(Transform.class, RigidBody.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        Transform transform = entity.getComponent(Transform.class).get();
        BoundingBox boundingBox = entity.getComponent(RigidBody.class).get().boundingBox;

        Transform boundingBoxTransform = new Transform(
                transform.position,
                transform.rotation,
                boundingBox.dimensions()
        );

        Camera camera = World.getPerspective();

        if (Debug.boundingBox) {
            renderer.setRenderContext(camera, boundingBoxTransform);
            renderer.render(Cube.mesh);
        }
    }
}
