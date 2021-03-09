package game;

import engine.io.Input;
import engine.io.Window;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.math.Region3f;
import engine.objects.Camera;
import game.world.World;
import net.Client;
import org.lwjgl.glfw.GLFW;

import static engine.math.Mathf.lerp;

import java.util.ArrayList;
import java.util.List;

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

    private final float MOVE_SPEED = 0.07f, SMOOTHING = 0.6f, CROUCH_SPEED = 0.6f, SPRINT_SPEED = 1.4f, JUMP_HEIGHT = 0.62f, AIR_FRICTION = 1.5f, GROUND_FRICTION = 5.0f;
    private final float GRAVITY = 0.006f;

    private float averageFPS = 120f;

    private long timeStart;
    private int timeElapsed;

    private float recoil = 1.0f;

    private float root2 = (float) Math.sqrt(2);

    private static List<Region3f> collision;
    // Hipfire 1.0
    // ADS 0.5
    // Moving +0.5
    // In-Air +0.5
    // Crouching -0.2

    public PlayerMovement() {
        position = new Vector3f(0, 0, 0);
        velocity = new Vector3f(0, 0, 0);
        timeStart = System.currentTimeMillis();
        collision = new ArrayList<>();
    }

    public void update() {

        if (!(Client.isConnected() && World.isLoaded())) {
            return;
        }

        timeElapsed = (int) (System.currentTimeMillis() - timeStart);

        isCrouched = Input.isKey(GLFW.GLFW_KEY_LEFT_CONTROL);
        isSprinting = Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT);
        isAiming = Input.isMouseButton(1);

        boolean feetWithinWall = false;

        if (isGrounded && position.getY() <= World.getTerrainHeight(position.getX(), position.getZ()) + 0.1f) {
            position.setY(World.getTerrainHeight(position.getX(), position.getZ()));
        }

        isGrounded = position.getY() <= World.getTerrainHeight(position.getX(), position.getZ()) || feetWithinWall;

        if (isGrounded) {
            if (Input.isKey(GLFW.GLFW_KEY_SPACE)) {
                jump();
                isGrounded = false;
            } else {
                velocity.setX(velocity.getX() / (1 + (GROUND_FRICTION)));
                velocity.setZ(velocity.getZ() / (1 + (GROUND_FRICTION)));
                velocity.setY(0);
            }
        } else {
            velocity.setX(velocity.getX() / (1 + (AIR_FRICTION)));
            velocity.setZ(velocity.getZ() / (1 + (AIR_FRICTION)));
            velocity.setY(velocity.getY() - (GRAVITY) * Window.getDeltaTime());
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

        headBobbingMultiplier = lerp(headBobbingMultiplier, moving, 0.2f * Window.getDeltaTime());
        headBobbing = (float) (Math.sin(timeElapsed / (80f * (isSprinting ? 0.6f : 1))) * 0.05f * (isCrouched ? 0.5f : 1f) * headBobbingMultiplier);

        cameraHeight = lerp(cameraHeight, isCrouched ? 1.5f : 2, 0.1f * Window.getDeltaTime());

        if (isSprinting) {
            Window.getGameWindow().setFov(lerp(Window.getGameWindow().getFov(), 85.0f, 0.1f * Window.getDeltaTime()));
        } else if (isAiming) {
            Window.getGameWindow().setFov(lerp(Window.getGameWindow().getFov(), 5.0f, 0.05f * Window.getDeltaTime()));
        } else {
            Window.getGameWindow().setFov(lerp(Window.getGameWindow().getFov(), 80.0f, 0.1f * Window.getDeltaTime()));
        }

        float recoilValue = (isAiming ? 0.4f : 0.8f) + (velocitySum > 0.5f ? 0.5f : 0.0f) + (isCrouched ? -0.1f : 0.0f) + (isGrounded ? 0.0f : 1.0f) + (isSprinting ? 0.5f : 0.0f);
        recoil = lerp(recoil, recoilValue, 0.1f * Window.getDeltaTime());

        for (Region3f region : collision) {
            // position = region.collision(position, , 0.1f);
        }

        position = Vector3f.add(position, new Vector3f(velocity).multiply(Window.getDeltaTime()));

        // Position Bounds
        if (position.getX() < 0) {
            position.setX(0);
        }
        if (position.getZ() < 0) {
            position.setZ(0);
        }
        Vector3f scale = World.getWorld().groundPlane.getScale();
        if (position.getX() > 512 * scale.getX()) {
            position.setX(512 * scale.getX());
        }
        if (position.getZ() > 512 * scale.getZ()) {
            position.setZ(512 * scale.getZ());
        }
        if (position.getY() < World.getTerrainHeight(position.getX(), position.getZ())) {
            position.setY(World.getTerrainHeight(position.getX(), position.getZ()));
        }
        if (position.getY() > 1000) {
            position.setY(1000);
        }

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

    public static void addCollisionRegion(Region3f region) {
        collision.add(region);
    }
}
