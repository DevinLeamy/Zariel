import math.Vector3;
import math.Matrix4;

public class Transform {
    public Vector3 position;
    public Vector3 rotation;
    public Vector3 scale;

    public Transform(Vector3 position, Vector3 rotation, Vector3 scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale    = scale;
    }

    public Matrix4 toMatrix() {
        Matrix4 rotationM    = Matrix4.genRotationMatrix(rotation.x, rotation.y, rotation.z);
        Matrix4 scalingM     = Matrix4.genScalingMatrix(scale.x, scale.y, scale.z);
        Matrix4 translationM = Matrix4.genTranslationMatrix(position.x, position.y, position.z);

        Matrix4 transform = Matrix4.identity();
        transform.mult(translationM);
        transform.mult(rotationM);
        transform.mult(scalingM);

        return transform;
    }

    public void rotate(float x, float y, float z) {
        rotation.x += x;
        rotation.x %= 2 * Math.PI;

        rotation.y += y;
        rotation.y %= 2 * Math.PI;

        rotation.z += z;
        rotation.z %= 2 * Math.PI;
    }
}
