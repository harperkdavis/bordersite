package engine.objects;

import engine.io.Input;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {

    private Vector3f position, rotation;
    private final float MOUSE_SENSITIVITY = 0.04f;
    private float preMouseX = 0, preMouseY = 0, mouseX = 0, mouseY = 0;

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

}
