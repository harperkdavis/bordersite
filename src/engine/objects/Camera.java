package engine.objects;

import engine.math.Vector3;

public class Camera {

    private Vector3 position, rotation;

    private static final Camera mainCamera = new Camera(new Vector3(0, 0, 0), Vector3.zero());

    public Camera(Vector3 position, Vector3 rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getRotation() {
        return rotation;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }

    public static Camera getMainCamera() {
        return mainCamera;
    }

    public static Vector3 getMainCameraPosition() {
        return mainCamera.getPosition();
    }

    public static Vector3 getMainCameraRotation() {
        return mainCamera.getRotation();
    }

    public static void setMainCameraPosition(Vector3 position) {
        mainCamera.setPosition(position);
    }

    public static void setMainCameraRotation(Vector3 rotation) {
        mainCamera.setRotation(rotation);
    }

    public static void addMainCameraPosition(Vector3 position) {
        mainCamera.setPosition(Vector3.add(mainCamera.getPosition(), position));
    }

    public static void addMainCameraRotation(Vector3 rotation) {
        mainCamera.setRotation(Vector3.add(mainCamera.getRotation(), rotation));
    }


}
