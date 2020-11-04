package main;

import engine.io.Input;
import engine.math.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Bordersite {

    private Main main;
    private Vector3f position, velocity;
    private boolean isCrouched = false;
    private boolean isSprinting = false;
    private boolean isGrounded = true;

    private float velocityForward = 0;
    private float velocityLeft = 0;
    private float velocityRight = 0;
    private float velocityBack = 0;

    private float frames;

    private float cameraHeight = 2;
    private float headBobbing = 0;
    private float headBobbingMultiplier = 0;

    private final float MOVE_SPEED = 0.08f, SMOOTHING = 0.3f, CROUCH_SPEED = 0.6f, SPRINT_SPEED = 1.4f, JUMP_HEIGHT = 0.06f;

    private long timeStart;
    private int timeElapsed;

    public Bordersite(Main main) {
        position = new Vector3f(0, 0, 0);
        velocity = new Vector3f(0, 0, 0);
        timeStart = System.currentTimeMillis();
        this.main = main;
    }

    public void update() {

        timeElapsed = (int) (System.currentTimeMillis() - timeStart);

        float deltaTime = (1 / main.window.frameRate) * 60;

        isCrouched = Input.isKey(GLFW.GLFW_KEY_LEFT_CONTROL);
        isSprinting = Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT);

        if (isGrounded) {
            velocity.setX(velocity.getX() / (15.0f * deltaTime));
            velocity.setZ(velocity.getZ() / (15.0f * deltaTime));
            position.setY(0);
            if (Input.isKey(GLFW.GLFW_KEY_SPACE)) {
                velocity.setY(JUMP_HEIGHT);
                isGrounded = false;
            }
        } else {
            velocity.setX(velocity.getX() / (6.0f * deltaTime));
            velocity.setZ(velocity.getZ() / (6.0f * deltaTime));
            velocity.setY(velocity.getY() - (0.003f * deltaTime));
            if (position.getY() <= 0) {
                isGrounded = true;
                velocity.setY(0);
            }
        }

        if (isCrouched || !isGrounded) {
            isSprinting = false;
        }

        float rot = (float) Math.toRadians(main.camera.getRotation().getY());

        velocityForward = lerp(velocityForward, Input.isKey(GLFW.GLFW_KEY_W) ? 1 : 0, SMOOTHING);
        velocityLeft = lerp(velocityLeft, Input.isKey(GLFW.GLFW_KEY_A) ? 1 : 0, SMOOTHING);
        velocityRight = lerp(velocityRight, Input.isKey(GLFW.GLFW_KEY_D) ? 1 : 0, SMOOTHING);
        velocityBack = lerp(velocityBack, Input.isKey(GLFW.GLFW_KEY_S) ? 1 : 0, SMOOTHING);

        float velocitySum = velocityForward + velocityLeft + velocityRight + velocityBack;
        float moving = velocitySum > 0.1f  ? 1 : 0;

        if (velocitySum > 1) {
            velocityForward *= (1 / velocitySum) * Math.sqrt(2);
            velocityLeft *= (1 / velocitySum) * Math.sqrt(2);
            velocityRight *= (1 / velocitySum) * Math.sqrt(2);
            velocityBack *= (1 / velocitySum) * Math.sqrt(2);
        }

        Vector3f vectorForward = new Vector3f((float) -Math.sin(rot), 0, (float) -Math.cos(rot));
        Vector3f vectorLeft = new Vector3f((float) -Math.sin(rot + Math.PI / 2), 0, (float) -Math.cos(rot + Math.PI / 2));
        Vector3f vectorRight = new Vector3f((float) -Math.sin(rot - Math.PI / 2), 0, (float) -Math.cos(rot - Math.PI / 2));
        Vector3f vectorBack = new Vector3f((float) Math.sin(rot), 0, (float) Math.cos(rot));

        float speed = MOVE_SPEED * (isCrouched ? CROUCH_SPEED : 1) * (isSprinting ? SPRINT_SPEED : 1);

        velocity.add(vectorForward.multiply(velocityForward).multiply(speed).multiply(1 / main.window.frameRate).multiply(60));
        velocity.add(vectorLeft.multiply(velocityLeft).multiply(speed).multiply(1 / main.window.frameRate).multiply(60));
        velocity.add(vectorRight.multiply(velocityRight).multiply(speed).multiply(1 / main.window.frameRate).multiply(60));
        velocity.add(vectorBack.multiply(velocityBack).multiply(speed).multiply(1 / main.window.frameRate).multiply(60));

        headBobbingMultiplier = lerp(headBobbingMultiplier, moving, 0.1f);
        headBobbing = (float) (Math.sin(timeElapsed / (80f * (isSprinting ? 0.6f : 1))) * 0.1f * (isCrouched ? 0.5f : 1f) * headBobbingMultiplier);

        cameraHeight = lerp(cameraHeight, isCrouched ? 1.5f : 2, 0.02f);

        position.add(velocity);

        main.camera.setPosition(new Vector3f(position).add(0, cameraHeight + (isGrounded ? headBobbing : 0),0));
    }

    private float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

}
