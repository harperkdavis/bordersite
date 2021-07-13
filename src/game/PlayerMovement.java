package game;

import engine.audio.AudioMaster;
import engine.audio.SoundEffect;
import engine.collision.Collision;
import engine.components.Component;
import engine.io.Input;
import engine.io.Window;
import engine.math.*;
import engine.objects.Camera;
import game.scene.Scene;
import main.Main;
import main.Global;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static engine.math.Mathf.*;

public class PlayerMovement {

    private static PlayerMovement playerMovement;

    private static Vector3f position;

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
    private Vector3f cameraRotation;
    private float cameraTilt;

    private float velocityForward = 0;
    private float velocityLeft = 0;
    private float velocityRight = 0;
    private float velocityBack = 0;

    private long movementStarted = System.currentTimeMillis(), lastStep = 0;

    private float cameraHeight = 2;

    public static float PLAYER_RADIUS = 0.5f;

    private float bobbingMultiplier = 0;
    private final static List<Component> colliders = new ArrayList<>();
    // Hipfire 1.0
    // ADS 0.5
    // Moving +0.5
    // In-Air +0.5
    // Crouching -0.2

    public PlayerMovement() {
        position = new Vector3f(0, 20, 0);
        cameraRotation = new Vector3f(0, 0, 0);
        Vector3f lockedRotation = new Vector3f(0, 0, 0);

    }

    private void updateCamera() {

        float mouseX = (float) Input.getMouseX();
        float mouseY = (float) Input.getMouseY();

        if (hasCameraControl && Window.isMouseLocked()) {

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
            if (Input.isKeybind("jump")) {
                velY = 10.0f * Main.getDeltaTime() * 120;
                position.add(0, 0.1f, 0);
                AudioMaster.playSound(SoundEffect.JUMP);
                isGrounded = false;
            }
        } else {
            velY -= 0.2f * Main.getDeltaTime() * 120;
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

        velocityLeft = Mathf.lerpdt(velocityLeft, Input.isKeybind("move_left") ? 1 : 0, 0.5f);
        velocityRight = Mathf.lerpdt(velocityRight, Input.isKeybind("move_right") ? 1 : 0, 0.5f);
        velocityForward = Mathf.lerpdt(velocityForward, Input.isKeybind("move_forward") ? 1 : 0, 0.5f);
        velocityBack = Mathf.lerpdt(velocityBack, Input.isKeybind("move_backward") ? 1 : 0, 0.5f);

        Vector3f inputVector = new Vector3f(velocityRight - velocityLeft, 0, velocityForward - velocityBack);

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

        Vector3f forwards = new Vector3f((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI));
        Vector3f right = new Vector3f((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI / 2), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI / 2));

        Vector3f movement = Vector3f.add(forwards.multiply(inputVector.getZ()), right.multiply(inputVector.getX()));

        float SPRINT_SPEED = 0.5f;
        float CROUCH_SPEED = 0.4f;
        float MOVE_SPEED = 5.0f;
        float movementSpeed = MOVE_SPEED * (isCrouching ? CROUCH_SPEED : 1.0f) * (1 + SPRINT_SPEED * sprintModifier * Mathf.lerp(1.0f, 1.2f, stamina / 200.0f)) * (isAiming ? 0.8f : 1.0f) * (isGrounded ? 1.0f : 0.01f) * (0.9f + stamina / 2000.0f) * (1.2f - fatigue * 0.4f);
        movement.multiply(movementSpeed);

        Vector3f previous = new Vector3f(position);

        if (isGrounded) {
            velY = 0;
        }

        velX += movement.getX();
        velY += movement.getY();
        velZ += movement.getZ();

        position.add(new Vector3f(velX * Main.getDeltaTime(), velY * Main.getDeltaTime(), velZ * Main.getDeltaTime()));

        boolean hasLanded = isGrounded;

        isGrounded = false;
        for (Component collider : colliders) {
            Collision result = collider.getCollision(previous, position, new Vector3f(velX, velY, velZ), isCrouching ? 1.0f : 1.5f, isGrounded);
            if (result != null) {
                position = new Vector3f(result.getPositionResult());

                velX = result.getVelocityResult().getX();
                velY = result.getVelocityResult().getY();
                velZ = result.getVelocityResult().getZ();

                if (result.isResultGrounded()) {
                    isGrounded = true;
                    velY = 0;
                }
            }
        }

        hasLanded = !hasLanded && isGrounded;
        if (hasLanded) {
            // cameraTilt += 1;
            // AudioMaster.playSound(SoundEffect.JUMP_LAND);
        }

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
        cameraTilt = Mathf.lerpdt(cameraTilt, 0, 0.05f);

        cameraHeight = Mathf.lerpdt(cameraHeight, isCrouching ? 1f : 1.5f, 0.01f) + bobbing * bobbingMultiplier * (isSprinting ? 2.5f : (isCrouching ? 1.5f : 1.0f)) * 0.01f;

        if (isAiming) {
            Camera.setFov(Mathf.lerpdt(Camera.getFov(), 65.0f, 0.01f));
        } else {
            Camera.setFov(Mathf.lerpdt(Camera.getFov(), 80.0f, 0.01f));
        }

    }

    public void flyingMovement() {
        Vector3f forward = new Vector3f((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI));
        Vector3f right = new Vector3f((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI / 2), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI / 2));
        Vector3f up = new Vector3f(0, 1, 0);

        float speed = (Input.isKey(GLFW.GLFW_KEY_LEFT_ALT) ? 400 : (Input.isKey(GLFW.GLFW_KEY_Q) ? 20 : 5));

        if (Input.isKey(GLFW.GLFW_KEY_W)) {
            position.add(Vector3f.multiply(forward, Vector3f.one().multiply(speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_A)) {
            position.add(Vector3f.multiply(right, Vector3f.one().multiply(-speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_SPACE)) {
            position.add(Vector3f.multiply(up, Vector3f.one().multiply(speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_S)) {
            position.add(Vector3f.multiply(forward, Vector3f.one().multiply(-speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_D)) {
            position.add(Vector3f.multiply(right, Vector3f.one().multiply(speed * Main.getDeltaTime())));
        }
        if (Input.isKey(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            position.add(Vector3f.multiply(up, Vector3f.one().multiply(-speed * Main.getDeltaTime())));
        }
    }

    public void update() {

        if (!Scene.isLoaded()) {
            return;
        }

        updateCamera();
        if (Global.BUILD_MODE) {
            flyingMovement();
        } else {
            updateMovement();
        }

        Camera.setMainCameraPosition(Vector3f.add(position, new Vector3f(0, cameraHeight, 0)));
    }

    public float getRecoil() {
        float recoil = 1.0f;
        return recoil;
    }

    public static Vector3f getPosition() {
        return position;
    }

    public static Vector3f getVelocity() {
        return new Vector3f(velX, velY, velZ);
    }

    public static float getSpeed() {
        return new Vector3f(velX * 4, velY, velZ * 4).magnitude();
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

    public static void setPosition(Vector3f position) {
        PlayerMovement.position = position;
    }

    public static void addCollisionComponent(Component collider) {
        colliders.add(collider);
    }
}
