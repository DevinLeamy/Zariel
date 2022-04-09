import math.Matrix3;
import math.Matrix4;
import math.Vector3;

public class Camera {
    Vector3 position;

    float fov;
    float aspect;
    float pitch;
    float yaw;
    float ncp;
    float fcp;

    /**
     * @param fov:      Field of view in radians (0, PI)
     * @param aspect:   Aspect ratio of our window
     * @param position: Position of the camera in world space
     */
    public Camera(float fov, float aspect, Vector3 position) {
        this.position = position;
        this.fov    = fov;
        this.aspect = 1; // aspect;
        this.pitch  = 0.0f; // centered
        this.yaw    = (float) -Math.PI / 2 + 0.01f; // centered
        this.ncp    = 0.01f;  // near clip plane (NCP)
        this.fcp    = 1000.0f; // far clip plane (FCP)
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

    private Vector3 calculateDirection(float yaw, float pitch) {
        Vector3 orientation = new Vector3(
                (float) Math.cos(yaw) * (float) Math.cos(pitch),
                (float) Math.sin(pitch),
                (float) Math.sin(yaw) * (float) Math.cos(pitch)
        ).normalize();
        return Vector3.add(position, orientation);
    }

    public Matrix4 viewMatrix() {
        return lookAt(position, calculateDirection(yaw, pitch), new Vector3(0, 1, 0));
    }

    public void updatePitch(float mag) {
        float diff = 0.01f;
        pitch = Utils.clamp((float) -Math.PI / 2 + diff, (float) Math.PI / 2 - diff, pitch + mag);
    }

    public void updateYaw(float mag) {
        yaw += mag;
    }

    private Vector3 getForwardAxis() {
        Vector3 from = position;
        Vector3 to = calculateDirection(yaw, pitch);
        return Vector3.sub(from, to).normalize();
    }


    private Vector3 getLeftAxis() {
        Vector3 tempUp = new Vector3(0, 1, 0);
        Vector3 forward = getForwardAxis();
        return Vector3.cross(tempUp, forward);
    }

    public void moveLeft(float mag) {
        position.add(Vector3.scale(getLeftAxis(), mag));
    }

    public void moveForward(float mag) {
        Vector3 forward = getForwardAxis();
        position.add(Vector3.scale(forward, mag));
    }

    public void moveUp(float mag) {
        position.add(new Vector3(0, 1, 0).scale(mag));
    }

     public Matrix4 lookAt(Vector3 from, Vector3 to, Vector3 tempUp) {
         Vector3 forward = Vector3.sub(from, to).normalize();
         Vector3 side = Vector3.cross(forward, tempUp).normalize();
         Vector3 up = Vector3.cross(side, forward).normalize();

         // TODO: why does the forward vector how to be inverted?
         Matrix4 m1 = new Matrix4(new float[][]{
                 {side.x,     side.y,     side.z,     0.0f},
                 {up.x,       up.y,       up.z,       0.0f},
                 {-forward.x,  -forward.y,  -forward.z,  0.0f},
                 {0.0f,       0.0f,       0.0f,       1.0f}
         });
         Matrix4 m2 = Matrix4.genTranslationMatrix(-from.x, -from.y, -from.z);
         return Matrix4.mult(m1, m2);
     }
}
