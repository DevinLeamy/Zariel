import math.Matrix3;
import math.Matrix4;
import math.Vector3;

public class Camera {
    Vector3 position;
    Vector3 forward;
    Vector3 up;
    Vector3 right;
    float fov;
    float aspect;
    float ncp;
    float fcp;

    /**
     * @param fov:      Field of view in radians (0, PI)
     * @param aspect:   Aspect ratio of our window
     * @param position: Position of the camera in world space
     * @param forward:  The vector going straight through the "eye" of the camera
     * @param up:       The vector going up through the top of the camera
     */
    public Camera(float fov, float aspect, Vector3 position, Vector3 forward, Vector3 up) {
        this.position = position;
        this.forward  = forward.normalize();
        this.up       = up.normalize();
        this.right    = Vector3.cross(up, forward).normalize();
        this.fov      = fov;
        this.aspect   = aspect;
        this.ncp      = 1.0f;  // near clip plane (NCP)
        this.fcp      = 10.0f; // far clip plane (FCP)
    }

    public Matrix4 projectionMatrix() {
        // Note: z is copied into w for perspective division
        // res: https://ogldev.org/www/tutorial12/tutorial12.html
        float inverseTanHalfFov = (float) (1 / Math.tan(fov / 2));
        return new Matrix4(new float[][] {
            {inverseTanHalfFov / aspect, 0,                 0,                          0                            },
            {0,                          inverseTanHalfFov, 0,                          0                            },
            {0,                          0,                 (-fcp - ncp) / (ncp - fcp), (2 * fcp * ncp) / (ncp - fcp)},
            {0,                          0,                 1,                          0                            }
        });
    }

    /**
     * Think "nodding your head"
     * Rotate about right-axis
     * @param theta: Change in pitch in radians
     */
    public void pitch(float theta) {
        Matrix3 pitchM = Matrix3.genRotationMatrix(right, -theta);

        forward = Matrix3.mult(pitchM, forward).normalize();
        up      = Vector3.cross(forward, right).normalize();
    }

    /**
     * Think "rolling a log"
     * Rotate around forward-axis
     * @param theta: Change in roll in radians
     */
    public void roll(float theta) {
        Matrix3 rollM = Matrix3.genRotationMatrix(forward, theta);

        right = Matrix3.mult(rollM, right).normalize();
        up      = Vector3.cross(forward, right).normalize();
    }

    /**
     * Think "automatic watering"
     * Rotate around up-axis
     * @param theta: Change in yaw in radians
     */
    public void yaw(float theta) {
        Matrix3 yawM = Matrix3.genRotationMatrix(up, theta);

        forward = Matrix3.mult(yawM, forward).normalize();
        right   = Vector3.cross(up, forward).normalize();
    }

    public void moveRight(float mag) {
        position.add(Vector3.scale(right, mag));
    }

    public void moveForward(float mag) {
        position.add(Vector3.scale(forward, mag));
    }

    public void moveUp(float mag) {
        position.add(Vector3.scale(up, mag));
    }

    public Matrix4 viewMatrix() {
        // (word -> camera) matrix
        // Note: transpose of the (camera -> world) matrix
        return new Matrix4(new float[][] {
            {right.x,   right.y,   right.z,   -position.x},
            {up.x,      up.y,      up.z,      -position.y},
            {forward.x, forward.y, forward.z, -position.z},
            {0.0f,      0.0f,      0.0f,      1.0f        }
        });
    }
}
