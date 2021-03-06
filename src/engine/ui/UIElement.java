package engine.ui;

import engine.graphics.TextureLoader;
import engine.main.BoundingBox2D;
import math.Matrix4;
import math.Vector2;
import math.Vector3;
import math.Vector4;

import java.util.ArrayList;

import static engine.util.Utils.createRGB;

/**
 * Parent class for all UI elements
 */
public class UIElement {
    private static int NEXT_ID = 0;
    private static String BLANK_TEXTURE = "res/images/BLANK_ICON.png";

    // Normalized Device Coordinates
    final private static Vector2 CENTER_POSITION = new Vector2(0.5f, 0.5f);

    final public int id;
    Vector2 size;
    Vector2 position;
    Vector3 color;
    float alpha;
    int textureHandle;
    ArrayList<UIElement> children;
    UIElement parent;

    /**
     * @param size as a percentage of the parent
     * @param position as a percentage of the parent
     */
    public UIElement(Vector2 size, Vector2 position, UIElement parent) {
        this.id = NEXT_ID++;
        this.size = size;
        this.position = position;
        this.children = new ArrayList<>();
        this.textureHandle = TextureLoader.load2DTexture(BLANK_TEXTURE);
        this.color = createRGB(140, 140, 140);
        this.alpha = 1f;
        this.parent = parent;
    }

    public UIElement(Vector2 size, Vector2 position) {
        this(size, position, null);
    }

    public Vector2 size() {
        return size.clone();
    }

    public void setColor(Vector3 color) {
        this.color = color;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public Vector4 rgbaColor() {
        return new Vector4(color.x, color.y, color.z, alpha);
    }

    public Vector3 color() {
        return color;
    }

    public void setTextureHandle(int textureHandle) {
        this.textureHandle = textureHandle;
    }

    public void setTexture(String texturePath) {
        this.textureHandle = TextureLoader.load2DTexture(texturePath);
    }

    public int textureHandle() {
        return textureHandle;
    }

    public void centerOn(float x, float y) {
        Vector2 offset = Vector2.scale(size, 0.5f);
        position = Vector2.sub(CENTER_POSITION, offset);
    }

    public void addChild(UIElement element) {
        this.children.add(element);
        element.setParent(this);
    }

    private void setParent(UIElement parent) {
        this.parent = parent;
    }

    public void removeChild(int elementId) {
        this.children.removeIf(child -> child.id == elementId);
    }

    public ArrayList<UIElement> getChildren() {
        return children;
    }

    // NDC bounding container, relative to parent container
    public BoundingBox2D boundingContainer() {
        Matrix4 screenCoordM = screenCoordMatrix();

        Vector3 bottomLeftCoord = new Vector3(0, 0, 0);
        Vector3 topRightCoord = new Vector3(size.x, size.y, 0);

        Vector3 screenBottomLeft = Matrix4.mult(screenCoordM, bottomLeftCoord);
        Vector3 screenTopRight = Matrix4.mult(screenCoordM, topRightCoord);

        Vector2 ndcSize = new Vector2(
                screenTopRight.x - screenBottomLeft.x,
                screenTopRight.y - screenBottomLeft.y
        );
        Vector2 ndcPos = new Vector2(screenBottomLeft.x, screenBottomLeft.y);

        return new BoundingBox2D(ndcSize, ndcPos);
    }

    /**
     * @return - A matrix to convert a point from (0-1) coordinate to NDC, relative to a window
     */
    public Matrix4 screenCoordMatrix() {
        BoundingBox2D parentBBox = parent == null ? UI.MAIN_WINDOW_CONTAINER : parent.boundingContainer();
        float windowLowX = parentBBox.position.x;
        float windowLowY = parentBBox.position.y;
        float windowWidth = parentBBox.size.x;
        float windowHeight = parentBBox.size.y;

        float relativeTransX = windowWidth * position.x;
        float relativeTransY = windowHeight * position.y;

        Matrix4 fillWindowMatrix = Matrix4.genScalingMatrix(windowWidth / size.x, windowHeight / size.y, 1);
        Matrix4 scaleRelativeToWindow = Matrix4.genScalingMatrix(size.x, size.y, 1);
        Matrix4 translateToWindow = Matrix4.genTranslationMatrix(windowLowX, windowLowY, 0);
        Matrix4 translateInsideOfWindow = Matrix4.genTranslationMatrix(relativeTransX, relativeTransY, 0);

        return Matrix4.mult(
                translateInsideOfWindow,
                translateToWindow,
                scaleRelativeToWindow,
                fillWindowMatrix
        );
    }

    public void dispose() {
        for (UIElement child : children) {
            child.dispose();
        }
    }
}
