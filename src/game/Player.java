package game;

import engine.math.Vector3f;

import java.util.UUID;

public class Player {


    private int playerId;
    private String uuid;

    private String username;

    private int team = 0;
    private boolean registered = false;
    private int inputSequence = 0;

    // MOVEMENT
    protected Vector3f position = Vector3f.oneY();
    protected Vector3f rotation = Vector3f.zero();

    protected boolean grounded, crouching, sprinting, moving, aiming;
    protected float velX, velY, velZ;
    protected float velocityLeft, velocityRight, velocityForward, velocityBack;
    protected float sprintModifier, crouchModifier;
    protected float playerHeight;

    public Player(int playerId, String username) {
        this.playerId = playerId;
        this.uuid = UUID.randomUUID().toString();
        this.username = username;
    }

    public Player() {
        this.playerId = 0;
        this.uuid = "";
        this.username = "";
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public int getInputSequence() {
        return inputSequence;
    }

    public void setInputSequence(int inputSequence) {
        this.inputSequence = inputSequence;
    }

    public Vector3f getPosition() {
        return position;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public boolean isCrouching() {
        return crouching;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public boolean isAiming() {
        return aiming;
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public float getVelZ() {
        return velZ;
    }

    public float getSprintModifier() {
        return sprintModifier;
    }

    public float getCrouchModifier() {
        return crouchModifier;
    }

    public boolean isMoving() {
        return moving;
    }

    public float getVelocityLeft() {
        return velocityLeft;
    }

    public float getVelocityRight() {
        return velocityRight;
    }

    public float getVelocityForward() {
        return velocityForward;
    }

    public float getVelocityBack() {
        return velocityBack;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getPlayerHeight() {
        return playerHeight;
    }

    public Player copy() {
        Player copy = new Player(playerId, username);
        copy.playerId = playerId;
        copy.uuid = uuid;
        copy.username = username;

        copy.team = team;
        copy.registered = registered;
        copy.inputSequence = inputSequence;

        copy.position = position.copy();
        copy.rotation = rotation.copy();

        copy.grounded = grounded;
        copy.crouching = crouching;
        copy.sprinting = sprinting;
        copy.moving = moving;
        copy.aiming = aiming;

        copy.velX = velX;
        copy.velY = velY;
        copy.velZ = velZ;

        copy.velocityLeft = velocityLeft;
        copy.velocityRight = velocityRight;
        copy.velocityForward = velocityForward;
        copy.velocityBack = velocityBack;

        copy.sprintModifier = sprintModifier;
        copy.crouchModifier = crouchModifier;
        copy.rotation = rotation;
        copy.playerHeight = playerHeight;

        return copy;
    }

}
