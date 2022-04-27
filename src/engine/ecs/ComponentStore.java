package engine.ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComponentStore<T extends Component> {
    private static Map<String, ComponentStore<? extends Component>> stores = new HashMap<>();

    final private ArrayList<T> components;

    private ComponentStore() {
        this.components = new ArrayList<>(ECSConfig.MAX_ENTITY_COUNT);
        for (int i = 0; i < ECSConfig.MAX_ENTITY_COUNT; ++i) {
            this.components.add(null);
        }
    }

    public static <C extends Component> ComponentStore<C> of(Class<C> componentClass) {
        String key = componentClass.toString();
        if (!stores.containsKey(key)) {
            stores.put(key, new ComponentStore<C>());
        }

        return (ComponentStore<C>) stores.get(key);
    }

    public Optional<T> getComponent(Entity entity) {
        if (components.get(entity.id) == null) {
            return Optional.empty();
        }
        return Optional.of(components.get(entity.id));
    }

    // returns the removed component
    public Optional<T> removeComponent(Entity entity) {
        Optional<T> removedComponent = getComponent(entity);
        components.set(entity.id, null);

        return removedComponent;
    }

    public void setComponent(Entity entity, T component) {
        components.set(entity.id, component);
    }

    public static void removeAllComponents(Entity entity) {
        for (String key : stores.keySet()) {
            stores.get(key).removeComponent(entity);
        }
    }
}
