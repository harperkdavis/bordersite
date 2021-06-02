package game;

import engine.audio.AudioMaster;
import engine.audio.SoundEffect;
import engine.collision.Collider;
import engine.collision.Collision;
import engine.io.Input;
import engine.io.Window;
import engine.math.*;
import engine.objects.Camera;
import game.scene.Scene;
import main.Global;
import main.Main;
import net.Client;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static engine.math.Mathf.*;

public class PlayerMovement {

    private static PlayerMovement playerMovement;

    private static Vector3 position;

    private static float velX = 0;
    private static float velY = 0;
    private static float velZ = 0;

    private boolean isCrouching = false;
    private boolean isSprinting = false;
    private boolean isGrounded = true;
    private boolean isMoving;
    private float sprintModifier = 0.0f;

    private static final boolean isPlayerActive = true;
    private static final boolean hasCameraControl = true;

    private static float health = 200, stamina = 200, healthChange = 0, fatigue = 0;

    private float preMouseX = 0, preMouseY = 0;
    private Vector3 cameraRotation;
    private float cameraTilt;

    private float velocityForward = 0;
    private float velocityLeft = 0;
    private float velocityRight = 0;
    private float velocityBack = 0;

    private long movementStarted = System.currentTimeMillis(), lastStep = 0;

    private float cameraHeight = 2;

    private final float JUMP_HEIGHT = 0.1f;
    private final float AIR_FRICTION = 3.0f;
    private final float GROUND_FRICTION = 5.0f;
    private final float GRAVITY = 0.006f;

    private float bobbingMultiplier = 0;
    private final static List<Collider> colliders = new ArrayList<>();;
    // Hipfire 1.0
    // ADS 0.5
    // Moving +0.5
    // In-Air +0.5
    // Crouching -0.2

    public PlayerMovement() {
        position = new Vector3(12, 6, 12);
        cameraRotation = new Vector3(0, 0, 0);
        Vector3 lockedRotation = new Vector3(0, 0, 0);

    }

    private void updateCamera() {

        float mouseX = (float) Input.getMouseX();
        float mouseY = (float) Input.getMouseY();

        if (hasCameraControl && Window.getGameWindow().isMouseLocked()) {

            float accX = mouseX - preMouseX;
            float accY = mouseY - preMouseY;

            float MOUSE_SENSITIVITY = 0.04f;
            cameraRotation = cameraRotation.add(-accY * MOUSE_SENSITIVITY, -accX * MOUSE_SENSITIVITY, 0);

            if (cameraRotation.getX() > 89) {
                cameraRotation.setX(89);
            }

            if (cameraRotation.getX() < -89) {
                cameraRotation.setX(-89);
            }

            cameraRotation.setZ(cameraTilt);

            Camera.setMainCameraRotation(cameraRotation);

        }

        preMouseX = mouseX;
        preMouseY = mouseY;
    }

    public void updateMovement() {

        if (isGrounded) {
            velY = 0;
            if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
                velY = 10.0f * Main.getDeltaTime() * 120;
                position.add(0, 0.02f, 0);
                AudioMaster.playSound(SoundEffect.JUMP);
                isGrounded = false;
            }
        } else {
            velY -= 0.2f * Main.getDeltaTime() * 120;
        }

        if (position.getY() < Scene.getTerrainHeight(position.getX(), position.getZ())) {
            position.setY(Scene.getTerrainHeight(position.getX(), position.getZ()));
        }

        int SPRINT_KEY = GLFW.GLFW_KEY_LEFT_SHIFT;
        if (Input.isKey(SPRINT_KEY)) {
            if (stamina > 40f) {
                isSprinting = true;
            }
        }
        sprintModifier = Mathf.lerpdt(sprintModifier, (isSprinting ? 1 : 0), 0.1f);

        int CROUCH_KEY = GLFW.GLFW_KEY_LEFT_CONTROL;
        if (Input.isKeyDown(CROUCH_KEY)) {
            isCrouching = !isCrouching;
        }

        boolean isAiming = Input.isMouseButton(1);

        velocityLeft = Mathf.lerpdt(velocityLeft, Input.isKey(GLFW.GLFW_KEY_A) ? 1 : 0, 0.5f);
        velocityRight = Mathf.lerpdt(velocityRight, Input.isKey(GLFW.GLFW_KEY_D) ? 1 : 0, 0.5f);
        velocityForward = Mathf.lerpdt(velocityForward, Input.isKey(GLFW.GLFW_KEY_W) ? 1 : 0, 0.5f);
        velocityBack = Mathf.lerpdt(velocityBack, Input.isKey(GLFW.GLFW_KEY_S) ? 1 : 0, 0.5f);

        Vector3 inputVector = new Vector3(velocityRight - velocityLeft, 0, velocityForward - velocityBack);

        boolean startedMoving = isMoving;
        isMoving = inputVector.magnitude() > 0.2f && isGrounded;

        startedMoving = !startedMoving && isMoving;

        isSprinting = isSprinting && (!isCrouching && !isAiming && isMoving);

        if (isSprinting) {
            stamina -= 4 * Main.getDeltaTime() * (1 + fatigue);
            fatigue += 0.0001f * Main.getDeltaTime();
            if (stamina < 40 && stamina >= 1) {
                fatigue += (40 / stamina) * 0.0009f * Main.getDeltaTime();
            } else if (stamina < 1) {
                fatigue += 0.0009f * Main.getDeltaTime();
            }
            if (stamina < 0) {
                fatigue += 0.001f * Main.getDeltaTime();
                stamina = 0;
                isSprinting = false;
            }
        } else {
            fatigue -= 0.00002f * Main.getDeltaTime();
            stamina += (4.0f * (isMoving ? 0.5f : 1.0f) * (isCrouching ? (isMoving ? 0.0f : 1.5f) : 1.0f) * (1 - fatigue * 0.5f)) * Main.getDeltaTime();
            if (stamina > 200) {
                fatigue -= 0.0004f * Main.getDeltaTime();
                stamina = 200;
            }
        }
        fatigue = clamp(fatigue, 0, 1);

        if (inputVector.magnitude() > 1) {
            inputVector.normalize();
        }

        Vector3 forwards = new Vector3((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI));
        Vector3 right = new Vector3((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI / 2), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI / 2));

        Vector3 movement = Vector3.add(forwards.multiply(inputVector.getZ()), right.multiply(inputVector.getX()));

        float SPRINT_SPEED = 0.5f;
        float CROUCH_SPEED = 0.4f;
        float MOVE_SPEED = 5.0f;
        float movementSpeed = MOVE_SPEED * (isCrouching ? CROUCH_SPEED : 1.0f) * (1 + SPRINT_SPEED * sprintModifier * Mathf.lerp(1.0f, 1.2f, stamina / 200.0f)) * (isAiming ? 0.8f : 1.0f) * (isGrounded ? 1.0f : 0.01f) * (0.9f + stamina / 2000.0f) * (1.2f - fatigue * 0.4f);
        movement.multiply(movementSpeed);

        Vector3 previous = new Vector3(position);

        velX += movement.getX();
        velY += movement.getY();
        velZ += movement.getZ();

        position.add(new Vector3(velX * Main.getDeltaTime(), velY * Main.getDeltaTime(), velZ * Main.getDeltaTime()));

        boolean hasLanded = isGrounded;
        isGrounded = position.getY() < Scene.getTerrainHeight(position.getX(), position.getZ()) + (isGrounded ? 0.1f : 0);

        for (Collider collider : colliders) {
            Collision result = collider.getCollision(previous, position, new Vector3(velX, velY, velZ), isCrouching ? 1.65f : 2.15f);
            if (result != null) {
                position = new Vector3(result.getPositionResult());

                velX = result.getVelocityResult().getX();
                velY = result.getVelocityResult().getY();
                velZ = result.getVelocityResult().getZ();

                if (result.isResultGrounded() && velY <= 0) {
                    isGrounded = true;
                }
            }
        }

        float groundedHeight = Scene.getTerrainHeight(position.getX(), position.getZ());
        for (Collider collider : colliders) {
            if (collider.isGrounded(position) && velY <= 0) {
                isGrounded = true;
                groundedHeight = Math.max(groundedHeight, collider.getGroundedHeight(position));
            }
        }

        if (isGrounded) {
            position.setY(groundedHeight);
            velY = 0;
        }

        hasLanded = !hasLanded && isGrounded;
        if (hasLanded) {
            cameraHeight -= 0.2f;
            cameraTilt += 10;
            AudioMaster.playSound(SoundEffect.JUMP_LAND);
        }

        position.setX(Mathf.clamp(position.getX(), 0, 510 * Scene.getScaleX()));
        position.setY(Mathf.clamp(position.getY(), -1, 1000));
        position.setZ(Mathf.clamp(position.getZ(), 0, 510 * Scene.getScaleX()));

        if (isGrounded) {
            velX /= 8.0f / (Main.getDeltaTime() * 120);
            velY /= 8.0f / (Main.getDeltaTime() * 120);
            velZ /= 8.0f / (Main.getDeltaTime() * 120);
        } else {
            velX /= 1.002f / (Main.getDeltaTime() * 120);
            velZ /= 1.002f / (Main.getDeltaTime() * 120);
        }

        if (startedMoving) {
            movementStarted = System.currentTimeMillis();
            lastStep = 0;
        }

        bobbingMultiplier = Mathf.lerpdt(bobbingMultiplier, (isMoving ? 1 : 0), 0.1f);

        if (System.currentTimeMillis() >= lastStep + (isSprinting ? 420 : (isCrouching ? 600 : 500)) && isMoving) {
            playFootstepSound();
            lastStep = System.currentTimeMillis();
        }

        float bobbingTime = (System.currentTimeMillis() - movementStarted) / ((isSprinting ? 600.0f : (isCrouching ? 1000.0f : 600.0f)) / 9.0f);
        float bobbing = (float) Math.sin(bobbingTime);
        cameraTilt = Mathf.lerpdt(cameraTilt,bobbingMultiplier * (float) Math.sin(bobbingTime / 2) * 0.2f, 0.05f);

        cameraHeight = Mathf.lerpdt(cameraHeight, isCrouching ? 1.5f : 2, 0.01f) + bobbing * bobbingMultiplier * (isSprinting ? 2.5f : (isCrouching ? 1.5f : 1.0f)) * 0.01f;

    }

    public void flyingMovement() {
        Vector3 forward = new Vector3((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI));
        Vector3 right = new Vector3((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI / 2), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI / 2));
        Vector3 up = new Vector3(0, 1, 0);

        float speed = (Input.isKey(GLFW.GLFW_KEY_LEFT_ALT) ? 400 : (Input.isKey(GLFW.GLFW_KEY_Q) ? 20 : 5));

        if (Input.isKey(GLFW.GLFW_KEY_W)) {
            position.add(Vector3.multiply(forward, Vector3.one().multiply(speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_A)) {
            position.add(Vector3.multiply(right, Vector3.one().multiply(-speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_SPACE)) {
            position.add(Vector3.multiply(up, Vector3.one().multiply(speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_S)) {
            position.add(Vector3.multiply(forward, Vector3.one().multiply(-speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_D)) {
            position.add(Vector3.multiply(right, Vector3.one().multiply(speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            position.add(Vector3.multiply(up, Vector3.one().multiply(-speed * Main.getDeltaTime())));
        }
    }

    public void update() {

        if (!(Client.isConnected() && Scene.isLoaded())) {
            return;
        }

        updateCamera();
        if (Global.BUILD_MODE) {
            flyingMovement();
        } else {
            updateMovement();
        }

        Camera.setMainCameraPosition(Vector3.add(position, new Vector3(0, cameraHeight, 0)));
    }

    public float getRecoil() {
        float recoil = 1.0f;
        return recoil;
    }

    public static Vector3 getPosition() {
        return position;
    }

    public static Vector3 getVelocity() {
        return new Vector3(velX, velY, velZ);
    }

    public static float getSpeed() {
        return new Vector3(velX * 4, velY, velZ * 4).magnitude();
    }

    public static PlayerMovement getPlayerMovement() {
        return playerMovement;
    }

    public static void setPlayerMovement(PlayerMovement playerMovement) {
        PlayerMovement.playerMovement = playerMovement;
    }

    private void playFootstepSound() {
        int footstep = new Random().nextInt(8);
        switch (footstep) {
            case 0 -> AudioMaster.playSound(SoundEffect.FOOTSTEP01);
            case 1 -> AudioMaster.playSound(SoundEffect.FOOTSTEP02);
            case 2 -> AudioMaster.playSound(SoundEffect.FOOTSTEP03);
            case 3 -> AudioMaster.playSound(SoundEffect.FOOTSTEP04);
            case 4 -> AudioMaster.playSound(SoundEffect.FOOTSTEP05);
            case 5 -> AudioMaster.playSound(SoundEffect.FOOTSTEP06);
            case 6 -> AudioMaster.playSound(SoundEffect.FOOTSTEP07);
            default -> AudioMaster.playSound(SoundEffect.FOOTSTEP08);
        }
    }

    public static float getHealth() {
        return health;
    }

    public static void setHealth(float health) {
        PlayerMovement.health = health;
    }

    public static float getStamina() {
        return stamina;
    }

    public static void setStamina(float stamina) {
        PlayerMovement.stamina = stamina;
    }

    public static float getHealthChange() {
        return healthChange;
    }

    public static void setHealthChange(float healthChange) {
        PlayerMovement.healthChange = healthChange;
    }

    public static void setPosition(Vector3 position) {
        PlayerMovement.position = position;
    }

    public static void addCollisionRegion(Collider collider) {
        colliders.add(collider);
    }
}
