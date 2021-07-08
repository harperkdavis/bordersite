package engine.objects;

import engine.math.Vector3f;

public class Camera {

    private Vector3f position, rotation;
    private static float fov = 80.0f;

    private static final Camera mainCamera = new Camera(new Vector3f(0, 0, 0), Vector3f.zero());

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

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

    public static Camera getMainCamera() {
        return mainCamera;
    }

    public static Vector3f getMainCameraPosition() {
        return mainCamera.getPosition();
    }

    public static Vector3f getMainCameraRotation() {
        return mainCamera.getRotation();
    }

    public static void setMainCameraPosition(Vector3f position) {
        mainCamera.setPosition(position);
    }

    public static void setMainCameraRotation(Vector3f rotation) {
        mainCamera.setRotation(rotation);
    }

    public static void addMainCameraPosition(Vector3f position) {
        mainCamera.setPosition(Vector3f.add(mainCamera.getPosition(), position));
    }

    public static void addMainCameraRotation(Vector3f rotation) {
        mainCamera.setRotation(Vector3f.add(mainCamera.getRotation(), rotation));
    }

    public static float getFov() {
        return fov;
    }

    public static void setFov(float fov) {
        Camera.fov = fov;
    }

}
