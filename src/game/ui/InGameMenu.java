package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.io.Input;
import engine.math.Mathf;
import engine.math.Vector2f;
import engine.math.Vector3f;

import java.util.*;

import engine.math.Vector4f;
import engine.objects.GameObject;
import engine.objects.camera.Camera;
import game.PlayerMovement;
import game.ui.text.UiText;
import main.Main;
import net.ClientHandler;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class InGameMenu extends Menu {

    private GameObject crosshair;
    private float crosshairTransparency = 1;

    private UiText chatInput;
    private GameObject chatPanel;
    private static List<ChatMessage> messages = Collections.synchronizedList(new ArrayList<>());
    private boolean isTyping = false;
    private String typedMessage = "";

    private Queue<MessageData> bufferedChatMessages = new ConcurrentLinkedQueue<>();

    private GameObject healthIcon, ammoIcon, timeIcon, killsIcon, redFlagIcon, blueFlagIcon;
    private UiText healthText, ammoText;

    private static UiText timeText, killsText, redScoreText, blueScoreText;
    private static UiText deadText, deathMessageText;

    @Override
    public void init() {
        crosshair = addObject(new GameObject(screen(1, 1, 1), UiBuilder.UICenter(p(32), Material.UI_CROSSHAIR)));
        chatPanel = addObject(new UiPanel(0, 2 - p(20), 1, 2, 12, 0.2f));
        chatInput = (UiText) addObject(new UiText(screen(p(2), 2 - p(18), 9), "> "));
        chatInput.setColor(0.9f, 0.9f, 0.9f, 1.0f);

        healthIcon = addObject(new GameObject(screen(1.8f, 1.95f, 4), UiBuilder.UICenterUV(p(16), 1, 1, Material.UI_ICONS, new Vector2f(0, 0), new Vector2f(0.25f, 0.25f))));
        ammoIcon = addObject(new GameObject(screen(1.9f, 1.95f, 4), UiBuilder.UICenterUV(p(16), 1, 1, Material.UI_ICONS, new Vector2f(0.25f, 0), new Vector2f(0.5f, 0.25f))));
        timeIcon = addObject(new GameObject(screen(0.025f, 0.05f, 4), UiBuilder.UICenterUV(p(16), 1, 1, Material.UI_ICONS, new Vector2f(0.5f, 0), new Vector2f(0.75f, 0.25f))));
        killsIcon = addObject(new GameObject(screen(0.025f, 0.05f + p(18), 4), UiBuilder.UICenterUV(p(16), 1, 1, Material.UI_ICONS, new Vector2f(0.75f, 0), new Vector2f(1.0f, 0.25f))));

        redFlagIcon = addObject(new GameObject(screen(0.025f, 0.05f + p(36), 4), UiBuilder.UICenterUV(p(16), 1, 1, Material.UI_ICONS, new Vector2f(0.0f, 0.25f), new Vector2f(0.25f, 0.5f))));
        blueFlagIcon = addObject(new GameObject(screen(0.025f, 0.05f + p(54), 4), UiBuilder.UICenterUV(p(16), 1, 1, Material.UI_ICONS, new Vector2f(0.0f, 0.25f), new Vector2f(0.25f, 0.5f))));

        redFlagIcon.setColor(210.0f / 256.0f, 41.0f / 256.0f, 45.0f / 256.0f, 1);
        blueFlagIcon.setColor(23.0f / 256.0f, 97.0f / 256.0f, 176.0f / 256.0f, 1);

        healthText = (UiText) addObject(new UiText(screen(1.8f + p(8), 1.95f - p(8), 4), "200"));
        ammoText = (UiText) addObject(new UiText(screen(1.9f + p(8), 1.95f - p(8), 4), "30"));
        timeText = (UiText) addObject(new UiText(screen(0.025f + p(8), 0.05f - p(8), 4), "0:00"));
        killsText = (UiText) addObject(new UiText(screen(0.025f + p(8), 0.05f + p(10), 4), "0 Kills"));

        redScoreText = (UiText) addObject(new UiText(screen(0.025f + p(8), 0.05f + p(28), 4), "0"));
        blueScoreText = (UiText) addObject(new UiText(screen(0.025f + p(8), 0.05f + p(46), 4), "0"));

        redScoreText.setColor(210.0f / 256.0f, 41.0f / 256.0f, 45.0f / 256.0f, 1);
        blueScoreText.setColor(23.0f / 256.0f, 97.0f / 256.0f, 176.0f / 256.0f, 1);

        deadText = (UiText) addObject(new UiText(screen(0.5f - p(16), 1 - p(12), 4), "You have died."));
        deathMessageText = (UiText) addObject(new UiText(screen(0.5f - p(16), 1, 4), "Killed By: NONE"));
    }

    @Override
    public void update() {
        crosshairTransparency = Mathf.lerpdt(crosshairTransparency, Input.isKeybind("aim") ? 0.0f : 1.0f, 0.1f);
        crosshair.setColor(1, 1, 1, crosshairTransparency);

        healthText.setText((int) PlayerMovement.getHealth() + "");
        float hp = PlayerMovement.getHealth() / 200.0f;
        healthText.setColor(Mathf.lerp(0, 1, hp + 0.4f), Mathf.lerp(0, 1, hp), Mathf.lerp(1, 0, (1 - hp * hp)), 1);
        ammoText.setText(PlayerMovement.getAmmo() + "");

        deadText.setVisible(PlayerMovement.isDead());
        deathMessageText.setVisible(PlayerMovement.isDead());

        crosshair.setVisible(!PlayerMovement.isDead());
        healthText.setVisible(!PlayerMovement.isDead());
        healthIcon.setVisible(!PlayerMovement.isDead());
        ammoText.setVisible(!PlayerMovement.isDead());
        ammoIcon.setVisible(!PlayerMovement.isDead());

        if (PlayerMovement.getReloadTime() > 0) {
            ammoText.setColor(0.5f, 0.5f, 0.5f, 1);
        } else {
            if (PlayerMovement.getAmmo() <= 5) {
                ammoText.setColor(1, 0, 0, 1);
            } else {
                ammoText.setColor(1, 1, 1, 1);
            }
        }


        updateChat();
    }

    public void updateChat() {

        while (bufferedChatMessages.size() > 0) {
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

    public static UiText getTimeText() {
        return timeText;
    }

    public static UiText getKillsText() {
        return killsText;
    }

    public static UiText getRedScoreText() {
        return redScoreText;
    }

    public static UiText getBlueScoreText() {
        return blueScoreText;
    }

    public static UiText getDeadText() {
        return deadText;
    }

    public static UiText getDeathMessageText() {
        return deathMessageText;
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
