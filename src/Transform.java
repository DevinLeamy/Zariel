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
        Matrix4 rotationM    = Matrix4.genRotationMatrix(rotation.v0, rotation.v1, rotation.v2);
        Matrix4 scalingM     = Matrix4.genScalingMatrix(scale.v0, scale.v1, scale.v2);
        Matrix4 translationM = Matrix4.genTranslationMatrix(position.v0, position.v1, position.v2);

        Matrix4 transform = Matrix4.identity();
        transform.mult(translationM);
        transform.mult(rotationM);
        transform.mult(scalingM);

        return transform;
    }

    public void rotate(float x, float y, float z) {
        rotation.v0 += x;
        rotation.v0 %= 2 * Math.PI;

        rotation.v1 += y;
        rotation.v1 %= 2 * Math.PI;

        rotation.v2 += z;
        rotation.v2 %= 2 * Math.PI;
    }
}
