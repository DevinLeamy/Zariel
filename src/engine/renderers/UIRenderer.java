package engine.renderers;

import engine.graphics.MeshGenerator;
import engine.main.BoundingBox2D;
import engine.ui.UIContainer;
import engine.ui.UIElement;
import engine.ui.UITextureElement;
import math.Matrix4;

public class UIRenderer {
    TexturedQuadRenderer texturedQuadRenderer;

    public UIRenderer() {
        this.texturedQuadRenderer = new TexturedQuadRenderer();
    }

    public void render(UIContainer container) {
        BoundingBox2D containerWindow = container.boundingContainer();
        for (UIElement element : container.getChildren()) {
            render(element, containerWindow);
        }
    }

    public void render(UIContainer container, BoundingBox2D parentWindow) {
        BoundingBox2D containerWindow = container.boundingContainer(parentWindow);
        for (UIElement element : container.getChildren()) {
            render(element, containerWindow);
        }
    }

    public void render(UIElement element, BoundingBox2D parentWindow) {
        if (element.getClass() == UITextureElement.class) {
            render((UITextureElement) element, parentWindow);
        }
    }

    public void render(UITextureElement textureElement, BoundingBox2D parentWindow) {
        texturedQuadRenderer.setRenderContext(
                textureElement.textureHandle(),
                textureElement.screenCoordMatrix(parentWindow),
                Matrix4.genScalingMatrix(textureElement.size().x, textureElement.size().y, 1)
        );
        texturedQuadRenderer.render(MeshGenerator.generateQuadMesh());

        BoundingBox2D containerWindow = textureElement.boundingContainer(parentWindow);
        for (UIElement element : textureElement.getChildren()) {
            render(element, containerWindow);
        }
    }
}
