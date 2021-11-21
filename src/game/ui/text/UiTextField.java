package game.ui.text;

import engine.io.Input;
import engine.math.Vector3f;
import net.ClientHandler;
import org.lwjgl.glfw.GLFW;

public class UiTextField extends UiText {

    public static UiTextField activeTextField = null;
    private String textEntry = "", textPrefix = "";
    private boolean password;

    public static void deselect() {
        if (activeTextField != null) {
            activeTextField.refresh(true);
            activeTextField = null;
        }
    }

    public static void update() {
        if (activeTextField == null) {
            return;
        }
        if (Input.isBindDown("esc")) {
            deselect();
        }

        String input = Input.getTypedCharacter();
        if (!input.equals("")) {
            activeTextField.addCharacter(input);
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_BACKSPACE)) {
            activeTextField.deleteCharacter();
        }
    }

    public static void select(UiTextField textField) {
        deselect();
        activeTextField = textField;
        activeTextField.refresh(false);
    }

    public UiTextField(Vector3f position, String textPrefix, int font, int textWidth, int horizontal, int vertical) {
        super(position, textPrefix, font, textWidth, horizontal, vertical);
        this.textPrefix = textPrefix;
    }

    public UiTextField(Vector3f position, String textPrefix) {
        super(position, textPrefix);
        this.textPrefix = textPrefix;
    }

    public UiTextField(Vector3f position, String textPrefix, int font) {
        super(position, textPrefix, font);
        this.textPrefix = textPrefix;
    }

    public UiTextField(Vector3f position, String textPrefix, int font, boolean password) {
        super(position, textPrefix, font);
        this.textPrefix = textPrefix;
        this.password = password;
    }

    public void refresh(boolean forceDeselect) {
        if (password) {
            String repeated = new String(new char[(textPrefix + textEntry).length()]).replace("\0", "*");
            setText(repeated + (activeTextField == this && !forceDeselect ? "_" : ""));
        } else {
            setText(textPrefix + textEntry + (activeTextField == this && !forceDeselect ? "_" : ""));
        }
    }

    public void addCharacter(String character) {
        textEntry += character;
        refresh(false);
    }

    public void deleteCharacter() {
        if (textEntry.length() != 0) {
            textEntry = textEntry.substring(0, textEntry.length() - 1);
        } else {
            textEntry = "";
        }
        refresh(false);
    }

    public void clearText() {
        textEntry = "";
        refresh(false);
    }

    public String getText() {
        return textEntry;
    }

    public void setTextEntry(String textEntry) {
        this.textEntry = textEntry;
        refresh(false);
    }

    public void setTyping(boolean typing) {
        if (typing) {
            activeTextField = this;
        } else {
            activeTextField = null;
        }
    }
}
