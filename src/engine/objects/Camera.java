package engine.objects;

import engine.io.Input;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {

    private Vector3f position, rotation;
    private final float MOUSE_SENSITIVITY = 0.04f;
    private float preMouseX = 0, preMouseY = 0, mouseX = 0, mouseY = 0;

    private static Camera mainCamera = new Camera(new Vector3f(0, 0, 0), Vector3f.zero());

    private float cameraTilt = 0;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void update() {

        mouseX = (float) Input.getMouseX();
        mouseY = (float) Input.getMouseY();

        float accX = mouseX - preMouseX;
        float accY = mouseY - preMouseY;

        rotation = rotation.add(-accY * MOUSE_SENSITIVITY, -accX * MOUSE_SENSITIVITY, 0);

        preMouseX = mouseX;
        preMouseY = mouseY;

        rotation.setZ(cameraTilt);
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

    public float getCameraTilt() {
        return cameraTilt;
    }

    public void setCameraTilt(float cameraTilt) {
        this.cameraTilt = cameraTilt;
    }


}
