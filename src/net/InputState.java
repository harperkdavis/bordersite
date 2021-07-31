package net;

import engine.io.Input;
import engine.math.Vector3f;
import game.PlayerMovement;

import java.util.ArrayList;

public class InputState {

    private final int sequence;
    private final float deltaTime;
    private final ArrayList<String> prevInputs;
    private final ArrayList<String> inputs;
    private final Vector3f rotation;

    public InputState(ArrayList<String> prevInputs, ArrayList<String> inputs, Vector3f rotation, float deltaTime, int sequence) {
        this.prevInputs = prevInputs;
        this.inputs = inputs;
        this.rotation = rotation;
        this.deltaTime = deltaTime;
        this.sequence = sequence;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public int getSequence() {
        return sequence;
    }

    public ArrayList<String> getPrevInputs() {
        return prevInputs;
    }

    public ArrayList<String> getInputs() {
        return inputs;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        return "{time: " + sequence + ", " + inputs + ", rotation: " + rotation + "}";
    }

}
