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
    protected Vector3f position;

    protected boolean grounded, crouching, sprinting, moving;
    protected float velX, velY, velZ;
    protected float velocityLeft, velocityRight, velocityForward, velocityBack;
    protected float sprintModifier;
    protected Vector3f rotation;
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

    public void printData() {
        System.out.println("Player Info Dump");
        System.out.println("Id: " + playerId);
        System.out.println("UUID: " + uuid);
        System.out.println("Username: " + username);

        System.out.println("Team: " + team);
        System.out.println("Registered: " + registered);

        System.out.println("Grounded: " + grounded);
        System.out.println("Crouching: " + crouching);
        System.out.println("Sprinting: " + sprinting);
        System.out.println("Moving: " + moving);

        System.out.println("VelX: " + velX);
        System.out.println("VelY: " + velY);
        System.out.println("VelZ: " + velZ);

        System.out.println("VelLeft: " + velocityLeft);
        System.out.println("VelRight: " + velocityRight);
        System.out.println("VelForward: " + velocityForward);
        System.out.println("VelBack: " + velocityBack);

        System.out.println("SprintModifier: " + sprintModifier);
        System.out.println("Height: " + playerHeight);

        System.out.println("Rotation: " + rotation);
        System.out.println("Position: " + position);
    }

    public Player copy() {
        Player copy = new Player(playerId, username);
        copy.playerId = playerId;
        copy.uuid = uuid;
        copy.username = username;

        copy.team = team;
        copy.registered = registered;
        copy.inputSequence = inputSequence;

        copy.grounded = grounded;
        copy.crouching = crouching;
        copy.sprinting = sprinting;
        copy.moving = moving;

        copy.velX = velX;
        copy.velY = velY;
        copy.velZ = velZ;

        copy.velocityLeft = velocityLeft;
        copy.velocityRight = velocityRight;
        copy.velocityForward = velocityForward;
        copy.velocityBack = velocityBack;

        copy.sprintModifier = sprintModifier;
        copy.rotation = rotation;
        copy.playerHeight = playerHeight;

        return copy;
    }

}
