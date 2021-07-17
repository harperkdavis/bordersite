package net;

import engine.io.Printer;
import net.packets.client.UserInputPacket;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SynchronizedInputSender implements Runnable {

    private static final Queue<InputSnapshot> inputHistory = new ConcurrentLinkedQueue<>();
    private static boolean running = true;

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Printer.println(2, "Terminating input sender thread.");
            }
            if (!inputHistory.isEmpty()) {
                ArrayList<InputSnapshot> inputSnapshotsList = new ArrayList<>();
                while(!inputHistory.isEmpty()) {
                    inputSnapshotsList.add(inputHistory.poll());
                }
                UserInputPacket userInputPacket = new UserInputPacket(inputSnapshotsList);
                ClientHandler.client.sendUDP(userInputPacket);
            }
        }
    }

    public static void addInput(ArrayList<String> inputs) {
        inputHistory.add(new InputSnapshot(inputs));
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        SynchronizedInputSender.running = running;
    }
}
