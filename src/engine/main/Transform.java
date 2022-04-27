package engine.main;

import engine.ecs.Component;
import math.Matrix3;
import math.Vector3;
import math.Matrix4;

public class Transform implements Component {
    final static public Vector3 up      = new Vector3(0, 1, 0);  // +y axis
    final static public Vector3 forward = new Vector3(0, 0, -1); // -z axis
    final static public Vector3 right   = new Vector3(1, 0, 0);  // +x axis

    public Vector3 position;
    public Vector3 scale;
    public Vector3 rotation;

    public Transform(Vector3 position, Vector3 rotation, Vector3 scale) {
        this.position = position;
        this.scale    = scale;
        this.rotation = rotation;
    }

    public Transform(Vector3 position) {
        this(position, Vector3.zeros(), new Vector3(1, 1, 1));
    }

    public Vector3 direction() {
        Matrix3 rotationMatrix = Matrix3.genRotationMatrix(rotation.x, rotation.y, rotation.z);
        return Matrix3.mult(rotationMatrix, Transform.forward).normalize();
    }

    /**
     * This is the right vector relative to the player's direction()
     */
    public Vector3 right() {
       return Vector3.cross(direction(), Transform.up).normalize();
    }

    /**
     * This is the up vector relative to the player's direction()
     */
    public Vector3 up() {
        return Vector3.cross(right(), direction()).normalize();
    }

    public Matrix4 modelMatrix() {
        // TODO: add pitch and roll
        Matrix4 rotationM    = Matrix4.genRotationMatrix(rotation.x, rotation.y, rotation.z); // (float) Math.PI * 0.5f);
        Matrix4 scalingM     = Matrix4.genScalingMatrix(scale.x, scale.y, scale.z);
        Matrix4 translationM = Matrix4.genTranslationMatrix(position.x, position.y, position.z);

        Matrix4 transform = Matrix4.identity();
        transform.mult(translationM);
        transform.mult(rotationM);
        transform.mult(scalingM);

        return transform;
    }

    public void translate(Vector3 translation) {
        position.add(translation);
    }
    public void rotate(Vector3 rotation) {
        this.rotation.add(rotation);
    }
    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }
    public void setPosition(Vector3 position) {
        this.position = position;
    }
}