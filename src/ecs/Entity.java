package ecs;

import java.lang.Class;
import java.util.Optional;


public class Entity {
    private static int NEXT_ID = 0;

    final public int id;
    final public long signature;

    public Entity() {
        this.id = NEXT_ID++;
        this.signature = 0;
    }

    public <T extends Component> void addComponent(T component) {
        ComponentStore<T> store = (ComponentStore<T>) ComponentStore.of(component.getClass());
        store.setComponent(this, component);
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        ComponentStore.of(componentClass).removeComponent(this);
    }

    public <T extends Component> Optional<T> getComponent(Class<T> componentClass) {
        return ComponentStore.of(componentClass).getComponent(this);
    }

    public void removeAllComponents() {
        ComponentStore.removeAllComponents(this);
    }

}
