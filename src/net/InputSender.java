package net;

import engine.io.Input;
import engine.math.Vector3f;
import game.PlayerMovement;
import main.Main;
import net.packets.client.UserInputPacket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InputSender extends TimerTask {

    private static InputState[] pendingInputs;
    private static final Queue<InputSnapshot> tickInputs = new ConcurrentLinkedQueue<>();

    protected static ArrayList<String> prevInputs = new ArrayList<>();

    private static int subtick = 0;
    private static int tick = 0;

    private static int inputSequence = 0;



    public InputSender(int tick) {
        reset(tick);
    }

    @Override
    public void run() {

        subtick++;
        PlayerMovement.applyMovement(prevInputs, Input.getKeybindList());
        addPending(new InputState(prevInputs, Input.getKeybindList(), PlayerMovement.getCameraRotation(), 2.0f / 1000.0f, getInputSequence()));
        incrementSequence();
        prevInputs = Input.getKeybindList();

        if (subtick >= 10) {
            ArrayList<InputSnapshot> inputSnapshotsList = new ArrayList<>();
            while (!tickInputs.isEmpty()) {
                inputSnapshotsList.add(tickInputs.poll());
            }
            UserInputPacket userInputPacket = new UserInputPacket(inputSnapshotsList, inputSequence);
            ClientHandler.client.sendUDP(userInputPacket);
            subtick = 0;
        }
    }

    public static void addInput(ArrayList<String> inputs, Vector3f cameraRotation) {
        tickInputs.add(new InputSnapshot(inputs, cameraRotation, subtick));
    }

    public static void reset(int tick) {
        tickInputs.clear();
        prevInputs = new ArrayList<>();
        pendingInputs = new InputState[512];
        subtick = 0;
        InputSender.tick = tick;
        inputSequence = 0;
    }

    public static int getSubtick() {
        return subtick;
    }

    public static int getTick() {
        return tick;
    }

    public static void removeRecentPending() {
        InputState[] newPendingInputs = new InputState[512];
        System.arraycopy(pendingInputs, 1, newPendingInputs, 0, 511);
        pendingInputs = newPendingInputs;
    }

    public static void addPending(InputState inputState) {
        for (int i = 0; i < 512; i++) {
            if (pendingInputs[i] == null) {
                pendingInputs[i] = inputState;
                return;
            }
        }
        removeRecentPending();
        pendingInputs[511] = inputState;
    }

    public static InputState[] getPendingInputs() {
        if (pendingInputs == null) {
            pendingInputs = new InputState[512];
        }
        return pendingInputs;
    }

    public static void incrementSequence() {
        inputSequence++;
    }

    public static Queue<InputSnapshot> getTickInputs() {
        return tickInputs;
    }

    public static int getInputSequence() {
        return inputSequence;
    }
}
