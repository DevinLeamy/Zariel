package engine.ecs;

import java.util.ArrayList;
import java.util.Arrays;

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

    public ArrayList<Entity> queryEntities(long signature) {
        return new ArrayList<>(
            Arrays
                .stream(entities)
                .filter(entity -> entity != null && (entity.signature & signature) == signature)
                .toList()
        );
    }

    public void removeEntity(Entity entity) {
        entities[entity.id] = null;
        entity.dispose();
    }

    public void removeAllEntities() {
        for (Entity entity : entities) {
            if (entity != null) {
                entity.dispose();
            }
        }
        entities = new Entity[ECSConfig.MAX_ENTITY_COUNT];
    }
}
