import math.Vector3;
import math.Matrix4;

public class Transform {
    final public Vector3 up = new Vector3(0, 1, 0);

    public Vector3 position;
    public Vector3 rotation;
    public Vector3 scale;
    private float pitch;
    private float yaw;

    public Transform(Vector3 position, Vector3 rotation, Vector3 scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale    = scale;
        this.pitch    = 0.0f; // centered
        this.yaw      = (float) -Math.PI / 2 + 0.01f; // centered
    }

    public Transform(Vector3 position) {
        this(position, Vector3.zeros(), new Vector3(1, 1, 1));
    }

    public Vector3 computeTarget() {
        Vector3 orientation = new Vector3(
                (float) Math.cos(yaw) * (float) Math.cos(pitch),
                (float) Math.sin(pitch),
                (float) Math.sin(yaw) * (float) Math.cos(pitch)
        ).normalize();
        return Vector3.add(position, orientation);
    }

    public Vector3 getForwardAxis() {
        Vector3 from = position;
        Vector3 to = computeTarget();
        return Vector3.sub(to, from).normalize();
    }

    public Matrix4 modelMatrix() {
        Matrix4 rotationM    = Matrix4.genRotationMatrix(rotation.x, rotation.y, rotation.z);
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

    public void moveUp(float mag) {
        position.add(Vector3.scale(up, mag));
    }

    private Vector3 getRightAxis() {
        return Vector3.cross(getForwardAxis(), up);
    }

    public void moveRight(float mag) {
        position.add(Vector3.scale(getRightAxis(), mag));
    }

    public void moveForward(float mag) {
        position.add(Vector3.scale(getForwardAxis(), mag));
    }

    public void updatePitch(float mag) {
        float diff = 0.01f;
        pitch = Utils.clamp((float) -Math.PI / 2 + diff, (float) Math.PI / 2 - diff, pitch + mag);
    }

    public void updateYaw(float mag) {
        yaw += mag;
    }

    // TODO: updates pitch and yaw so the lookAt vector points at the point
//    public void lookAt(Vector3 point) {
//        this.forward = Vector3.sub(point, position);
//    }
}
