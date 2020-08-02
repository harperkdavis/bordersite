package engine.math;

public class Matrix4 {

    public static final int SIZE = 4;
    private float[] elements = new float[SIZE * SIZE];

    public static Matrix4 identity() {
        Matrix4 result = new Matrix4();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                result.set(i, j, i == j ? 1 : 0);
            }
        }

        return result;
    }

    public static Matrix4 translate(Vector3 translate) {
        Matrix4 result = Matrix4.identity();

        result.set(0, 3, translate.x);
        result.set(1, 3, translate.y);
        result.set(2, 3, translate.z);

        return result;
    }

    public static Matrix4 rotate(float angle, Vector3 axis) {
        Matrix4 result = Matrix4.identity();

        float cos = (float) Math.cos(Math.toRadians(angle));
        float sin = (float) Math.sin(Math.toRadians(angle));
        float C = 1 - cos;
        float S = 1 - sin;

        result.set(0, 0, cos + axis.x * axis.x * C);
        result.set(0, 1, axis.x * axis.y * C - axis.z * sin);
        result.set(0, 2, axis.x * axis.z * C + axis.y * sin);

        result.set(1, 0, axis.y * axis.x * C + axis.z * sin);
        result.set(1, 1, cos + axis.y * axis.y * C);
        result.set(1, 2, axis.y * axis.z * C - axis.x * sin);

        result.set(2, 0, axis.z * axis.x * C - axis.y * sin);
        result.set(2, 1, axis.z * axis.y * C + axis.x * sin);
        result.set(2, 2, cos + axis.z * axis.z * C);

        return result;
    }

    public static Matrix4 scale(Vector3 scalar) {
        Matrix4 result = Matrix4.identity();

        result.set(0, 0, scalar.x);
        result.set(1, 1, scalar.y);
        result.set(2, 2, scalar.z);

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
