package engine.math;

import java.util.Objects;

public class Vector3f {
    private float x, y, z;

    public static Vector3f zero() { return new Vector3f(0, 0, 0); }
    public static Vector3f one() { return new Vector3f(1, 1, 1); }
    public static Vector3f oneX() { return new Vector3f(1, 0, 0); }
    public static Vector3f oneY() { return new Vector3f(0, 1, 0); }
    public static Vector3f oneZ() { return new Vector3f(0, 0, 1); }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f other) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = other.getZ();
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f add(Vector3f a) {
        x += a.x;
        y += a.y;
        z += a.z;
        return this;
    }

    public Vector3f add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public static Vector3f add(Vector3f a, Vector3f b) {
        return new Vector3f(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public Vector3f subtract(Vector3f a) {
        x -= a.x;
        y -= a.y;
        z -= a.z;
        return this;
    }

    public Vector3f subtract(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public static Vector3f subtract(Vector3f a, Vector3f b) {
        return new Vector3f(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public Vector3f multiply(Vector3f a) {
        x *= a.x;
        y *= a.y;
        z *= a.z;
        return this;
    }

    public Vector3f multiply(float x, float y, float z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vector3f multiply(float a) {
        this.x *= a;
        this.y *= a;
        this.z *= a;
        return this;
    }

    public static Vector3f multiply(Vector3f a, Vector3f b) {
        return new Vector3f(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public Vector3f divide(Vector3f a) {
        x /= a.x;
        y /= a.y;
        z /= a.z;
        return this;
    }

    public Vector3f divide(float x, float y, float z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public Vector3f divide(float a) {
        this.x /= a;
        this.y /= a;
        this.z /= a;
        return this;
    }

    public static Vector3f divide(Vector3f a, Vector3f b) {
        return new Vector3f(a.x / b.x, a.y / b.y, a.z / b.z);
    }

    public float magnitude() {
        return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public Vector3f normalized() {
        return new Vector3f(this).divide(magnitude());
    }

    public Vector3f normalize() {
        divide(magnitude());
        return this;
    }

    public float dot(Vector3f other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public static float dot(Vector3f a, Vector3f b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3f vector3f = (Vector3f) o;
        return Float.compare(vector3f.x, x) == 0 &&
                Float.compare(vector3f.y, y) == 0 &&
                Float.compare(vector3f.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
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
}
