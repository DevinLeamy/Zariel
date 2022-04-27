package engine.ecs;

import engine.util.Utils;

import java.util.ArrayList;
import java.util.Optional;

public class Entity {
    private static ArrayList<Integer> ids = Utils.range(0, ECSConfig.MAX_ENTITY_COUNT);

    final public int id;
    public long signature;

    private int nextId() {
        int id = ids.get(ids.size() - 1);
        ids.remove(ids.size() - 1);

        return id;
    }

    public Entity() {
        this.id = nextId();
        this.signature = 0;
    }

    public <T extends Component> void addComponent(T component) {
        ComponentStore<T> store = (ComponentStore<T>) ComponentStore.of(component.getClass());
        store.setComponent(this, component);

        signature |= ComponentRegistry.getComponentId(component.getClass());
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        ComponentStore.of(componentClass).removeComponent(this);

        signature &= (~ComponentRegistry.getComponentId(componentClass));
    }

    public <T extends Component> Optional<T> getComponent(Class<T> componentClass) {
        return ComponentStore.of(componentClass).getComponent(this);
    }

    public void removeAllComponents() {
        ComponentStore.removeAllComponents(this);
    }

    public void dispose() {
        ids.add(id);
    }

}
