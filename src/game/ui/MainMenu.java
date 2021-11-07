package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.io.Input;
import engine.io.Window;
import engine.math.Mathf;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;
import game.ui.text.UiText;
import game.ui.text.UiTextField;
import main.ConfigLoader;
import main.Global;
import main.Main;
import net.ClientHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static game.ui.UserInterface.*;

public class MainMenu extends Menu {

    private static List<UiButton> buttons = new ArrayList<>();
    private static Map<String, UiText> keybindValues = new HashMap<>();

    private GameObject logo;
    private GameObject version;

    private GameObject menu, sideBar;

    private UiButton playButton, optionsButton, disconnectButton, quitButton;

    private GameObject serversLabel, optionsLabel;
    private GameObject displayOptionsLabel, controlOptionsLabel, keybindsOptionsLabel;

    private UiPanel loginPanel;
    private UiTextField usernameField, passwordField;
    private UiTextField ipField;

    public static UiText loginResult, connectResult;

    private int menuOpen = 0, optionsMenuOpen = 0;

    private String keybindPrimed = "";
    private boolean keyPrime = false;

    private static final List<Vector2f> resOptions = new ArrayList<>();

    @Override
    public void init() {

        resOptions.add(new Vector2f(800, 450));
        resOptions.add(new Vector2f(1280, 720));
        resOptions.add(new Vector2f(1336, 768));
        resOptions.add(new Vector2f(1600, 900));
        resOptions.add(new Vector2f(1920, 1080));
        resOptions.add(new Vector2f(2560, 1440));
        resOptions.add(new Vector2f(3480, 2160));

        addObject(new UiPanel(0, 0, 2, 2, 10, 0.2f));

        logo = addObject(new GameObject(screen(p(8), 0.04f, 3), UiBuilder.UIRect(p(1024), Material.UI_BORDERSITE_LOGO)));
        version = addObject(new UiText(screen(0 + p(8), 0.27f, 2), "created by harperkdavis    -    version " + Main.MAJOR_VERSION + "." + Main.MINOR_VERSION + "." + Main.PATCH_VERSION, 1));

        playButton = (UiButton) addObject(new UiButton(0.01f, 1, 0.21f, 1.09f, 3, 0.5f) {
            @Override
            public void onClick() {
                if (menuOpen == 1) {
                    menuOpen = 0;
                } else {
                    menuOpen = 1;
                }
            }
        });
        GameObject playLabel = addObject(new UiText(screen(0.04f, 1.03f, 2), "Play"));
        playLabel.setParent(playButton);
        buttons.add(playButton);

        optionsButton = (UiButton) addObject(new UiButton(0.01f, 1.1f, 0.21f, 1.19f, 3, 0.5f) {
            @Override
            public void onClick() {
                if (menuOpen == 2) {
                    menuOpen = 0;
                } else {
                    menuOpen = 2;
                }
            }
        });
        GameObject optionsButtonLabel = addObject(new UiText(screen(0.04f, 1.13f, 2), "Options"));
        optionsButtonLabel.setParent(optionsButton);
        buttons.add(optionsButton);

        quitButton = (UiButton) addObject(new UiButton(0.01f, 1.8f, 0.21f, 1.89f, 3, 0.5f) {
            @Override
            public void onClick() {
                menuOpen = 0;
                if (ClientHandler.isConnected()) {
                    ClientHandler.disconnect();
                }
                Main.requestClose = true;
            }
        });
        GameObject quitButtonLabel = addObject(new UiText(screen(0.04f, 1.83f, 2), "Quit"));
        quitButtonLabel.setParent(quitButton);
        buttons.add(quitButton);

        playButton.setVisible(false);
        optionsButton.setVisible(false);

        loginPanel = (UiPanel) addObject(new UiPanel(0.04f, 0.5f, 0.74f, 1.5f, 6, 0.5f));
        GameObject loginLabel = addObject(new UiText(screen(0.04f + p(8), 0.53f, 5), "Login"));
        loginLabel.setParent(loginPanel);

        usernameField = (UiTextField) addObject(new UiTextField(screen(0.1f + p(8), 0.73f, 4), "", 1));
        usernameField.setParent(loginPanel);
        UiButton usernameFieldButton = (UiButton) addObject(new UiButton(0.1f, 0.7f, 0.68f, 0.8f, 5, 0.5f) {
            @Override
            public void onClick() {
                UiTextField.select(usernameField);
            }
        });
        usernameFieldButton.setParent(loginPanel);
        buttons.add(usernameFieldButton);

        GameObject usernameLabel = addObject(new UiText(screen(0.1f, 0.65f, 5), "Username"));
        usernameLabel.setParent(loginPanel);

        passwordField = (UiTextField) addObject(new UiTextField(screen(0.1f + p(8), 0.93f, 4), "", 1, true));
        passwordField.setParent(loginPanel);
        UiButton passwordFieldButton = (UiButton) addObject(new UiButton(0.1f, 0.9f, 0.68f, 1.0f, 5, 0.5f) {
            @Override
            public void onClick() {
                UiTextField.select(passwordField);
            }
        });
        passwordFieldButton.setParent(loginPanel);
        buttons.add(passwordFieldButton);

        GameObject passwordLabel = addObject(new UiText(screen(0.1f, 0.85f, 5), "Password"));
        passwordLabel.setParent(loginPanel);

        loginResult = (UiText) addObject(new UiText(screen(0.1f, 1.1f, 4), "Please enter your username and password."));
        loginResult.setParent(loginPanel);

        UiButton tryLoginButton = (UiButton) addObject(new UiButton(0.1f, 1.2f, 0.68f, 1.3f, 5, 0.5f) {
            @Override
            public void onClick() {
                loginResult.setText("Attempting login...");
                UiTextField.deselect();
                try {
                    ClientHandler.login(usernameField.getText(), passwordField.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        tryLoginButton.setParent(loginPanel);
        buttons.add(tryLoginButton);

        GameObject loginButtonLabel = addObject(new UiText(screen(0.1f + p(8), 1.23f, 4), "Login"));
        loginButtonLabel.setParent(tryLoginButton);

        menu = addObject(new UiPanel(0.3f, 0.35f, 1.95f, 1.95f, 8, 0.5f));
        sideBar = addObject(new UiPanel(0.3f, 0.35f, 0.7f, 1.95f, 7, 0.5f));

        serversLabel = addObject(new UiText(screen(0.3f + p(8), 0.35f + p(12), 6), "Server Browser"));
        optionsLabel = addObject(new UiText(screen(0.3f + p(8), 0.35f + p(12), 6), "Options"));

        ///

        GameObject bottomPanel = addObject(new UiPanel(0.7f, 1.65f, 1.95f, 1.95f, 6, 0.2f));
        bottomPanel.setParent(serversLabel);

        GameObject closeServersButton = addObject(new UiButton(0.3f, 1.8f, 0.7f, 1.89f, 5, 0.5f) {
            @Override
            public void onClick() {
                menuOpen = 0;
            }
        });
        closeServersButton.setParent(serversLabel);
        buttons.add((UiButton) closeServersButton);
        addObject(new UiText(screen(0.3f + p(8), 1.83f, 4), "Close")).setParent(serversLabel);

        ipField = (UiTextField) addObject(new UiTextField(screen(0.75f + p(8), 1.83f, 4), "", 1));
        ipField.setParent(serversLabel);
        UiButton ipFieldButton = (UiButton) addObject(new UiButton(0.75f, 1.8f, 1.4f, 1.9f, 5, 0.5f) {
            @Override
            public void onClick() {
                UiTextField.select(ipField);
            }
        });
        ipFieldButton.setParent(serversLabel);
        buttons.add(ipFieldButton);

        GameObject ipLabel = addObject(new UiText(screen(0.75f, 1.75f, 5), "Ip"));
        ipLabel.setParent(serversLabel);

        connectResult = (UiText) addObject(new UiText(screen(0.75f, 1.68f, 4), "Select a server or direct connect to an IP."));
        connectResult.setParent(serversLabel);

        GameObject directConnectButton = addObject(new UiButton(1.45f, 1.8f, 1.9f, 1.9f, 5, 0.5f) {
            @Override
            public void onClick() {
                ClientHandler.tryConnect(ipField.getText());
                UiTextField.deselect();
                connectResult.setText("Connecting to " + ipField.getText() + "...");
            }
        });
        directConnectButton.setParent(serversLabel);
        buttons.add((UiButton) directConnectButton);
        addObject(new UiText(screen(1.45f + p(8), 1.83f, 4), "Direct Connect")).setParent(serversLabel);

        ///

        GameObject displayOptionsButton = addObject(new UiButton(0.3f, 0.5f, 0.7f, 0.59f, 5, 0.5f) {
            @Override
            public void onClick() {
                optionsMenuOpen = 0;
            }
        });
        displayOptionsButton.setParent(optionsLabel);
        buttons.add((UiButton) displayOptionsButton);
        addObject(new UiText(screen(0.3f + p(8), 0.53f, 4), "Display")).setParent(optionsLabel);

        GameObject controlOptionsButton = addObject(new UiButton(0.3f, 0.6f, 0.7f, 0.69f, 5, 0.5f) {
            @Override
            public void onClick() {
                optionsMenuOpen = 1;
            }
        });
        controlOptionsButton.setParent(optionsLabel);
        buttons.add((UiButton) controlOptionsButton);
        addObject(new UiText(screen(0.3f + p(8), 0.63f, 4), "Input")).setParent(optionsLabel);

        GameObject keybindsOptionsButton = addObject(new UiButton(0.3f, 0.7f, 0.7f, 0.79f, 5, 0.5f) {
            @Override
            public void onClick() {
                optionsMenuOpen = 2;
            }
        });
        keybindsOptionsButton.setParent(optionsLabel);
        buttons.add((UiButton) keybindsOptionsButton);
        addObject(new UiText(screen(0.3f + p(8), 0.73f, 4), "Keybinds")).setParent(optionsLabel);



        GameObject defaultsButton = addObject(new UiButton(0.3f, 1.5f, 0.7f, 1.59f, 5, 0.5f) {
            @Override
            public void onClick() {
                ConfigLoader.resetConfig();
                refreshKeybinds();
            }
        });
        defaultsButton.setParent(optionsLabel);
        buttons.add((UiButton) defaultsButton);
        addObject(new UiText(screen(0.3f + p(8), 1.53f, 4), "Reset to Defaults")).setParent(optionsLabel);

        GameObject loadConfigButton = addObject(new UiButton(0.3f, 1.6f, 0.7f, 1.69f, 5, 0.5f) {
            @Override
            public void onClick() {
                ConfigLoader.loadConfig();
                refreshKeybinds();
            }
        });
        loadConfigButton.setParent(optionsLabel);
        buttons.add((UiButton) loadConfigButton);
        addObject(new UiText(screen(0.3f + p(8), 1.63f, 4), "Load Config")).setParent(optionsLabel);

        GameObject saveConfigButton = addObject(new UiButton(0.3f, 1.7f, 0.7f, 1.79f, 5, 0.5f) {
            @Override
            public void onClick() {
                ConfigLoader.saveConfig();
                refreshKeybinds();
            }
        });
        saveConfigButton.setParent(optionsLabel);
        buttons.add((UiButton) saveConfigButton);
        addObject(new UiText(screen(0.3f + p(8), 1.73f, 4), "Save Config")).setParent(optionsLabel);

        GameObject closeOptionsButton = addObject(new UiButton(0.3f, 1.8f, 0.7f, 1.89f, 5, 0.5f) {
            @Override
            public void onClick() {
                menuOpen = 0;
            }
        });
        closeOptionsButton.setParent(optionsLabel);
        buttons.add((UiButton) closeOptionsButton);
        addObject(new UiText(screen(0.3f + p(8), 1.83f, 4), "Close")).setParent(optionsLabel);



        displayOptionsLabel = addObject(new UiText(screen(0.7f + p(8), 0.35f + p(12), 5), "Display (Requires Restart)"));
        controlOptionsLabel = addObject(new UiText(screen(0.7f + p(8), 0.35f + p(12), 5), "Input"));
        keybindsOptionsLabel = addObject(new UiText(screen(0.7f + p(8), 0.35f + p(12), 5), "Keybinds"));

        displayOptionsLabel.setParent(optionsLabel);
        controlOptionsLabel.setParent(optionsLabel);
        keybindsOptionsLabel.setParent(optionsLabel);

        int index = 0;
        for (String key : Global.keybinds.keySet()) {

            GameObject label = addObject(new UiText(screen(0.7f + (index / 12) * 0.4f + p(8), 0.53f + (index % 12) * 0.1f, 5), key));
            UiText value = (UiText) addObject(new UiText(screen(1.0f + (index / 12) * 0.4f + p(8), 0.53f + (index % 12) * 0.1f, 4), Global.keybinds.get(key)));
            GameObject button = addObject(new UiButton(1.0f + (index / 12) * 0.4f, 0.5f + (index % 12) * 0.1f, 1.2f + (index / 12) * 0.4f, 0.59f + (index % 12) * 0.1f, 5, 0.5f) {
                @Override
                public void onClick() {
                    keyPrime = true;
                    keybindPrimed = key;
                    value.setText("[press]");
                }
            });
            button.setParent(keybindsOptionsLabel);
            buttons.add((UiButton) button);
            addObject(new UiText(screen(0.3f + p(8), 1.83f, 4), "Close")).setParent(keybindsOptionsLabel);

            label.setParent(keybindsOptionsLabel);
            value.setParent(keybindsOptionsLabel);

            keybindValues.put(key, value);

            index++;
        }

        ///

        GameObject resolutionLabel = addObject(new UiText(screen(0.7f + p(8), 0.53f, 5), "Resolution"));
        UiText resolutionValue = (UiText) addObject(new UiText(screen(1.0f + p(8), 0.53f, 4), getResolution()));
        GameObject resolutionButton = addObject(new UiButton(1.0f, 0.5f, 1.2f, 0.59f, 5, 0.5f) {
            @Override
            public void onClick() {
                cycleResolution();
                resolutionValue.setText(getResolution());
            }
        });

        buttons.add((UiButton) resolutionButton);

        resolutionLabel.setParent(displayOptionsLabel);
        resolutionValue.setParent(displayOptionsLabel);
        resolutionButton.setParent(displayOptionsLabel);

        GameObject fullscreenLabel = addObject(new UiText(screen(0.7f + p(8), 0.63f, 5), "Fullscreen"));
        UiText fullscreenValue = (UiText) addObject(new UiText(screen(1.0f + p(8), 0.63f, 4), Global.FULLSCREEN + ""));
        GameObject fullscreenButton = addObject(new UiButton(1.0f, 0.6f, 1.2f, 0.69f, 5, 0.5f) {
            @Override
            public void onClick() {
                Global.FULLSCREEN = !Global.FULLSCREEN;
                fullscreenValue.setText(Global.FULLSCREEN + "");
            }
        });

        buttons.add((UiButton) fullscreenButton);

        fullscreenLabel.setParent(displayOptionsLabel);
        fullscreenValue.setParent(displayOptionsLabel);
        fullscreenButton.setParent(displayOptionsLabel);


        ///

        GameObject mouseSensXLabel = addObject(new UiText(screen(0.7f + p(8), 0.53f, 5), "Sensitivity X"));
        UiText mouseSensXValue = (UiText) addObject(new UiText(screen(1.0f + p(8), 0.53f, 4), Global.MOUSE_SENSITIVITY_X + ""));
        GameObject mouseSensXButton = addObject(new UiButton(1.0f, 0.5f, 1.5f, 0.59f, 5, 0.5f) {
            @Override
            public void onClick() {
                Global.MOUSE_SENSITIVITY_X = ((getNormMouseX() - 1.0f) * 4) * ((getNormMouseX() - 1.0f) * 4);
                mouseSensXValue.setText(Global.MOUSE_SENSITIVITY_X + "");
            }
        });

        buttons.add((UiButton) mouseSensXButton);

        mouseSensXLabel.setParent(controlOptionsLabel);
        mouseSensXValue.setParent(controlOptionsLabel);
        mouseSensXButton.setParent(controlOptionsLabel);

        GameObject mouseSensYLabel = addObject(new UiText(screen(0.7f + p(8), 0.63f, 5), "Sensitivity Y"));
        UiText mouseSensYValue = (UiText) addObject(new UiText(screen(1.0f + p(8), 0.63f, 4), Global.MOUSE_SENSITIVITY_Y + ""));
        GameObject mouseSensYButton = addObject(new UiButton(1.0f, 0.6f, 1.5f, 0.69f, 5, 0.5f) {
            @Override
            public void onClick() {
                Global.MOUSE_SENSITIVITY_Y = ((getNormMouseX() - 1.0f) * 4) * ((getNormMouseX() - 1.0f) * 4);
                mouseSensYValue.setText(Global.MOUSE_SENSITIVITY_Y + "");
            }
        });

        buttons.add((UiButton) mouseSensYButton);

        mouseSensYLabel.setParent(controlOptionsLabel);
        mouseSensYValue.setParent(controlOptionsLabel);
        mouseSensYButton.setParent(controlOptionsLabel);

        GameObject invertMouseXLabel = addObject(new UiText(screen(0.7f + p(8), 0.73f, 5), "Invert X"));
        UiText invertMouseXValue = (UiText) addObject(new UiText(screen(1.0f + p(8), 0.73f, 4), Global.MOUSE_INVERT_X + ""));
        GameObject invertMouseXButton = addObject(new UiButton(1.0f, 0.7f, 1.2f, 0.79f, 5, 0.5f) {
            @Override
            public void onClick() {
                Global.MOUSE_INVERT_X = !Global.MOUSE_INVERT_X;
                invertMouseXValue.setText(Global.MOUSE_INVERT_X + "");
            }
        });

        buttons.add((UiButton) invertMouseXButton);

        invertMouseXLabel.setParent(controlOptionsLabel);
        invertMouseXValue.setParent(controlOptionsLabel);
        invertMouseXButton.setParent(controlOptionsLabel);

        GameObject invertMouseYLabel = addObject(new UiText(screen(0.7f + p(8), 0.83f, 5), "Invert Y"));
        UiText invertMouseYValue = (UiText) addObject(new UiText(screen(1.0f + p(8), 0.83f, 4), Global.MOUSE_INVERT_Y + ""));
        GameObject invertMouseYButton = addObject(new UiButton(1.0f, 0.8f, 1.2f, 0.89f, 5, 0.5f) {
            @Override
            public void onClick() {
                Global.MOUSE_INVERT_Y = !Global.MOUSE_INVERT_Y;
                invertMouseYValue.setText(Global.MOUSE_INVERT_Y + "");
            }
        });

        buttons.add((UiButton) invertMouseYButton);

        invertMouseYLabel.setParent(controlOptionsLabel);
        invertMouseYValue.setParent(controlOptionsLabel);
        invertMouseYButton.setParent(controlOptionsLabel);


        refresh();
    }

    public String getResolution() {
        return Global.WINDOW_WIDTH + " x " + Global.WINDOW_HEIGHT;
    }

    public void cycleResolution() {
        Vector2f current = new Vector2f(Global.WINDOW_WIDTH, Global.WINDOW_HEIGHT);
        boolean cycled = false;
        for (int i = 0; i < resOptions.size(); i++) {
            if (resOptions.get(i).getX() == current.getX()) {
                current = new Vector2f(resOptions.get((i + 1) % resOptions.size()));
                cycled = true;
                break;
            }
        }
        if (!cycled) {
            current = resOptions.get(0);
        }
        Global.WINDOW_WIDTH = (int) current.getX();
        Global.WINDOW_HEIGHT = (int) current.getY();
    }

    public void refresh() {
        serversLabel.setVisible(false);
        optionsLabel.setVisible(false);

        menu.setVisible(false);
        sideBar.setVisible(false);

        playButton.setVisible(ClientHandler.SESSION_ID != null);
        optionsButton.setVisible(ClientHandler.SESSION_ID != null);

        loginPanel.setVisible(ClientHandler.SESSION_ID == null);

        if (menuOpen > 0) {

            menu.setVisible(true);
            sideBar.setVisible(true);

            if (menuOpen == 1) {
                serversLabel.setVisible(true);
            } else {
                optionsLabel.setVisible(true);

                displayOptionsLabel.setVisible(false);
                controlOptionsLabel.setVisible(false);
                keybindsOptionsLabel.setVisible(false);
                if (optionsMenuOpen == 0) {
                    displayOptionsLabel.setVisible(true);
                } else if (optionsMenuOpen == 1) {
                    controlOptionsLabel.setVisible(true);
                } else if (optionsMenuOpen == 2) {
                    keybindsOptionsLabel.setVisible(true);
                }
            }
        }
    }

    public void refreshKeybinds() {
        for (String key : Global.keybinds.keySet()) {
            keybindValues.get(key).setText(Global.keybinds.get(key));
        }
    }

    @Override
    public void update() {
        refresh();
        if (keyPrime) {
            for (String bind : Global.bindNames.keySet()) {
                if (!bind.equals("esc") && Input.isBindDown(bind)) {
                    Global.keybinds.put(keybindPrimed, bind);
                    keyPrime = false;
                    keybindValues.get(keybindPrimed).setText(bind);
                    break;
                }
            }
        }
        for (UiButton button : buttons) {
            button.update();
        }
    }

    @Override
    public void unload() {

    }

}
