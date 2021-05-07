package engine.math;

public class Vector4f {

    private float x, y, z, w;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector4f other) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = other.getZ();
        this.w = other.getW();
    }

    public Vector4f(Vector3f other, float w) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = other.getZ();
        this.w = w;
    }

    /*
    (x*a + y*e + z*i + w*m;
     x*b + y*f + z*j + w*n;
     x*c + y*g + z*k + w*o;
     x*d + y*h + z*l + w*p)
     */

    public void multiply(Matrix4f matrix) {

        float newX = x * matrix.get(0, 0) + y * matrix.get(0, 1) + z * matrix.get(0, 2) + w * matrix.get(0, 3);
        float newY = x * matrix.get(1, 0) + y * matrix.get(1, 1) + z * matrix.get(1, 2) + w * matrix.get(1, 3);
        float newZ = x * matrix.get(2, 0) + y * matrix.get(2, 1) + z * matrix.get(2, 2) + w * matrix.get(2, 3);
        float newW = x * matrix.get(3, 0) + y * matrix.get(3, 1) + z * matrix.get(3, 2) + w * matrix.get(3, 3);

        this.x = newX;
        this.y = newY;
        this.z = newZ;
        this.w = newW;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }
}
