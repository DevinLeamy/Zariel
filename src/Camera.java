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
        this.aspect = aspect;
        this.pitch  = 0.0f; // centered
        this.yaw    = (float) Math.PI / 2; // centered
        this.ncp    = 1.0f;  // near clip plane (NCP)
        this.fcp    = 20.0f; // far clip plane (FCP)
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
        return new Vector3(
                (float) Math.cos(yaw) * (float) Math.cos(pitch),
                (float) Math.sin(pitch),
                (float) Math.sin(yaw) * (float) Math.cos(pitch)
        );
    }

    public Matrix4 viewMatrix() {
        Vector3 direction = this.calculateDirection(yaw, pitch).normalize();
        return lookAt(position, Vector3.add(position, direction) , new Vector3(0, 1, 0));
    }

    public void updatePitch(float mag) {
        float diff = 0.01f;
        pitch += mag;
        pitch = Utils.clamp((float) -Math.PI / 2 + diff, (float) Math.PI / 2 - diff, pitch);

    }

    public void updateYaw(float mag) {
        float diff = 0.01f;
        yaw += mag;
        yaw = Utils.clamp(diff, (float) Math.PI - diff, yaw);
    }


    private Vector3 getForwardAxis() {
        Vector3 from = position;
        Vector3 to = calculateDirection(yaw, pitch);
        return Vector3.sub(from, to).normalize();
    }


    private Vector3 getRightAxis() {
        Vector3 tempUp = new Vector3(0, 1, 0);
        Vector3 forward = calculateDirection(yaw, pitch);
        return Vector3.cross(tempUp, forward);
    }

//    private Vector3 getTrueUpAxis() {
//        Vector3 rightAxis = getRightAxis();
//        return Vector3.cross(forward, rightAxis);
//    }


    public void moveRight(float mag) {
        position.add(Vector3.scale(getRightAxis(), mag));
    }

    public void moveForward(float mag) {
        Vector3 forward = getForwardAxis();
        position.add(Vector3.scale(forward, mag));
    }

//    public void moveUp(float mag) {
//        position.add(Vector3.scale(up, mag));
//    }

     public Matrix4 lookAt(Vector3 from, Vector3 to, Vector3 tempUp) {
         Vector3 forward = Vector3.sub(from, to).normalize();
         Vector3 side = Vector3.cross(forward, tempUp).normalize();
         Vector3 up = Vector3.cross(side, forward).normalize();

//        System.out.println("FORWARD: " + forward);
//        System.out.println("RIGHT: " + right);
//        System.out.println("UP: " + trueUp);

         Matrix4 m1 = new Matrix4(new float[][]{
                 {side.x, side.y, side.z, 0},
                 {up.x, up.y, up.z, 0},
                 {-forward.x, -forward.y, -forward.z, 0},
                 {0.0f, 0.0f, 0.0f, 1.0f}
         });
         Matrix4 m2 = Matrix4.genTranslationMatrix(-from.x, -from.y, -from.z);
         return Matrix4.mult(m1, m2);
     }

//    public static Matrix4 translate(Matrix4 m, Vector3 trans) {
//        m.m[3][0] += m.m[0][0] * trans.x + src.m10 * vec.y + src.m20 * vec.z;
//        dest.m31 += src.m01 * vec.x + src.m11 * vec.y + src.m21 * vec.z;
//        dest.m32 += src.m02 * vec.x + src.m12 * vec.y + src.m22 * vec.z;
//        dest.m33 += src.m03 * vec.x + src.m13 * vec.y + src.m23 * vec.z;
//    }
}
