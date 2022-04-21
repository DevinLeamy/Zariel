package ecs;

public class EntityManager {
    public static EntityManager instance = new EntityManager();

    private Entity[] entities;

    private EntityManager() {
        entities = new Entity[ECSConfig.MAX_ENTITY_COUNT];
    }

    public Entity addEntity(Entity entity) {
        entities[entity.id] = entity;
        return entity;
    }

    public void removeEntity(Entity entity) {
        entities[entity.id] = null;
    }

    public void removeAllEntities() {
        entities = new Entity[ECSConfig.MAX_ENTITY_COUNT];
    }
}
