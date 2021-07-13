package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.io.Input;
import engine.math.Vector3f;
import engine.math.Vector4f;

import java.util.Collections;
import java.util.List;
import engine.objects.GameObject;
import main.Main;
import net.ClientHandler;
import net.packets.server.ChatPacket;
import org.lwjgl.glfw.GLFW;
import org.lwjglx.test.spaceinvaders.Game;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class InGameMenu extends Menu {

    private GameObject crosshair;

    private UiText chatInput;
    private GameObject chatPanel;
    private static List<ChatMessage> messages = Collections.synchronizedList(new ArrayList<>());
    private boolean isTyping = false;
    private String typedMessage = "";

    private Queue<MessageData> bufferedChatMessages = new ConcurrentLinkedQueue<>();

    @Override
    public void init() {
        crosshair = addObject(new GameObject(screen(1, 1, 1), UiBuilder.UICenter(p(32), Material.UI_CROSSHAIR)));
        chatPanel = addObject(new UiPanel(0, 2 - p(20), 1, 2, 12, 0.2f));
        chatInput = (UiText) addObject(new UiText(screen(p(2), 2 - p(18), 9), "> "));
        chatInput.setColor(0.9f, 0.9f, 0.9f, 1.0f);
    }

    @Override
    public void update() {
        if (bufferedChatMessages.size() > 0) {
            MessageData m = bufferedChatMessages.poll();
            messages.add((ChatMessage) addObject(new ChatMessage(m.message, m.red, m.green, m.blue)));
        }
        for (ChatMessage message : messages) {
            message.update(Main.getDeltaTime());
        }
        messages.removeIf(ChatMessage::shouldRemove);
        if (!isTyping) {
            chatPanel.setPosition(Vector3f.lerpdt(chatPanel.getPosition(), screen(1, 1.2f, 12), 0.004f));
            chatInput.setPosition(Vector3f.lerpdt(chatInput.getPosition(), screen(p(2), 2 - p(18) + 0.2f, 9), 0.004f));

            if (Input.isKeybindDown("chat")) {
                isTyping = true;
                Input.setTyping(true);
            }
        } else {
            chatPanel.setPosition(Vector3f.lerpdt(chatPanel.getPosition(), screen(1, 1, 12), 0.004f));
            chatInput.setPosition(Vector3f.lerpdt(chatInput.getPosition(), screen(p(2), 2 - p(18), 9), 0.004f));

            if (Input.isBindDown("esc")) {
                isTyping = false;
                Input.setTyping(false);
            }

            if (Input.isKeyDown(GLFW.GLFW_KEY_ENTER)) {
                isTyping = false;
                Input.setTyping(false);
                ClientHandler.sendChatPacket(typedMessage);
                typedMessage = "";
                chatInput.setText("> ");
            }

            String input = Input.getTypedCharacter();
            if (!input.equals("")) {
                typedMessage += input;
                chatInput.setText("> " + typedMessage);
            }
            if (Input.isKeyDown(GLFW.GLFW_KEY_BACKSPACE) && typedMessage.length() > 0) {
                typedMessage = typedMessage.substring(0, typedMessage.length() - 1);
                chatInput.setText("> " + typedMessage);
            }
        }

    }

    @Override
    public void unload() {

    }

    public void addBufferedChatMessage(String text, float r, float g, float b) {
        bufferedChatMessages.add(new MessageData(text, r, g, b));
    }

    public static void displayChatMessage(String text, float r, float g, float b) {
        UserInterface.inGameMenu.addBufferedChatMessage(text, r, g, b);
        for (ChatMessage message : messages) {
            message.bump();
        }
    }

    private static class ChatMessage extends UiText {

        private float time = 0, r = 1, g = 1, b = 1;
        private int position = 0;

        public ChatMessage(String text) {
            this(text, 1, 1, 1);
        }

        public ChatMessage(String text, float r, float g, float b) {
            super(screen(p(2), 2, 10), text);
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public void update(float deltaTime) {
            time += deltaTime;
            if (time <= 0.5f) {
                setColor(r, g, b, time * 2);
            } else if (time >= 7.5f) {
                setColor(r, g, b, 1 - ((time - 7.5f) * 2));
            } else {
                setColor(r, g, b, 1);
            }
            setPosition(Vector3f.lerpdt(getPosition(), calcScreenPos(position), 0.01f));
        }

        public void bump() {
            position ++;
        }

        private Vector3f calcScreenPos(int y) {
            return screen(p(2), 2 - p(40) - p(16) * y, 10);
        }

        public boolean shouldRemove() {
            return time > 8;
        }

    }

    private static class MessageData {

        public String message;
        public float red = 1, green = 1, blue = 1;

        public MessageData(String message, float red, float green, float blue) {
            this.message = message;
            this.red = red;
            this.blue = blue;
            this.green = green;
        }

    }

}
