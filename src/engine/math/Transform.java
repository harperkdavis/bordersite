package engine.math;

public class Transform {

    private Vector3f position, rotation, scale;
    private Vector3f localPosition, localRotation;

    public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.localPosition = position;
        this.rotation = rotation;
        this.localRotation = rotation;
        this.scale = scale;
    }

    public static Transform identity() {
        return new Transform(Vector3f.zero(), Vector3f.zero(), Vector3f.one());
    }

    public static Vector3f transformPosition(Vector3f position, Transform transform) {
        Vector4f position4 = new Vector4f(position, 0);
        position4.multiply(transform.getMatrix());
        return new Vector3f(position4.getX(), position4.getY(), position4.getZ());
    }

    public void transformBy(Transform transform) {
        position = transformPosition(localPosition, transform);
        rotation = Vector3f.add(localRotation, transform.rotation);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getLocalPosition() {
        return localPosition;
    }

    public void setLocalPosition(Vector3f localPosition) {
        this.localPosition = localPosition;
    }

    public Vector3f getLocalRotation() {
        return localRotation;
    }

    public void setLocalRotation(Vector3f localRotation) {
        this.localRotation = localRotation;
    }

    public Matrix4f getMatrix() {
        return Matrix4f.transform(position, rotation, scale);
    }

    public void setLocals(Transform transform) {
        localPosition = Vector3f.subtract(this.position, transform.position);
        localRotation = Vector3f.subtract(this.rotation, transform.rotation);
    }

    public void resetLocals() {
        localPosition = new Vector3f(position);
        localRotation = new Vector3f(rotation);
    }

}
