package engine.math;

import java.util.Objects;

public class Vector3 {

    private float x, y, z;

    public static Vector3 zero() { return new Vector3(0, 0, 0); }
    public static Vector3 uirzero() { return new Vector3(90, 0, 0); }
    public static Vector3 one() { return new Vector3(1, 1, 1); }
    public static Vector3 oneX() { return new Vector3(1, 0, 0); }
    public static Vector3 oneY() { return new Vector3(0, 1, 0); }
    public static Vector3 oneZ() { return new Vector3(0, 0, 1); }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 other) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = other.getZ();
    }

    public Vector3(Vector2 other, float z) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = z;
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

    public static Vector3 add(Vector3 a, Vector3 b) {
        return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
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

    public static Vector3 subtract(Vector3 a, Vector3 b) {
        return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
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

    public static Vector3 multiply(Vector3 a, Vector3 b) {
        return new Vector3(a.x * b.x, a.y * b.y, a.z * b.z);
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

    public static Vector3 divide(Vector3 a, Vector3 b) {
        return new Vector3(a.x / b.x, a.y / b.y, a.z / b.z);
    }

    public float magnitude() {
        return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public Vector3 normalized() {
        return new Vector3(this).divide(magnitude());
    }

    public Vector3 normalize() {
        divide(magnitude());
        return this;
    }

    public static Vector3 cross(Vector3 a, Vector3 b) {
        return new Vector3(a.getY() * b.getZ() - a.getZ() * b.getY(), a.getZ() * b.getX() - a.getX() * b.getZ(), a.getX() * b.getY() - a.getY() * b.getX());
    }

    public float dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public static float dot(Vector3 a, Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vector3 lerp(Vector3 a, Vector3 b, float c) {
        return new Vector3(Mathf.lerp(a.getX(), b.getX(), c), Mathf.lerp(a.getY(), b.getY(), c), Mathf.lerp(a.getZ(), b.getZ(), c));
    }

    public static Vector3 lerpdt(Vector3 a, Vector3 b, float c) {
        return new Vector3(Mathf.lerpdt(a.getX(), b.getX(), c), Mathf.lerpdt(a.getY(), b.getY(), c), Mathf.lerpdt(a.getZ(), b.getZ(), c));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3 vector3 = (Vector3) o;
        return Float.compare(vector3.x, x) == 0 &&
                Float.compare(vector3.y, y) == 0 &&
                Float.compare(vector3.z, z) == 0;
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

    public Vector3 getRelative(Vector3 vector) {
        Matrix4 rotXMatrix = Matrix4.rotate(x, new Vector3(1, 0, 0));
        Matrix4 rotYMatrix = Matrix4.rotate(y, new Vector3(0, 1, 0));
        Matrix4 rotZMatrix = Matrix4.rotate(z, new Vector3(0, 0, 1));

        Matrix4 rotation = Matrix4.multiply(rotZMatrix, Matrix4.multiply(rotYMatrix, rotXMatrix));
        Vector4 vec = new Vector4(vector, 1);
        vec.multiply(rotation);

        return new Vector3(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vector3 getForward() {
        return getRelative(new Vector3(0, 0, -1));
    }

    public String toString() {
        return "{x: " + x + ", y: " + y + ", z: " + z + "}";
    }
}
