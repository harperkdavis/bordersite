package game;

import engine.audio.AudioBuffer;
import engine.audio.AudioMaster;
import engine.audio.AudioSource;
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
import net.ClientHandler;
import net.InputSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerMovement {

    private static PerspectiveCamera camera = new PerspectiveCamera(Vector3f.zero(), Vector3f.zero(), 80.0f);

    private static Vector3f correctionOffset = new Vector3f(0, 0, 0), position = new Vector3f(0, 0.5f, 0);
    private static Vector3f cameraRotation = new Vector3f(0, 0, 0);

    private final static float PLAYER_SPEED = 8.0f;

    private static float velX = 0, velY = 0, velZ = 0;

    private static boolean isCrouching = false, isSprinting = false, isGrounded = true, isAiming = false, isMoving = false;
    private static float sprintModifier = 0.0f, crouchModifier = 0.0f;

    private static final boolean isPlayerActive = true;
    private static final boolean hasCameraControl = true;

    private static float health = 200;

    private static float preMouseX = 0, preMouseY = 0;

    private static float cameraTilt;
    private static float velocityForward = 0, velocityLeft = 0, velocityRight = 0, velocityBack = 0;
    private static float playerHeight = 2;

    public static final float PLAYER_RADIUS = 0.5f;
    private final static List<Component> colliders = new ArrayList<>();

    // Hipfire 1.0
    // ADS 0.5
    // Moving +0.5
    // In-Air +0.5
    // Crouching -0.2

    private final static Vector3f hipfirePos = new Vector3f(2.8f, -1.65f, -2.9f), aimPos = new Vector3f(0, -1.015f, -1.4f);
    private static Vector3f gunPos = hipfirePos.copy();
    private static Vector3f gunRecoil = Vector3f.zero(), cameraRecoil = Vector3f.zero();
    private static int ammo = 30;

    private static float shotAnimation = 0, reloadTime = 0;
    private static AudioSource gunshotSourceA, gunshotSourceB;
    private static boolean gunshotSource = false;

    public static void init() {
        gunshotSourceA = new AudioSource(false, false);
        gunshotSourceA.setBuffer(AudioBuffer.getSoundEffectBufferId(SoundEffect.ACP_9_SHOT));
        gunshotSourceB = new AudioSource(false, false);
        gunshotSourceB.setBuffer(AudioBuffer.getSoundEffectBufferId(SoundEffect.ACP_9_SHOT));
    }


    public static void setCamera() {
        Camera.setActiveCamera(camera);
    }

    public static void updateCamera() {

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

            camera.setRotation(cameraRotation.plus(cameraRecoil));

            if (!(accX == 0 && accY == 0)) {
                InputSender.addInput(Input.getKeybindList(), cameraRotation);
            }

            float gunShake = MOUSE_SENSITIVITY * 0.01f * (isAiming ? 0.2f : 1.0f);
            gunPos.add(accX * gunShake, accY * gunShake, 0);

        }

        preMouseX = mouseX;
        preMouseY = mouseY;
    }

    public static void applyMovement(List<String> prevInputs, List<String> inputs) {

        if (isGrounded) {
            velY = 0;
            if (!prevInputs.contains("jump") && inputs.contains("jump")) {
                velY += 0.016f;
                isGrounded = false;
                position.add(0, 0.1f, 0);
            }
        } else {
            velY -= 0.00008f;
        }

        isAiming = inputs.contains("aim");
        isCrouching = inputs.contains("crouch");
        isSprinting = inputs.contains("sprint") && (!isCrouching && !isAiming);
        sprintModifier = Mathf.lerp(sprintModifier, isSprinting ? 1.0f : 0.0f, 0.04f);
        crouchModifier = Mathf.lerp(crouchModifier, isCrouching ? 1.0f : 0.0f, 0.08f);

        velocityForward = Mathf.lerp(velocityForward, inputs.contains("move_forward") ? 1.0f : 0.0f, 0.04f);
        velocityBack = Mathf.lerp(velocityBack, inputs.contains("move_backward") ? 1.0f : 0.0f, 0.04f);
        velocityLeft = Mathf.lerp(velocityLeft, inputs.contains("move_left") ? 1.0f : 0.0f, 0.04f);
        velocityRight = Mathf.lerp(velocityRight, inputs.contains("move_right") ? 1.0f : 0.0f, 0.04f);

        Vector2f inputVector = new Vector2f(velocityRight - velocityLeft, velocityForward - velocityBack);
        isMoving = inputVector.magnitude() > 0.5f;
        if (inputVector.magnitude() > 1.0f) {
            inputVector.normalize();
        }
        float rotation = (float) -Math.toRadians(cameraRotation.getY());

        Vector3f forward = new Vector3f((float) Math.cos(rotation - Math.PI / 2), 0, (float) Math.sin(rotation - Math.PI / 2));
        Vector3f right = new Vector3f((float) Math.cos(rotation), 0, (float) Math.sin(rotation));

        Vector3f movement = forward.times(inputVector.getY()).plus(right.times(inputVector.getX()));

        float speedMultiplier = PLAYER_SPEED * (isSprinting ? 1.0f + 0.6f * sprintModifier : 1.0f) * (isCrouching ? 1.0f - crouchModifier * 0.5f : 1.0f) * (isAiming ? 0.6f : 1.0f);

        velX += movement.getX() * 0.0001f * speedMultiplier;
        velZ += movement.getZ() * 0.0001f * speedMultiplier;

        Vector3f previous = position.copy();
        position.add(velX, velY, velZ);

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

        velX = Mathf.lerp(velX, 0, 0.1f);
        velZ = Mathf.lerp(velZ, 0, 0.1f);

        playerHeight = Mathf.lerp(playerHeight, 1.5f - crouchModifier * 0.5f, 0.02f);

    }

    public static void updateCameraExtra() {
        cameraTilt = Mathf.lerpdt(cameraTilt, (velocityRight - velocityLeft) * 4.0f * (1.0f + crouchModifier), 0.01f);

        camera.setFov(Mathf.lerpdt(camera.getFov(), 80.0f + 8.0f * sprintModifier - (isAiming ? 20.0f : 0.0f), 0.02f));
    }

    private static long lastShot = 0;

    public static void updateGun() {
        if (shotAnimation > 0) {
            float inversePower = 1 - (1 - shotAnimation) * (1 - shotAnimation);
            Scene.getGunMuzzleFlash().setScale(new Vector3f(1, 1, -1).multiply(inversePower));
            Scene.getGunMuzzleFlash().setColor(1, inversePower, inversePower, inversePower);
            shotAnimation -= Main.getDeltaTime() * 20;
        } else {
            Scene.getGunMuzzleFlash().setVisible(false);
        }

        if (reloadTime <= 0) {
            if (Input.isKeybindDown("reload") && ammo < 30) {
                reloadTime = 4.0f;
                AudioMaster.playSound(SoundEffect.ACP_9_RELOAD);
            }
            if (Input.isKeybindDown("shoot") && ammo <= 0) {
                AudioMaster.playSound(SoundEffect.EMPTY);
            }
            if (Input.isKeybind("shoot") && System.currentTimeMillis() - lastShot > 55) {
                clientShoot();
                lastShot = System.currentTimeMillis();
            }
        } else {
            reloadTime -= Main.getDeltaTime();
            if (reloadTime <= 0) {
                ammo = 30;
            }
        }
        cameraRecoil = Vector3f.lerpdt(cameraRecoil, gunRecoil, 0.001f);
        gunRecoil = Vector3f.lerpdt(gunRecoil, Vector3f.zero(), 0.1f);

        gunPos.add(new Vector3f(-velX, -velY * 2.0f, -velZ).multiply(isAiming ? 0.2f : 0.4f));

        gunPos = Vector3f.lerpdt(gunPos, (isAiming ? aimPos : hipfirePos).minus(0, reloadTime > 0 ? 1 : 0, 0), 0.025f);
        Scene.setGunPosition(gunPos);


    }

    public static void update() {

        if (!Scene.isLoaded() || !ClientHandler.hasRegisteredTeam()) {
            return;
        }

        updateCameraExtra();
        updateGun();


        correctionOffset = Vector3f.lerpdt(correctionOffset, Vector3f.zero(), 1000.0f);
        camera.setPosition(Vector3f.add(position, new Vector3f(0, playerHeight, 0)).plus(correctionOffset));
    }

    public static void clientShoot() {
        if (ammo > 0) {
            shotAnimation = 1;
            gunPos.add(0, isAiming ? 0.001f : 0.1f, 0.2f);
            Scene.getGunMuzzleFlash().setVisible(true);
            Scene.getGunMuzzleFlash().setScale(new Vector3f(1, 1, -1).times(0.5f));
            Scene.getGunMuzzleFlash().setColor(1, 1, 1, 1);
            Scene.getGunMuzzleFlash().setRotation(new Vector3f(0, 0, new Random().nextFloat() * 720));
            if (gunshotSource) {
                gunshotSourceA.setPitch(0.95f + new Random().nextFloat() * 0.1f);
                gunshotSourceA.play();
            } else {
                gunshotSourceB.setPitch(0.95f + new Random().nextFloat() * 0.1f);
                gunshotSourceB.play();
            }
            gunshotSource = !gunshotSource;
            Random recoil = new Random();
            float recoilModifier = (isAiming ? 0.5f : 1.0f) * 4.0f;
            gunRecoil.add((1.0f + recoil.nextFloat() * 0.1f) * recoilModifier, recoil.nextFloat() * 0.5f * recoilModifier, 0);
            ammo--;
        }
    }

    public static void clientDead() {

    }



    public static void applyPlayer(Player player) {
        position = player.position;

        velX = player.velX;
        velY = player.velY;
        velZ = player.velZ;

        isCrouching = player.crouching;
        isSprinting = player.sprinting;
        isGrounded = player.grounded;
        isMoving = player.moving;
        isAiming = player.aiming;

        velocityForward = player.velocityForward;
        velocityBack = player.velocityBack;
        velocityLeft = player.velocityLeft;
        velocityRight = player.velocityRight;

        sprintModifier = player.sprintModifier;
        crouchModifier = player.crouchModifier;
    }

    public static float getRecoil() {
        float recoil = 1.0f;
        return recoil;
    }

    public static Vector3f getCorrectionOffset() {
        return correctionOffset;
    }

    public static void setCorrectionOffset(Vector3f correctionOffset) {
        PlayerMovement.correctionOffset = correctionOffset;
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

    public static int getAmmo() {
        return ammo;
    }

    public static float getReloadTime() {
        return reloadTime;
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
