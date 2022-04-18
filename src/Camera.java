import math.Matrix3;
import math.Matrix4;
import math.Vector3;

public class Camera {
    public Transform transform;
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
        this.transform = new Transform(position);
        this.fov    = fov;
        this.aspect = aspect; // aspect;
        this.ncp    = 0.01f;  // near clip plane (NCP)
        this.fcp    = 500.0f; // far clip plane (FCP)
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

    public static Matrix4 lookAt(Vector3 from, Vector3 to, Vector3 tempUp) {
         Vector3 forward = Vector3.sub(to, from).normalize(); // -z axis
         Vector3 right = Vector3.cross(forward, tempUp).normalize(); // +x axis
         Vector3 up = Vector3.cross(right, forward).normalize(); // (0, 1, 0)

         Matrix4 m1 = new Matrix4(new float[][]{
                 {right.x,     right.y,     right.z,     0.0f},
                 {up.x,       up.y,       up.z,       0.0f},
                 {forward.x,  forward.y,  forward.z,  0.0f},
                 {0.0f,       0.0f,       0.0f,       1.0f}
         });
         Matrix4 m2 = Matrix4.genTranslationMatrix(-from.x, -from.y, -from.z);
         return Matrix4.mult(m1, m2);
    }

    public Frustum getViewFrustum() {
        return new Frustum(fov, aspect, transform.position, transform.computeTarget(), new Vector3(0, 1, 0), ncp, fcp);
    }
}
