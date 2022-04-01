import math.Matrix3;
import math.Matrix4;
import math.Vector3;

public class Camera {
    Vector3 position;
    Vector3 rotation;
    float fov;

    /**
     *
     * @param fov: Field of view in radians (0, PI)
     * @param position: position of the camera in world space
     * @param rotation: rotation of the camera in world space
     */
    public Camera(float fov, Vector3 position, Vector3 rotation) {
        this.position = position;
        this.rotation = rotation;
        this.fov      = fov;
    }

    public Matrix4 projectionMatrix() {
        float inverseTanHalfFov = (float) (1 / Math.tan(fov / 2));
        return new Matrix4(new float[][] {
            {inverseTanHalfFov, 0,                 0, 0},
            {0,                 inverseTanHalfFov, 0, 0},
            {0,                 0,                 1, 0},
            {0,                 0,                 1, 0} // z is copied into w for perspective division
        });
    }

    // inputs coordinates are in world space
    public Vector3 getForward(float x, float y, float z) {
        Vector3 forward = new Vector3(x, y, z);
        forward.normalize();

        return forward;
    }

    public Vector3 getUp() {
        Vector3 defaultYAxis = new Vector3(0, 1, 0);

        // move the point based on the cameras x and z rotations
        float rotationX   = rotation.v0;
        float rotationZ   = rotation.v2;
        Matrix3 rotationM = Matrix3.genRotationMatrix(rotationX, 0, rotationZ);

        Vector3 up = Matrix3.mult(rotationM, defaultYAxis);
        up.normalize();

        return up;
    }

    public Vector3 getRight(Vector3 forwardAxis, Vector3 upAxis) {
       Vector3 right = Vector3.cross(forwardAxis, upAxis);
       right.normalize();

       return right;
    }

    public Matrix4 cameraTranslationMatrix() {
        return Matrix4.genTranslationMatrix(-position.v0, -position.v1, -position.v2);
    }

    public Matrix4 viewMatrix() {
        // axis
        Vector3 forwardAxis = new Vector3(0, 0, 1);
        Vector3 upAxis = new Vector3(0, 1, 0);
        Vector3 rightAxis = new Vector3(1, 0, 0);
//        Vector3 forwardAxis = getForward(0, 0, 1);  // Z
//        Vector3 upAxis      = getUp();                       // Y
//        Vector3 rightAxis   = getRight(upAxis, forwardAxis); // X

        // (word -> camera) matrix
        // Note: transpose of the (camera -> world) matrix
        Matrix4 viewMatrix = new Matrix4(new float[][] {
            {rightAxis.v0, rightAxis.v1, rightAxis.v2, -position.v0},
            {upAxis.v0, upAxis.v1, upAxis.v2,          -position.v1},
            {forwardAxis.v0, forwardAxis.v1, forwardAxis.v2, -position.v2},
            {0, 0, 0, 1}
        });

        return viewMatrix;
    }

    public void update() {}

    public void handleInput() {

    }
}
