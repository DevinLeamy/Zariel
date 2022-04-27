package engine.systems;

import engine.components.*;
import engine.ecs.ComponentRegistry;
import engine.ecs.ComponentStore;
import engine.ecs.Entity;
import engine.ecs.InstanceSystem;
import engine.main.Block;
import engine.main.BlockType;
import engine.main.Face;
import engine.World;
import engine.main.BoundingBox;
import math.Vector3;
import math.Vector3i;
import util.Utils;

import java.util.Optional;

public class TerrainCollisionResolutionSystem extends InstanceSystem {
    ComponentStore<RigidBody> rigidBodyStore = ComponentStore.of(RigidBody.class);
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);
    ComponentStore<TerrainCollision> terrainCollisionStore = ComponentStore.of(TerrainCollision.class);

    public TerrainCollisionResolutionSystem() {
        super(ComponentRegistry.getSignature(RigidBody.class, Dynamics.class, Transform.class, TerrainCollision.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        RigidBody rigidBody = rigidBodyStore.getComponent(entity).get();
        Transform transform = transformStore.getComponent(entity).get();
        TerrainCollision collision = terrainCollisionStore.getComponent(entity).get();
        Dynamics dynamics = entity.getComponent(Dynamics.class).get();

        if (rigidBody.objectType.equals("PLAYER")) {
            String face = collision.collisionFace.name;

            if (face.equals(Face.BOTTOM)) {
                transform.position.y = (float) Math.ceil(transform.position.y);
            } else {
                Transform prev = entity.getComponent(PlayerTag.class).get().previousTransform;
                Vector3 direction = transform.direction();
                Vector3 right = transform.right();
                Vector3 velocity = dynamics.velocity;

                float sideSlipAngle = Vector3.angleBetween(velocity, direction);
                float speed = velocity.len();

                // local to the car
                float vForward = (float) (Math.cos(sideSlipAngle) * speed);
                float vRight = (float) (Math.sin(sideSlipAngle) * speed);

                Vector3 vLocal = new Vector3(vRight, 0, vForward);
                Vector3 trans = new Vector3(1, 1, 1);

                switch (face) {
                    case Face.FRONT, Face.BACK -> trans = new Vector3(1, 1, -1);
                    case Face.LEFT, Face.RIGHT -> trans = new Vector3(-1, 1, 1);
                    case Face.TOP, Face.BOTTOM -> trans = new Vector3(1, -1, 1);
                }
                vLocal.scale(trans);

                dynamics.velocity = new Vector3(0, velocity.y, 0)
                                .add(Vector3.scale(right, vLocal.x)
                                .add(Vector3.scale(direction, vLocal.z)));

                transform.setPosition(prev.position.clone());
                transform.setRotation(prev.rotation.clone());
                transform.position.add(Vector3.scale(dynamics.velocity.clone().normalize(), 2f));
            }
        } else if (rigidBody.objectType.equals("BOMB")) {
            entity.addComponent(new DespawnTag());

            Optional<Block> maybeBlock = World.getInstance().getBlock(collision.location);

            if (maybeBlock.isEmpty()) {
                return;
            }

            BlockType explodedBlockType = maybeBlock.get().getBlockType();

            for (int i = 0; i < 4; ++i) {
                for (int j = 0; j < 4; ++j) {
                    for (int k = 0; k < 4; ++k) {
                        destroyBlock(Vector3i.add(collision.location, new Vector3i(i - 2, j - 2, k - 2)), Block.inActive());
                    }
                }
            }

            for (int i = 0; i < 30; ++i) {
                entityManager.addEntity(generateParticle(explodedBlockType, collision.location));
            }
        }

        entity.removeComponent(TerrainCollision.class);
    }

    private Entity generateParticle(BlockType explodedBlockType, Vector3i blockPosition) {
        Entity particle = new Entity();
        float size = 0.2f;
        particle.addComponent(new Transform(
            blockPosition.toVector3(),
            Utils.randVector3(),
            new Vector3(size, size, size)
        ));
        particle.addComponent(new Dynamics(
            Utils.randVector3().scale(3.0f).add(new Vector3(0, 10, 0)),
            Vector3.zeros()
        ));
        particle.addComponent(new RigidBody(
//            new engine.main.BoundingBox(size * 2, size * 2, size * 2),
                new BoundingBox(1, 1, 1),
                "PARTICLE"
        ));
        particle.addComponent(new GravityTag());
        particle.addComponent(new VoxelModel(new Block[][][] {{{new Block(true, explodedBlockType)}}}));
        particle.addComponent(new LifeTime(3));

        return particle;
    }

    private void destroyBlock(Vector3i blockLocation, Block newBlock) {
        World world = World.getInstance();
        // TODO: this should result in a chunk update
        Optional<Block> maybeBlock = world.getBlock(blockLocation);

        if (maybeBlock.isEmpty()) {
            return;
        }

        Block block = maybeBlock.get();
        block.setBlockType(newBlock.getBlockType());
        block.setActive(newBlock.isActive());
    }

}
