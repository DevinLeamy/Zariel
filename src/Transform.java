public class Transform {
    private float x;
    private float y;
    private float z;
    private float rotationX;
    private float rotationY;
    private float rotationZ;
    private float scaleX;
    private float scaleY;

    public Transform(float x, float y) {
        this.x = x;
        this.y = y;
        this.z = 0;
        this.rotationX = 0;
        this.rotationY = 0;
        this.rotationZ = 0;
        this.scaleX = 1;
        this.scaleY = 1;
    }

    public Matrix3 rotationMatrix() {
        return Matrix3.rotate(Matrix3.zero(), rotationX, rotationY, rotationZ);
    }

    public void rotate(float x, float y, float z) {
        this.rotationX += x;
        this.rotationX %= 2 * Math.PI;

        this.rotationY += y;
        this.rotationY %= 2 * Math.PI;

        this.rotationZ += z;
        this.rotationZ %= 2 * Math.PI;
    }
}
