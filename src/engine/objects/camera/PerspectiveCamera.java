package engine.objects.camera;

import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;

public class PerspectiveCamera extends Camera {

    private Vector3f position, rotation;
    private float fov = 80.0f;

    public PerspectiveCamera(Vector3f position, Vector3f rotation, float fov) {
        this.position = position;
        this.rotation = rotation;
        this.fov = fov;
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    @Override
    public Matrix4f getViewMatrix() {
        return Matrix4f.view(position, rotation, true);
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return Matrix4f.projection(fov, (float) Window.getWidth() / (float) Window.getHeight(), 0.01f, 2000.0f);
    }
}
