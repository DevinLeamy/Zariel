package ecs;

import ecs.Component;

import java.util.HashMap;
import java.util.Map;

public class ComponentRegistry {
    private static Map<String, Long> idRegistry = new HashMap<>();
    private static long nextComponentBit = 0;

    public static <C extends Component> long getComponentId(Class<C> componentClass) {
        String key = componentClass.toString();
        if (!idRegistry.containsKey(key)) {
            idRegistry.put(key, 1L << nextComponentBit);
            nextComponentBit += 1;
        }

        return idRegistry.get(key);
    }

    public static long getSignature(Class<? extends Component>... componentClasses) {
        long signature = 0;

        for (Class<? extends Component> componentClass : componentClasses) {
            signature |= getComponentId(componentClass);
        }

        return signature;
    }
}
