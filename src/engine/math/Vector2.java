package engine.math;

public class Vector2 {

    public float x, y;

    public static Vector2 zero() { return new Vector2(0, 0); }
    public static Vector2 one() { return new Vector2(1, 1); }
    public static Vector2 oneX() { return new Vector2(1, 0); }
    public static Vector2 oneY() { return new Vector2(0, 1); }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 other) {
        this.x = other.getX();
        this.y = other.getY();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 a) {
        x += a.x;
        y += a.y;
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }

    public Vector2 subtract(Vector2 a) {
        x -= a.x;
        y -= a.y;
        return this;
    }

    public Vector2 subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public static Vector2 subtract(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }

    public Vector2 multiply(Vector2 a) {
        x *= a.x;
        y *= a.y;
        return this;
    }

    public Vector2 multiply(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2 multiply(float a) {
        this.x *= a;
        this.y *= a;
        return this;
    }

    public static Vector2 multiply(Vector2 a, Vector2 b) {
        return new Vector2(a.x * b.x, a.y * b.y);
    }

    public Vector2 divide(Vector2 a) {
        x /= a.x;
        y /= a.y;
        return this;
    }

    public Vector2 divide(float x, float y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public Vector2 divide(float a) {
        this.x /= a;
        this.y /= a;
        return this;
    }

    public static Vector2 divide(Vector2 a, Vector2 b) {
        return new Vector2(a.x / b.x, a.y / b.y);
    }

    public float magnitude() {
        return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public Vector2 normalized() {
        return new Vector2(this).divide(magnitude());
    }

    public Vector2 normalize() {
        divide(magnitude());
        return this;
    }

    public float dot(Vector2 other) {
        return x * other.x + y * other.y;
    }

    public static float dot(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }
    
    public static Vector2 toPolar(Vector2 origin, Vector2 vector) {
        Vector2 distanceVector = Vector2.subtract(origin, vector);
        double angle = Math.atan2(distanceVector.getX(), distanceVector.getY());
        double distance = Math.sqrt(Math.pow(distanceVector.getX(), 2) + Math.pow(distanceVector.getY(), 2));
        return new Vector2((float) angle, (float) distance);
    }

    public static Vector2 toCartesian(Vector2 origin, Vector2 vector) {
        return Vector2.add(origin, new Vector2((float) Math.sin(vector.getX()) * vector.getY(), (float) Math.cos(vector.getX()) * vector.getY()));
    }
}
