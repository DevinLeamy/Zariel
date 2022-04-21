package ecs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComponentStore<T extends Component> {
    private static Map<String, ComponentStore<? extends Component>> stores = new HashMap<>();

    final private T[] components;

    private ComponentStore() {
        this.components = (T[]) new Object[ECSConfig.MAX_ENTITY_COUNT];
    }

    public static <C extends Component> ComponentStore<C> of(Class<C> componentClass) {
        String key = componentClass.toString();
        if (!stores.containsKey(key)) {
            stores.put(key, new ComponentStore<C>());
        }

        return (ComponentStore<C>) stores.get(key);
    }

    public Optional<T> getComponent(Entity entity) {
        if (components[entity.id] == null) {
            return Optional.empty();
        }
        return Optional.of(components[entity.id]);
    }

    // returns the removed component
    public Optional<T> removeComponent(Entity entity) {
        Optional<T> removedComponent = getComponent(entity);
        components[entity.id] = null;

        return removedComponent;
    }

    public void setComponent(Entity entity, T component) {
        components[entity.id] = component;
    }

    public static void removeAllComponents(Entity entity) {
        for (String key : stores.keySet()) {
            stores.get(key).removeComponent(entity);
        }
    }
}
