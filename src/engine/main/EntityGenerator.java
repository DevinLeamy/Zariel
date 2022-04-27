package engine.main;

import engine.components.Dynamics;
import engine.components.GravityTag;
import engine.components.LifeTime;
import engine.components.RigidBody;
import engine.components.Transform;
import engine.components.VoxelModel;
import engine.ecs.Component;
import engine.ecs.Entity;
import engine.graphics.VoxelGeometry;
import math.Vector3;
import math.Vector3i;

public class EntityGenerator {
    public static Entity createEntity(Component... components) {
        Entity entity = new Entity();
        for (Component component : components) {
            entity.addComponent(component);
        }

        return entity;
    }

    public static Entity generateParticle(Vector3 position, Transform transform) {
        float size = 0.1f;
        return createEntity(
                new Transform(position, transform.direction(), new Vector3(size, size, size)),
                new Dynamics(transform.direction().clone().scale(-0.3f), Vector3.zeros()),
                new RigidBody(new BoundingBox(size, size, size), "PARTICLE"),
                new GravityTag(),
                new LifeTime(2),
                new VoxelModel(new Block[][][] {{{new Block(true, new BlockType(new Vector3i(10, 10, 10)))}}})
        );
    }

    public static Entity generateBomb(Transform transform) {
        return createEntity(
                new Transform(
                        Vector3.add(transform.direction(), transform.position).add(Transform.up).add(Transform.up),
                        new Vector3(0, transform.rotation.y, 0),
                        new Vector3(1/12f, 1/12f, 1/12f)
                ),
                new GravityTag(),
                new VoxelModel(VoxelGeometry.loadFromFile("res/voxels/bomb.vox").voxels),
                new Dynamics(transform.direction().normalize().scale(10), Vector3.zeros()),
                new RigidBody(new BoundingBox(1, 1, 1), "BOMB")
        );
    }
}
