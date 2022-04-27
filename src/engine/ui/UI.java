package engine.ui;

import java.util.ArrayList;

public class UI {
    private ArrayList<UIElement> elements;

    public UI() {
        elements = new ArrayList<>();
    }

    public void addElement(UIElement element) {
        elements.add(element);
    }

    public void removeElement(int elementId) {
        // .dispose() removed UI element
        elements.removeIf(element -> element.id == elementId);
    }

    public void render() {
        for (UIElement element : elements) {
            element.render();
        }
    }

    public void clear() {
        for (UIElement element : elements) {
            element.dispose();
        }

        elements.clear();
    }
}
