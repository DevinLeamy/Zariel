import math.Matrix4;
import math.Vector3;

public class Camera {
    public Transform transform;
    float fov;
    float aspect;
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

    public Matrix4 prospectiveProjectionMatrix() {
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

    public Matrix4 projectionMatrix() {
        if (Config.orthographic) {
            return orthographicProjectionMatrix();
        }
        return prospectiveProjectionMatrix();
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
        return new Frustum(fov, aspect, transform.position, Vector3.add(transform.position, transform.direction()), new Vector3(0, 1, 0), ncp, fcp);
    }

    public Matrix4 orthographicProjectionMatrix() {
        int width = (int) (aspect * 20);
        int height = 20;
        int depth = 1000;

        /**
         * Note: We create our view box around vec3(0, 0, 0) because we are working in
         * camera space where the camera is located at the origin.
         */

        Vector3 pos = Vector3.zeros();
        float left = pos.x - width/2.0f;
        float right = left + width;
        float bottom = pos.y - height/2.0f;
        float top = bottom + height;
        float near = pos.z + depth/2.0f;
        float far = near - depth;

        return new Matrix4(new float[][] {
                {2 / (right - left), 0,                  0,                 -((left + right) / (right - left))},
                {0,                  2 / (top - bottom), 0,                 -((top + bottom) / (top - bottom))},
                {0,                  0,                  -2 / (far - near), -((far + near) / (far - near))    },
                {0,                  0,                  0,                 1                                 }
        });
    }
}
