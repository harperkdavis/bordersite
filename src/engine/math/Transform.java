package engine.math;

public class Transform {

    private Vector3 position, rotation, scale;
    private Vector3 localPosition, localRotation;

    public Transform(Vector3 position, Vector3 rotation, Vector3 scale) {
        this.position = position;
        this.localPosition = position;
        this.rotation = rotation;
        this.localRotation = rotation;
        this.scale = scale;
    }

    public static Transform identity() {
        return new Transform(Vector3.zero(), Vector3.zero(), Vector3.one());
    }

    public static Vector3 transformPosition(Vector3 position, Transform transform) {
        Vector4 position4 = new Vector4(position, 0);
        position4.multiply(transform.getMatrix());
        return new Vector3(position4.getX(), position4.getY(), position4.getZ());
    }

    public void transformBy(Transform transform) {
        position = transformPosition(localPosition, transform);
        rotation = Vector3.add(localRotation, transform.rotation);
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getRotation() {
        return rotation;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
    }

    public Vector3 getLocalPosition() {
        return localPosition;
    }

    public void setLocalPosition(Vector3 localPosition) {
        this.localPosition = localPosition;
    }

    public Vector3 getLocalRotation() {
        return localRotation;
    }

    public void setLocalRotation(Vector3 localRotation) {
        this.localRotation = localRotation;
    }

    public Matrix4 getMatrix() {
        return Matrix4.transform(position, rotation, scale);
    }

    public void setLocals(Transform transform) {
        localPosition = Vector3.subtract(this.position, transform.position);
        localRotation = Vector3.subtract(this.rotation, transform.rotation);
    }

    public void resetLocals() {
        localPosition = new Vector3(position);
        localRotation = new Vector3(rotation);
    }

}
