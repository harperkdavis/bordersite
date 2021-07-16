package engine.objects.camera;

import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;

public class OrbitCamera extends Camera {

    private Vector3f position, center;
    private float fov;

    public OrbitCamera(Vector3f position, Vector3f center, float fov) {
        this.position = position;
        this.center = center;
        this.fov = fov;
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    @Override
    public Matrix4f getViewMatrix() {
        return Matrix4f.lookAt(position, center, Vector3f.oneY());
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return Matrix4f.projection(fov, (float) Window.getWidth() / (float) Window.getHeight(), 0.1f, 10000.0f);
    }
}
