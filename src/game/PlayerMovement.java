package game;

import engine.audio.AudioMaster;
import engine.audio.SoundEffect;
import engine.collision.Collision;
import engine.components.Component;
import engine.io.Input;
import engine.io.Window;
import engine.math.*;
import engine.objects.camera.Camera;
import engine.objects.camera.PerspectiveCamera;
import game.scene.Scene;
import main.Main;
import main.Global;
import net.SynchronizedInputSender;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static engine.math.Mathf.*;

public class PlayerMovement {

    private static PerspectiveCamera camera = new PerspectiveCamera(Vector3f.zero(), Vector3f.zero(), 80.0f);

    private static Vector3f position = new Vector3f(0, 1.5f, 0);
    private static Vector3f cameraRotation = new Vector3f(0, 0, 0);

    private static float velX = 0, velY = 0.2f, velZ = 0;

    private static boolean isCrouching = false, isSprinting = false, isGrounded = true, isMoving = false;
    private static float sprintModifier = 0.0f;

    private static final boolean isPlayerActive = true;
    private static final boolean hasCameraControl = true;

    private static float health = 200, stamina = 200, healthChange = 0, fatigue = 0;

    private static float preMouseX = 0, preMouseY = 0;

    private static float cameraTilt;

    private static float velocityForward = 0, velocityLeft = 0, velocityRight = 0, velocityBack = 0;

    private static long movementStarted = System.currentTimeMillis(), lastStep = 0;

    private static float cameraHeight = 2;

    public static final float PLAYER_RADIUS = 0.5f;

    private static float bobbingMultiplier = 0;
    private final static List<Component> colliders = new ArrayList<>();
    // Hipfire 1.0
    // ADS 0.5
    // Moving +0.5
    // In-Air +0.5
    // Crouching -0.2

    public static void setCamera() {
        Camera.setActiveCamera(camera);
    }

    private static void updateCamera() {

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

            camera.setRotation(cameraRotation);

            if (!(accX == 0 && accY == 0)) {
                SynchronizedInputSender.addInput(Input.getKeybindList(), cameraRotation);
            }

        }

        preMouseX = mouseX;
        preMouseY = mouseY;
    }

    public static void applyMovement(float deltaTime) {

        if (isGrounded) {
            velY = 0;
            if (Input.isKeybindDown("jump")) {
                velY = 10.0f;
                position.add(0, 0.1f, 0);
                AudioMaster.playSound(SoundEffect.JUMP);
                isGrounded = false;
            }
        } else {
            velY -= 0.2f * deltaTime * 120;
        }
        
        if (Input.isKeybind("sprint")) {
            isSprinting = true;
        }
        sprintModifier = Mathf.lerpdt(sprintModifier, (isSprinting ? 1 : 0), 0.1f);
        
        if (Input.isKeybindDown("crouch")) {
            isCrouching = !isCrouching;
        }

        boolean isAiming = Input.isKeybind("aim");

        velocityLeft = Mathf.lerpdt(velocityLeft, Input.isKeybind("move_left") ? 1 : 0, 0.5f);
        velocityRight = Mathf.lerpdt(velocityRight, Input.isKeybind("move_right") ? 1 : 0, 0.5f);
        velocityForward = Mathf.lerpdt(velocityForward, Input.isKeybind("move_forward") ? 1 : 0, 0.5f);
        velocityBack = Mathf.lerpdt(velocityBack, Input.isKeybind("move_backward") ? 1 : 0, 0.5f);

        Vector3f inputVector = new Vector3f(velocityRight - velocityLeft, 0, velocityForward - velocityBack);

        boolean startedMoving = isMoving;
        isMoving = inputVector.magnitude() > 0.2f && isGrounded;

        startedMoving = !startedMoving && isMoving;

        isSprinting = isSprinting && (!isCrouching && !isAiming && isMoving);

        if (inputVector.magnitude() > 1) {
            inputVector.normalize();
        }

        Vector3f forwards = new Vector3f((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI));
        Vector3f right = new Vector3f((float) Math.sin(Math.toRadians(cameraRotation.getY()) + Math.PI / 2), 0, (float) Math.cos(Math.toRadians(cameraRotation.getY()) + Math.PI / 2));

        Vector3f movement = Vector3f.add(forwards.multiply(inputVector.getZ()), right.multiply(inputVector.getX()));

        float SPRINT_SPEED = 0.3f;
        float CROUCH_SPEED = 0.4f;
        float MOVE_SPEED = 4.0f;
        float movementSpeed = MOVE_SPEED * (isCrouching ? CROUCH_SPEED : 1.0f) * (1 + SPRINT_SPEED * sprintModifier) * (isAiming ? 0.8f : 1.0f) * (isGrounded ? 1.0f : 0.01f);
        movement.multiply(movementSpeed);

        Vector3f previous = new Vector3f(position);

        if (isGrounded) {
            velY = 0;
        }

        velX += movement.getX();
        velY += movement.getY();
        velZ += movement.getZ();

        position.add(new Vector3f(velX * deltaTime, velY * deltaTime, velZ * deltaTime));

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
            velX /= Math.pow(128.0f, deltaTime * 120);
            velY /= Math.pow(128.0f, deltaTime * 120);
            velZ /= Math.pow(128.0f, deltaTime * 120);
        } else {
            velX /= Math.pow(64.0f, deltaTime * 120);
            velZ /= Math.pow(64.0f, deltaTime * 120);
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

        if (isAiming) {
            camera.setFov(Mathf.lerpdt(camera.getFov(), 40.0f, 10.0f));
        } else {
            camera.setFov(Mathf.lerpdt(camera.getFov(), 80.0f + (isSprinting ? 4.0f : 0.0f), 0.01f));
        }

    }

    public static void flyingMovement() {
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

    public static void update() {

        if (!Scene.isLoaded()) {
            return;
        }

        updateCamera();
        if (Global.BUILD_MODE) {
            flyingMovement();
        } else {
            applyMovement(Main.getDeltaTime());
        }

        if (Input.isKeybindDown("crouch")) {
            isCrouching = !isCrouching;
        }

        cameraHeight = Mathf.lerpdt(cameraHeight, isCrouching ? 1f : 1.5f, 0.01f);

        camera.setPosition(Vector3f.add(position, new Vector3f(0, cameraHeight, 0)));
    }

    public static float getRecoil() {
        float recoil = 1.0f;
        return recoil;
    }

    public static Vector3f getPosition() {
        return position;
    }

    public static Vector3f getCameraRotation() {
        return cameraRotation;
    }

    public static void setCameraRotation(Vector3f cameraRotation) {
        PlayerMovement.cameraRotation = cameraRotation;
    }

    public static Vector3f getVelocity() {
        return new Vector3f(velX, velY, velZ);
    }

    public static void setVelocity(float velX, float velY, float velZ) {
        PlayerMovement.velX = velX;
        PlayerMovement.velY = velY;
        PlayerMovement.velZ = velZ;
    }

    public static float getSpeed() {
        return new Vector3f(velX * 4, velY, velZ * 4).magnitude();
    }

    private static void playFootstepSound() {
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

    public static PerspectiveCamera getCamera() {
        return camera;
    }
}
