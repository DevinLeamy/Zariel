import ecs.ComponentRegistry;
import ecs.ComponentStore;
import ecs.Entity;
import math.Vector3;
import math.Vector3i;

import java.util.Optional;

public class TerrainCollisionResolutionSystem extends InstanceSystem {
    ComponentStore<RigidBody> rigidBodyStore = ComponentStore.of(RigidBody.class);
    ComponentStore<Transform> transformStore = ComponentStore.of(Transform.class);
    ComponentStore<TerrainCollision> terrainCollisionStore = ComponentStore.of(TerrainCollision.class);

    public TerrainCollisionResolutionSystem() {
        super(ComponentRegistry.getSignature(RigidBody.class, Transform.class, TerrainCollision.class), 0);
    }

    @Override
    protected void update(float dt, Entity entity) {
        RigidBody rigidBody = rigidBodyStore.getComponent(entity).get();
        Transform transform = transformStore.getComponent(entity).get();
        TerrainCollision collision = terrainCollisionStore.getComponent(entity).get();

        if (rigidBody.objectType.equals("PLAYER")) {
           // move the player up until they no longer collide
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
    }

    private Entity generateParticle(BlockType explodedBlockType, Vector3i blockPosition) {
        Entity particle = new Entity();
        particle.addComponent(new Transform(
            blockPosition.toVector3(),
            Utils.randVector3(),
            new Vector3(0.2f, 0.2f, 0.2f)
        ));
        particle.addComponent(new Dynamics(
            Utils.randVector3().scale(3.0f).add(new Vector3(0, 10, 0)),
            Vector3.zeros()
        ));
        particle.addComponent(new RigidBody(
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