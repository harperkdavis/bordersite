package engine.math;

public class Matrix4f {
    public static final int SIZE = 4;
    private final float[] elements = new float[SIZE * SIZE];

    public static Matrix4f identity() {
        Matrix4f result = new Matrix4f();

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

    public static Matrix4f translate(Vector3f translate) {
        Matrix4f result = Matrix4f.identity();

        result.set(3, 0, translate.getX());
        result.set(3, 1, translate.getY());
        result.set(3, 2, translate.getZ());

        return result;
    }

    public static Matrix4f rotation(Vector3f rotation) {

        Matrix4f rotXMatrix = Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0));
        Matrix4f rotYMatrix = Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0));
        Matrix4f rotZMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f((float) -Math.sin(Math.toRadians(rotation.getY())), 0, (float) -Math.cos(Math.toRadians(rotation.getY()))));

        return Matrix4f.multiply(rotXMatrix, Matrix4f.multiply(rotYMatrix, rotZMatrix));

    }

    public static Matrix4f rotate(float angle, Vector3f axis) {
        Matrix4f result = Matrix4f.identity();

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

    public static Matrix4f scale(Vector3f scalar) {
        Matrix4f result = Matrix4f.identity();

        result.set(0, 0, scalar.getX());
        result.set(1, 1, scalar.getY());
        result.set(2, 2, scalar.getZ());

        return result;
    }

    public static Matrix4f transform(Vector3f position, Vector3f rotation, Vector3f scale) {

        Matrix4f translationMatrix = Matrix4f.translate(position.over(scale));
        Matrix4f rotationMatrix = Matrix4f.rotation(rotation);
        Matrix4f scaleMatrix = Matrix4f.scale(scale);

        return Matrix4f.multiply(rotationMatrix, Matrix4f.multiply(translationMatrix, scaleMatrix));
    }

    public static Matrix4f projection(float fov, float aspect, float near, float far) {
        Matrix4f result = Matrix4f.identity();

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

    public static Matrix4f ortho(float left, float right, float top, float bottom, float near, float far) {
        Matrix4f result = Matrix4f.identity();

        float range = far - near;

        result.set(0, 0, 2.0f / (right - left));
        result.set(1, 1, 1.0f / (top - bottom));
        result.set(2, 2, -2.0f / range);
        result.set(3, 0, -(right + left) / (right - left));
        result.set(3, 1, -(top + bottom) / (top - bottom));
        result.set(3, 2, -(far + near) / (far - near));

        return result;
    }

    public static Matrix4f view(Vector3f position, Vector3f rotation) {
        return view(position, rotation, false);
    }

    public static Matrix4f view(Vector3f position, Vector3f rotation, boolean zCompatible) {
        Matrix4f result = Matrix4f.identity();

        Vector3f negative = new Vector3f(-position.getX(), -position.getY(), - position.getZ());
        Matrix4f translationMatrix = Matrix4f.translate(negative);

        Matrix4f rotXMatrix = Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0));
        Matrix4f rotYMatrix = Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0));

        Matrix4f rotZMatrix;
        if (zCompatible) {
            rotZMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f((float) -Math.sin(Math.toRadians(rotation.getY())), 0, (float) -Math.cos(Math.toRadians(rotation.getY()))));
        } else {
            rotZMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f(0, 0, 1));
        }

        Matrix4f rotationMatrix = Matrix4f.multiply(rotZMatrix, Matrix4f.multiply(rotYMatrix, rotXMatrix));

        result = Matrix4f.multiply(translationMatrix, rotationMatrix);

        return result;
    }


    public static Matrix4f multiply(Matrix4f matrix, Matrix4f other) {
        Matrix4f result = Matrix4f.identity();

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

    public static Matrix4f lookAt(Vector3f eye, Vector3f center, Vector3f up) {

        Vector3f f = Vector3f.subtract(center, eye).normalize();
        Vector3f u = up.normalize();
        Vector3f s = Vector3f.cross(f, u).normalize();
        u = Vector3f.cross(s, f).normalize();

        Matrix4f result = Matrix4f.identity();
        result.set(0, 0, s.getX());
        result.set(1, 0, s.getY());
        result.set(2, 0, s.getZ());

        result.set(0, 1, u.getX());
        result.set(1, 1, u.getY());
        result.set(2, 1, u.getZ());

        result.set(0, 2, -f.getX());
        result.set(1, 2, -f.getY());
        result.set(2, 2, -f.getZ());

        return multiply(Matrix4f.translate(new Vector3f(-eye.getX(), -eye.getY(), -eye.getZ())), result);
    }

    public static Matrix4f inverse(Matrix4f matrix) {

        float[] inv = new float[16];

        inv[0] = matrix.elements[5]  * matrix.elements[10] * matrix.elements[15] -
                matrix.elements[5]  * matrix.elements[11] * matrix.elements[14] -
                matrix.elements[9]  * matrix.elements[6]  * matrix.elements[15] +
                matrix.elements[9]  * matrix.elements[7]  * matrix.elements[14] +
                matrix.elements[13] * matrix.elements[6]  * matrix.elements[11] -
                matrix.elements[13] * matrix.elements[7]  * matrix.elements[10];

        inv[4] = -matrix.elements[4]  * matrix.elements[10] * matrix.elements[15] +
                matrix.elements[4]  * matrix.elements[11] * matrix.elements[14] +
                matrix.elements[8]  * matrix.elements[6]  * matrix.elements[15] -
                matrix.elements[8]  * matrix.elements[7]  * matrix.elements[14] -
                matrix.elements[12] * matrix.elements[6]  * matrix.elements[11] +
                matrix.elements[12] * matrix.elements[7]  * matrix.elements[10];

        inv[8] = matrix.elements[4]  * matrix.elements[9] * matrix.elements[15] -
                matrix.elements[4]  * matrix.elements[11] * matrix.elements[13] -
                matrix.elements[8]  * matrix.elements[5] * matrix.elements[15] +
                matrix.elements[8]  * matrix.elements[7] * matrix.elements[13] +
                matrix.elements[12] * matrix.elements[5] * matrix.elements[11] -
                matrix.elements[12] * matrix.elements[7] * matrix.elements[9];

        inv[12] = -matrix.elements[4]  * matrix.elements[9] * matrix.elements[14] +
                matrix.elements[4]  * matrix.elements[10] * matrix.elements[13] +
                matrix.elements[8]  * matrix.elements[5] * matrix.elements[14] -
                matrix.elements[8]  * matrix.elements[6] * matrix.elements[13] -
                matrix.elements[12] * matrix.elements[5] * matrix.elements[10] +
                matrix.elements[12] * matrix.elements[6] * matrix.elements[9];

        inv[1] = -matrix.elements[1]  * matrix.elements[10] * matrix.elements[15] +
                matrix.elements[1]  * matrix.elements[11] * matrix.elements[14] +
                matrix.elements[9]  * matrix.elements[2] * matrix.elements[15] -
                matrix.elements[9]  * matrix.elements[3] * matrix.elements[14] -
                matrix.elements[13] * matrix.elements[2] * matrix.elements[11] +
                matrix.elements[13] * matrix.elements[3] * matrix.elements[10];

        inv[5] = matrix.elements[0]  * matrix.elements[10] * matrix.elements[15] -
                matrix.elements[0]  * matrix.elements[11] * matrix.elements[14] -
                matrix.elements[8]  * matrix.elements[2] * matrix.elements[15] +
                matrix.elements[8]  * matrix.elements[3] * matrix.elements[14] +
                matrix.elements[12] * matrix.elements[2] * matrix.elements[11] -
                matrix.elements[12] * matrix.elements[3] * matrix.elements[10];

        inv[9] = -matrix.elements[0]  * matrix.elements[9] * matrix.elements[15] +
                matrix.elements[0]  * matrix.elements[11] * matrix.elements[13] +
                matrix.elements[8]  * matrix.elements[1] * matrix.elements[15] -
                matrix.elements[8]  * matrix.elements[3] * matrix.elements[13] -
                matrix.elements[12] * matrix.elements[1] * matrix.elements[11] +
                matrix.elements[12] * matrix.elements[3] * matrix.elements[9];

        inv[13] = matrix.elements[0]  * matrix.elements[9] * matrix.elements[14] -
                matrix.elements[0]  * matrix.elements[10] * matrix.elements[13] -
                matrix.elements[8]  * matrix.elements[1] * matrix.elements[14] +
                matrix.elements[8]  * matrix.elements[2] * matrix.elements[13] +
                matrix.elements[12] * matrix.elements[1] * matrix.elements[10] -
                matrix.elements[12] * matrix.elements[2] * matrix.elements[9];

        inv[2] = matrix.elements[1]  * matrix.elements[6] * matrix.elements[15] -
                matrix.elements[1]  * matrix.elements[7] * matrix.elements[14] -
                matrix.elements[5]  * matrix.elements[2] * matrix.elements[15] +
                matrix.elements[5]  * matrix.elements[3] * matrix.elements[14] +
                matrix.elements[13] * matrix.elements[2] * matrix.elements[7] -
                matrix.elements[13] * matrix.elements[3] * matrix.elements[6];

        inv[6] = -matrix.elements[0]  * matrix.elements[6] * matrix.elements[15] +
                matrix.elements[0]  * matrix.elements[7] * matrix.elements[14] +
                matrix.elements[4]  * matrix.elements[2] * matrix.elements[15] -
                matrix.elements[4]  * matrix.elements[3] * matrix.elements[14] -
                matrix.elements[12] * matrix.elements[2] * matrix.elements[7] +
                matrix.elements[12] * matrix.elements[3] * matrix.elements[6];

        inv[10] = matrix.elements[0]  * matrix.elements[5] * matrix.elements[15] -
                matrix.elements[0]  * matrix.elements[7] * matrix.elements[13] -
                matrix.elements[4]  * matrix.elements[1] * matrix.elements[15] +
                matrix.elements[4]  * matrix.elements[3] * matrix.elements[13] +
                matrix.elements[12] * matrix.elements[1] * matrix.elements[7] -
                matrix.elements[12] * matrix.elements[3] * matrix.elements[5];

        inv[14] = -matrix.elements[0]  * matrix.elements[5] * matrix.elements[14] +
                matrix.elements[0]  * matrix.elements[6] * matrix.elements[13] +
                matrix.elements[4]  * matrix.elements[1] * matrix.elements[14] -
                matrix.elements[4]  * matrix.elements[2] * matrix.elements[13] -
                matrix.elements[12] * matrix.elements[1] * matrix.elements[6] +
                matrix.elements[12] * matrix.elements[2] * matrix.elements[5];

        inv[3] = -matrix.elements[1] * matrix.elements[6] * matrix.elements[11] +
                matrix.elements[1] * matrix.elements[7] * matrix.elements[10] +
                matrix.elements[5] * matrix.elements[2] * matrix.elements[11] -
                matrix.elements[5] * matrix.elements[3] * matrix.elements[10] -
                matrix.elements[9] * matrix.elements[2] * matrix.elements[7] +
                matrix.elements[9] * matrix.elements[3] * matrix.elements[6];

        inv[7] = matrix.elements[0] * matrix.elements[6] * matrix.elements[11] -
                matrix.elements[0] * matrix.elements[7] * matrix.elements[10] -
                matrix.elements[4] * matrix.elements[2] * matrix.elements[11] +
                matrix.elements[4] * matrix.elements[3] * matrix.elements[10] +
                matrix.elements[8] * matrix.elements[2] * matrix.elements[7] -
                matrix.elements[8] * matrix.elements[3] * matrix.elements[6];

        inv[11] = -matrix.elements[0] * matrix.elements[5] * matrix.elements[11] +
                matrix.elements[0] * matrix.elements[7] * matrix.elements[9] +
                matrix.elements[4] * matrix.elements[1] * matrix.elements[11] -
                matrix.elements[4] * matrix.elements[3] * matrix.elements[9] -
                matrix.elements[8] * matrix.elements[1] * matrix.elements[7] +
                matrix.elements[8] * matrix.elements[3] * matrix.elements[5];

        inv[15] = matrix.elements[0] * matrix.elements[5] * matrix.elements[10] -
                matrix.elements[0] * matrix.elements[6] * matrix.elements[9] -
                matrix.elements[4] * matrix.elements[1] * matrix.elements[10] +
                matrix.elements[4] * matrix.elements[2] * matrix.elements[9] +
                matrix.elements[8] * matrix.elements[1] * matrix.elements[6] -
                matrix.elements[8] * matrix.elements[2] * matrix.elements[5];

        float det = matrix.elements[0] * inv[0] + matrix.elements[1] * inv[4] + matrix.elements[2] * inv[8] + matrix.elements[3] * inv[12];

        if (det == 0)
            return null;

        det = 1.0f / det;
        
        Matrix4f newMatrix = Matrix4f.identity();
        System.arraycopy(inv, 0, newMatrix.elements, 0, 16);

        return newMatrix;
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
