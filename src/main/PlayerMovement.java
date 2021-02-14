package main;

import engine.io.Input;
import engine.io.Window;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.math.Region3f;
import engine.objects.Camera;
import org.lwjgl.glfw.GLFW;

import static engine.math.Mathf.lerp;

public class PlayerMovement {

    private static PlayerMovement playerMovement;

    private Vector3f position, velocity;

    private boolean isCrouched = false;
    private boolean isSprinting = false;
    private boolean isGrounded = true;
    private boolean isAiming = false;

    private float velocityForward = 0;
    private float velocityLeft = 0;
    private float velocityRight = 0;
    private float velocityBack = 0;

    private float frames;

    private float cameraHeight = 2;
    private float headBobbing = 0;
    private float headBobbingMultiplier = 0;

    private final float MOVE_SPEED = 0.06f, SMOOTHING = 0.6f, CROUCH_SPEED = 0.6f, SPRINT_SPEED = 1.4f, JUMP_HEIGHT = 0.12f;
    private final float GRAVITY = 0.006f;

    private float averageFPS = 120f;

    private long timeStart;
    private int timeElapsed;

    private float recoil = 1.0f;

    private float root2 = (float) Math.sqrt(2);

    public Region3f wallRegion = new Region3f(new Vector3f(7.5f, 1.0f, 7.5f), new Vector3f( 12.5f, -2.5f, 12.5f));
    // Hipfire 1.0
    // ADS 0.5
    // Moving +0.5
    // In-Air +0.5
    // Crouching -0.2

    public PlayerMovement() {
        position = new Vector3f(0, 0, 0);
        velocity = new Vector3f(0, 0, 0);
        timeStart = System.currentTimeMillis();
    }

    public void update() {

        timeElapsed = (int) (System.currentTimeMillis() - timeStart);

        averageFPS = lerp(averageFPS, Window.getGameWindow().frameRate, 0.02f);

        float deltaTime = 60 / averageFPS;

        isCrouched = Input.isKey(GLFW.GLFW_KEY_LEFT_CONTROL);
        isSprinting = Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT);
        isAiming = Input.isMouseButton(1);

        boolean feetWithinWall = wallRegion.isWithin(new Vector3f(position).add(0, -0.02f, 0));

        isGrounded = position.getY() <= 0 || feetWithinWall;

        if (isGrounded) {
            if (Input.isKey(GLFW.GLFW_KEY_SPACE)) {
                jump();
                isGrounded = false;
            } else {
                velocity.setX(velocity.getX() / (1 + (5.0f)));
                velocity.setZ(velocity.getZ() / (1 + (5.0f)));
                velocity.setY(0);
            }
        } else {
            velocity.setX(velocity.getX() / (1 + (1.5f)));
            velocity.setZ(velocity.getZ() / (1 + (1.5f)));
            velocity.setY(velocity.getY() - (GRAVITY) * deltaTime);
        }

        if (isCrouched || !isGrounded || isAiming) {
            isSprinting = false;
        }

        float rot = (float) Math.toRadians(Camera.getMainCameraRotation().getY());

        velocityForward = lerp(velocityForward, Input.isKey(GLFW.GLFW_KEY_W) ? 1 : 0, SMOOTHING);
        velocityLeft = lerp(velocityLeft, Input.isKey(GLFW.GLFW_KEY_A) ? 1 : 0, SMOOTHING);
        velocityRight = lerp(velocityRight, Input.isKey(GLFW.GLFW_KEY_D) ? 1 : 0, SMOOTHING);
        velocityBack = lerp(velocityBack, Input.isKey(GLFW.GLFW_KEY_S) ? 1 : 0, SMOOTHING);

        float velocitySum = velocityForward + velocityLeft + velocityRight + velocityBack;
        float moving = velocitySum > 0.1f ? 1 : 0;

        if (velocitySum > 1.0f) {
            velocityForward *= (1 / velocitySum) * root2;
            velocityLeft *= (1 / velocitySum) * root2;
            velocityRight *= (1 / velocitySum) * root2;
            velocityBack *= (1 / velocitySum) * root2;
        }

        Vector3f vectorForward = new Vector3f((float) -Math.sin(rot), 0, (float) -Math.cos(rot));
        Vector3f vectorLeft = new Vector3f((float) -Math.sin(rot + Math.PI / 2), 0, (float) -Math.cos(rot + Math.PI / 2));
        Vector3f vectorRight = new Vector3f((float) -Math.sin(rot - Math.PI / 2), 0, (float) -Math.cos(rot - Math.PI / 2));
        Vector3f vectorBack = new Vector3f((float) Math.sin(rot), 0, (float) Math.cos(rot));

        float speed = MOVE_SPEED * (isCrouched ? CROUCH_SPEED : 1) * (isSprinting ? SPRINT_SPEED : 1) * (isAiming ? 0.5f : 1);

        velocity.add(vectorForward.multiply(velocityForward).multiply(speed));
        velocity.add(vectorLeft.multiply(velocityLeft).multiply(speed));
        velocity.add(vectorRight.multiply(velocityRight).multiply(speed));
        velocity.add(vectorBack.multiply(velocityBack).multiply(speed));

        headBobbingMultiplier = lerp(headBobbingMultiplier, moving, 0.2f * deltaTime);
        headBobbing = (float) (Math.sin(timeElapsed / (80f * (isSprinting ? 0.6f : 1))) * 0.05f * (isCrouched ? 0.5f : 1f) * headBobbingMultiplier);

        cameraHeight = lerp(cameraHeight, isCrouched ? 1.5f : 2, 0.1f * deltaTime);

        if (isSprinting) {
            Window.getGameWindow().setFov(lerp(Window.getGameWindow().getFov(), 85.0f, 0.1f * deltaTime));
        } else if (isAiming) {
            Window.getGameWindow().setFov(lerp(Window.getGameWindow().getFov(), 70.0f, 0.1f * deltaTime));
        } else {
            Window.getGameWindow().setFov(lerp(Window.getGameWindow().getFov(), 80.0f, 0.1f * deltaTime));
        }

        float recoilValue = (isAiming ? 0.4f : 0.8f) + (velocitySum > 0.5f ? 0.5f : 0.0f) + (isCrouched ? -0.1f : 0.0f) + (isGrounded ? 0.0f : 1.0f) + (isSprinting ? 0.5f : 0.0f);
        recoil = lerp(recoil, recoilValue, 0.1f * deltaTime);

        position = wallRegion.collision(position, new Vector3f(velocity).multiply(deltaTime), 0.1f);

        Camera.setMainCameraPosition(new Vector3f(position).add(0, cameraHeight + (isGrounded ? headBobbing : 0),0));
    }

    public float getRecoil() {
        return recoil;
    }

    private void jump() {
        float magnitude = new Vector2f(velocity.getX(), velocity.getZ()).magnitude();
        velocity.setY(JUMP_HEIGHT * (1 + (magnitude)));
        velocity.set(velocity.getX(), velocity.getY(), velocity.getZ());
        isGrounded = false;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public static PlayerMovement getPlayerMovement() {
        return playerMovement;
    }

    public static void setPlayerMovement(PlayerMovement playerMovement) {
        PlayerMovement.playerMovement = playerMovement;
    }
}
