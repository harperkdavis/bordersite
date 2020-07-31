package engine.math;

public class Vector3 {
    public float x, y, z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 a) {
        x += a.x;
        y += a.y;
        z += a.z;
        return this;
    }

    public Vector3 add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3 subtract(Vector3 a) {
        x -= a.x;
        y -= a.y;
        z -= a.z;
        return this;
    }

    public Vector3 subtract(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vector3 multiply(Vector3 a) {
        x *= a.x;
        y *= a.y;
        z *= a.z;
        return this;
    }

    public Vector3 multiply(float x, float y, float z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vector3 multiply(float a) {
        this.x *= a;
        this.y *= a;
        this.z *= a;
        return this;
    }

    public Vector3 divide(Vector3 a) {
        x /= a.x;
        y /= a.y;
        z /= a.z;
        return this;
    }

    public Vector3 divide(float x, float y, float z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public Vector3 divide(float a) {
        this.x /= a;
        this.y /= a;
        this.z /= a;
        return this;
    }

    public float mag() {
        return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public Vector3 norm() {
        return new Vector3(this).divide(mag());
    }

    public Vector3 normalize() {
        this.x /= mag();
        this.y /= mag();
        this.z /= mag();
        return this;
    }
}
