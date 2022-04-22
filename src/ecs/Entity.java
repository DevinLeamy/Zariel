package ecs;

import java.lang.Class;
import java.util.Optional;


public class Entity {
    private static int NEXT_ID = 0;

    final public int id;
    public long signature;

    public Entity() {
        this.id = NEXT_ID++;
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

}
