import math.Matrix3;
import math.Vector3;

public class Transform {
    private Vector3 position;
    private Vector3 rotation;
    private Vector3 scale;

    public Transform(Vector3 position, Vector3 rotation, Vector3 scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale    = scale;
    }

    public Matrix3 toMatrix() {
        Matrix3 rotationM = Matrix3.genRotationMatrix(rotation.v0, rotation.v1, rotation.v2);
        Matrix3 scalingM  = Matrix3.genScalingMatrix(scale.v0, scale.v1, scale.v2);

        // rotate by rotationM and then scale by scaleM
        return Matrix3.mult(scalingM, rotationM);
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
