package net;

import engine.math.Vector3f;
import net.packets.client.UserInputPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SynchronizedInputSender extends TimerTask {

    private static final HashMap<Integer, InputSnapshot> inputHistory = new ConcurrentHashMap<>();
    private static final Queue<InputSnapshot> tickInputs = new ConcurrentLinkedQueue<>();
    private static int subtick = 0;
    private static int tick = 0;

    public SynchronizedInputSender(int tick) {
        reset(tick);
    }

    @Override
    public void run() {
        subtick++;
        if (subtick >= 10) {
            if (!tickInputs.isEmpty()) {
                ArrayList<InputSnapshot> inputSnapshotsList = new ArrayList<>();
                while (!tickInputs.isEmpty()) {
                    inputSnapshotsList.add(tickInputs.poll());
                }
                UserInputPacket userInputPacket = new UserInputPacket(inputSnapshotsList);
                ClientHandler.client.sendUDP(userInputPacket);
            }
            subtick = 0;
        }
    }

    public static void addInput(ArrayList<String> inputs, Vector3f cameraRotation) {
        tickInputs.add(new InputSnapshot(inputs, cameraRotation, subtick));
    }

    public static void reset(int tick) {
        tickInputs.clear();
        subtick = 0;
        SynchronizedInputSender.tick = tick;
    }

    public static int getSubtick() {
        return subtick;
    }

    public static int getTick() {
        return tick;
    }
}
