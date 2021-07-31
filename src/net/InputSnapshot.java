package net;

import engine.io.Input;
import engine.math.Vector3f;
import game.PlayerMovement;

import java.util.ArrayList;

public class InputSnapshot {

    private final int timestamp;
    private final ArrayList<String> inputs;
    private final Vector3f rotation;

    public InputSnapshot(ArrayList<String> inputs, Vector3f rotation, int timestamp) {
        this.inputs = inputs;
        this.rotation = rotation;
        this.timestamp = timestamp;
    }

    public InputSnapshot() {
        this.inputs = null;
        this.rotation = Vector3f.zero();
        this.timestamp = 0;
    }

    public static InputSnapshot getCurrentSnapshot(int timestamp) {
        return new InputSnapshot(Input.getKeybindList(), PlayerMovement.getCameraRotation(), timestamp);
    }

    public int getTimestamp() {
        return timestamp;
    }

    public ArrayList<String> getInputs() {
        return inputs;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        return "{timestamp: " + timestamp + ", " + inputs + ", rotation: " + rotation + "}";
    }
}