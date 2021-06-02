package engine.math;

public class Matrix4 {
    public static final int SIZE = 4;
    private final float[] elements = new float[SIZE * SIZE];

    public static Matrix4 identity() {
        Matrix4 result = new Matrix4();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.set(i, j, 0);
            }
        }

        result.set(0, 0, 1);
        result.set(1, 1, 1);
        result.set(2, 2, 1);
        result.set(3, 3, 1);

        return result;
    }

    public static Matrix4 translate(Vector3 translate) {
        Matrix4 result = Matrix4.identity();

        result.set(3, 0, translate.getX());
        result.set(3, 1, translate.getY());
        result.set(3, 2, translate.getZ());

        return result;
    }

    public static Matrix4 rotation(Vector3 rotation) {

        Matrix4 rotXMatrix = Matrix4.rotate(rotation.getX(), new Vector3(1, 0, 0));
        Matrix4 rotYMatrix = Matrix4.rotate(rotation.getY(), new Vector3(0, 1, 0));
        Matrix4 rotZMatrix = Matrix4.rotate(rotation.getZ(), new Vector3(0, 0, 1));

        return Matrix4.multiply(rotXMatrix, Matrix4.multiply(rotYMatrix, rotZMatrix));

    }

    public static Matrix4 rotate(float angle, Vector3 axis) {
        Matrix4 result = Matrix4.identity();

        float cos = (float) Math.cos(Math.toRadians(angle));
        float sin = (float) Math.sin(Math.toRadians(angle));
        float C = 1 - cos;

        result.set(0, 0, cos + axis.getX() * axis.getX() * C);
        result.set(0, 1, axis.getX() * axis.getY() * C - axis.getZ() * sin);
        result.set(0, 2, axis.getX() * axis.getZ() * C + axis.getY() * sin);

        result.set(1, 0, axis.getY() * axis.getX() * C + axis.getZ() * sin);
        result.set(1, 1, cos + axis.getY() * axis.getY() * C);
        result.set(1, 2, axis.getY() * axis.getZ() * C - axis.getX() * sin);

        result.set(2, 0, axis.getZ() * axis.getX() * C - axis.getY() * sin);
        result.set(2, 1, axis.getZ() * axis.getY() * C + axis.getX() * sin);
        result.set(2, 2, cos + axis.getZ() * axis.getZ() * C);

        return result;
    }

    public static Matrix4 scale(Vector3 scalar) {
        Matrix4 result = Matrix4.identity();

        result.set(0, 0, scalar.getX());
        result.set(1, 1, scalar.getY());
        result.set(2, 2, scalar.getZ());

        return result;
    }

    public static Matrix4 transform(Vector3 position, Vector3 rotation, Vector3 scale) {

        Matrix4 translationMatrix = Matrix4.translate(new Vector3(position).divide(scale));
        Matrix4 rotationMatrix = Matrix4.rotation(rotation);
        Matrix4 scaleMatrix = Matrix4.scale(scale);

        return Matrix4.multiply(rotationMatrix, Matrix4.multiply(translationMatrix, scaleMatrix));
    }

    public static Matrix4 projection(float fov, float aspect, float near, float far) {
        Matrix4 result = Matrix4.identity();

        float tanFov = (float) Math.tan(Math.toRadians(fov / 2));
        float range = far - near;

        result.set(0, 0, 1.0f / (aspect * tanFov));
        result.set(1, 1, 1.0f / tanFov);
        result.set(2, 2, -((far + near) / range));
        result.set(2, 3, -1);
        result.set(3, 2, -((2 * far * near) / range));
        result.set(3, 3, 0);

        return result;
    }

    public static Matrix4 ortho(float left, float right, float top, float bottom, float near, float far) {
        Matrix4 result = Matrix4.identity();

        float range = far - near;

        result.set(0, 0, 2.0f / (right - left));
        result.set(1, 1, 1.0f / (top - bottom));
        result.set(2, 2, -2.0f / range);
        result.set(3, 0, -(right + left) / (right - left));
        result.set(3, 1, -(top + bottom) / (top - bottom));
        result.set(3, 2, -(far + near) / (far - near));

        return result;
    }

    public static Matrix4 view(Vector3 position, Vector3 rotation) {
        return view(position, rotation, false);
    }

    public static Matrix4 view(Vector3 position, Vector3 rotation, boolean zCompatible) {
        Matrix4 result = Matrix4.identity();

        Vector3 negative = new Vector3(-position.getX(), -position.getY(), - position.getZ());
        Matrix4 translationMatrix = Matrix4.translate(negative);

        Matrix4 rotXMatrix = Matrix4.rotate(rotation.getX(), new Vector3(1, 0, 0));
        Matrix4 rotYMatrix = Matrix4.rotate(rotation.getY(), new Vector3(0, 1, 0));

        Matrix4 rotZMatrix;
        if (zCompatible) {
            rotZMatrix = Matrix4.rotate(rotation.getZ(), new Vector3((float) -Math.sin(Math.toRadians(rotation.getY())), 0, (float) -Math.cos(Math.toRadians(rotation.getY()))));
        } else {
            rotZMatrix = Matrix4.rotate(rotation.getZ(), new Vector3(0, 0, 1));
        }

        Matrix4 rotationMatrix = Matrix4.multiply(rotZMatrix, Matrix4.multiply(rotYMatrix, rotXMatrix));

        result = Matrix4.multiply(translationMatrix, rotationMatrix);

        return result;
    }


    public static Matrix4 multiply(Matrix4 matrix, Matrix4 other) {
        Matrix4 result = Matrix4.identity();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.set(i, j, matrix.get(i, 0) * other.get(0, j) +
                        matrix.get(i, 1) * other.get(1, j) +
                        matrix.get(i, 2) * other.get(2, j) +
                        matrix.get(i, 3) * other.get(3, j));
            }
        }

        return result;
    }

    public static Matrix4 lookAt(Vector3 eye, Vector3 center, Vector3 up) {

        Vector3 f = Vector3.subtract(center, eye).normalize();
        Vector3 u = up.normalize();
        Vector3 s = Vector3.cross(f, u).normalize();
        u = Vector3.cross(s, f).normalize();

        Matrix4 result = Matrix4.identity();
        result.set(0, 0, s.getX());
        result.set(1, 0, s.getY());
        result.set(2, 0, s.getZ());

        result.set(0, 1, u.getX());
        result.set(1, 1, u.getY());
        result.set(2, 1, u.getZ());

        result.set(0, 2, -f.getX());
        result.set(1, 2, -f.getY());
        result.set(2, 2, -f.getZ());

        return multiply(Matrix4.translate(new Vector3(-eye.getX(), -eye.getY(), -eye.getZ())), result);
    }

    public float get(int x, int y) {
        return elements[y * SIZE + x];
    }

    public void set(int x, int y, float value) {
        elements[y * SIZE + x] = value;
    }

    public float[] getAll() {
        return elements;
    }
}
