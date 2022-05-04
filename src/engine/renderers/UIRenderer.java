package engine.renderers;

import engine.graphics.MeshGenerator;
import engine.ui.UIContainer;
import engine.ui.UIElement;
import math.Matrix4;

public class UIRenderer {
    TexturedQuadRenderer texturedQuadRenderer;

    public UIRenderer() {
        this.texturedQuadRenderer = new TexturedQuadRenderer();
    }

    public void render(UIContainer container) {
        for (UIElement element : container.getChildren()) {
            render(element);
        }
    }

    public void render(UIElement element) {
        texturedQuadRenderer.setRenderContext(
                element.textureHandle(),
                element.rgbaColor(),
                element.screenCoordMatrix(),
                Matrix4.genScalingMatrix(element.size().x, element.size().y, 1)
        );
        texturedQuadRenderer.render(MeshGenerator.generateQuadMesh());

        for (UIElement child: element.getChildren()) {
            render(child);
        }
    }
}
