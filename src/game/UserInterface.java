package game;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.*;
import engine.io.Input;
import engine.io.Window;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.objects.GameObjectGroup;
import engine.objects.GameObjectMesh;
import net.Client;
import org.lwjgl.glfw.GLFW;

public class UserInterface {

    private static UserInterface ui;

    private float PIXEL = 0.0025f;
    private int width;
    private int height;

    private static List<GameObject> uiObjects = new ArrayList<>();

    private boolean inMainMenu = true;

    private GameObject mm_gameLogo;
    private GameObject mm_gameVersion;
    private GameObject mm_mapBackground;

    private GameObject mm_playButton;
    private GameObject mm_optionsButton;
    private GameObject mm_creditsButton;

    private GameObject mm_slider;
    private GameObject mm_sliderButton;

    private enum MainMenuScreen {
        MAIN_MENU,
        OPTIONS,
        PLAY,
        CREDITS
    }

    private MainMenuScreen mms = MainMenuScreen.MAIN_MENU;

    public UserInterface(int width, int height) {
        this.width = width;
        this.height = height;

        PIXEL = 1.0f / (height / 2.25f);

        mm_mapBackground = addObjectWithoutLoading(new GameObjectMesh(screen(1.0f, 0.0f, 3), Vector3f.zero(), Vector3f.one(), MeshBuilder.UICenter(p(4096.0f), new Material("/textures/bordersite-map.png"))), "menu");

        addObjectWithoutLoading(new GameObjectMesh(screen(0.0f, 0.0f, 2), Vector3f.zero(), Vector3f.one(), MeshBuilder.UIRect(4.0f, new Material("/textures/black-transparent.png"))), "menu");

        mm_gameLogo = addObjectWithoutLoading(new GameObjectMesh(screen(p(268), 1.0f, 1), Vector3f.zero(), Vector3f.one(), MeshBuilder.UICenter(p(1024.0f), new Material("/textures/bordersite-logo.png"))), "menu");
        mm_gameVersion = addObjectWithoutLoading(new GameObjectMesh(screen(p(3), 1.0f, 1), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("ALPHA V0.1", p(32.0f), TextMode.LEFT)), "menu");

        mm_optionsButton = addObjectWithoutLoading(new GameObjectMesh(screen(0.0f + p(3), -1.0f + p(50), 1), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("OPTIONS", p(40.0f), TextMode.LEFT)), "menu");
        mm_playButton = addObjectWithoutLoading(new GameObjectMesh(screen(0.0f + p(3), -1.0f, 1), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("PLAY", p(40.0f), TextMode.LEFT)));
        mm_creditsButton = addObjectWithoutLoading(new GameObjectMesh(screen(0.0f + p(3), -1.0f - p(50), 1), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("CREDITS", p(40.0f), TextMode.LEFT)), "menu");

        mm_sliderButton = addObjectWithoutLoading(new GameObjectMesh(screen(1.0f, 2.0f, 1), Vector3f.zero(), Vector3f.one(), MeshBuilder.UICenter(p(1024.0f), new Material("/textures/slider.png"))), "menu");
        mm_slider = addObjectWithoutLoading(new GameObjectMesh(screen(1.0f, 2.0f, 1), Vector3f.zero(), Vector3f.one(), MeshBuilder.UICenter(p(64.0f), new Material("/textures/slider-button.png"))), "menu");
    }

    public void load() {
        for (GameObject o : uiObjects) {
            if (o instanceof GameObjectGroup) {
                ((GameObjectGroup) o).load();
            } else if (o instanceof GameObjectMesh) {
                ((GameObjectMesh) o).load();
            }
        }
    }

    public void update() {

        float mouseXNormalized = ((float) Input.getMouseX() - Window.getWidth() / 2.0f) / (Window.getWidth() / 2.0f);
        float mouseYNormalized = (Window.getHeight() / 2.0f - (float) Input.getMouseY()) / (Window.getHeight() / 2.0f);

        if (inMainMenu) {

            // Menu Background and Logo
            float screenX = 0.0f;
            float screenY = 0.0f;

            switch (mms) {
                case PLAY:
                    screenX = 3.0f;
                    screenY = 0.0f;
                    break;
                case CREDITS:
                    screenX = 0.0f;
                    screenY = -3.0f;
                    break;
                case OPTIONS:
                    screenX = 0.0f;
                    screenY = 3.0f;
                    break;
                default:
                    screenX = 0.0f;
                    screenY = 0.0f;
                    break;
            }

            if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
                mms = MainMenuScreen.MAIN_MENU;
            }

            Vector3f displacement = new Vector3f(-mouseXNormalized * p(100) - screenX, -mouseYNormalized * p(100) - screenY, 0.0f);

            mm_mapBackground.setPosition(Vector3f.lerp(mm_mapBackground.getPosition(), screen(1.0f, 0.0f, 3).add(Vector3f.divide(displacement, new Vector3f(2, 2, 2))), 0.1f * Window.getDeltaTime()));

            mm_gameLogo.setPosition(Vector3f.lerp(mm_gameLogo.getPosition(), screen(p(268), p(-80), 1).add(displacement), 0.2f * Window.getDeltaTime()));
            mm_gameVersion.setPosition(Vector3f.lerp(mm_gameVersion.getPosition(), screen(p(3), p(-160), 1).add(displacement), 0.2f * Window.getDeltaTime()));

            // Three Buttons

            boolean optionsSelected = mouseYNormalized < 0.3f + displacement.getY() && mouseYNormalized > 0.1f + displacement.getY();
            boolean playSelected = mouseYNormalized < 0.1f + displacement.getY() && mouseYNormalized > -0.1f + displacement.getY();
            boolean creditsSelected = mouseYNormalized < -0.1f + displacement.getY() && mouseYNormalized > -0.3f + displacement.getY();

            float optionsScale = 1.0f;
            float playScale = 1.0f;
            float creditsScale = 1.0f;
            float optionsY = -1.0f + p(50);
            float playY = -1.0f;
            float creditsY = -1.0f - p(50);

            if (mms == MainMenuScreen.MAIN_MENU) {
                if (mouseXNormalized < -0.5f) {
                    optionsScale = optionsSelected ? 2.0f : 1.0f;
                    playScale = playSelected ? 2.0f : 1.0f;
                    creditsScale = creditsSelected ? 2.0f : 1.0f;
                    if (optionsSelected) {
                        playY = -1.0f - p(10);
                        creditsY = -1.0f - p(70);
                    } else if (playSelected) {
                        optionsY = -1.0f + p(60);
                        creditsY = -1.0f - p(60);
                    } else if (creditsSelected) {
                        optionsY = -1.0f + p(70);
                        playY = -1.0f + p(10);
                    }
                }
                mm_optionsButton.setScale(Vector3f.lerp(mm_optionsButton.getScale(), new Vector3f(optionsScale, optionsScale, 1.0f), 0.2f * Window.getDeltaTime()));
                mm_playButton.setScale(Vector3f.lerp(mm_playButton.getScale(), new Vector3f(playScale, playScale, 1.0f), 0.2f * Window.getDeltaTime()));
                mm_creditsButton.setScale(Vector3f.lerp(mm_creditsButton.getScale(), new Vector3f(creditsScale, creditsScale, 1.0f), 0.2f * Window.getDeltaTime()));

                if (Input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
                    if (optionsSelected) {
                        mms = MainMenuScreen.OPTIONS;
                    } else if (playSelected) {
                        mms = MainMenuScreen.PLAY;
                    } else if (creditsSelected) {
                        mms = MainMenuScreen.CREDITS;
                    }
                }
            }
            float optionsDisplace = -p(12.5f * mm_optionsButton.getScale().getX());
            float playDisplace = -p(12.5f * mm_playButton.getScale().getX());
            float creditsDisplace = -p(12.5f * mm_creditsButton.getScale().getX());

            mm_optionsButton.setPosition(Vector3f.lerp(mm_optionsButton.getPosition(), screen(0.0f, optionsY + optionsDisplace, 1).add(displacement), 0.2f * Window.getDeltaTime()));
            mm_playButton.setPosition(Vector3f.lerp(mm_playButton.getPosition(), screen(0.0f, playY + playDisplace, 1).add(displacement), 0.2f * Window.getDeltaTime()));
            mm_creditsButton.setPosition(Vector3f.lerp(mm_creditsButton.getPosition(), screen(0.0f, creditsY + creditsDisplace, 1).add(displacement), 0.2f * Window.getDeltaTime()));

            // OPTIONS MENU
            mm_slider.setPosition(Vector3f.lerp(mm_slider.getPosition(), screen(1.0f, 2.0f, 1).add(displacement), 0.2f * Window.getDeltaTime()));
            mm_sliderButton.setPosition(Vector3f.lerp(mm_sliderButton.getPosition(), screen(1.0f, 2.0f, 1).add(displacement), 0.2f * Window.getDeltaTime()));
        }

        if (Client.isConnected()) {
            float recoil = PlayerMovement.getPlayerMovement().getRecoil();
            float recoilOffset = recoil > 1 ? recoil * recoil : recoil;
        }
    }

    public void render(Renderer renderer) {
        for (GameObject o : uiObjects) {
            renderer.renderMesh(o, null);
        }
    }

    public void unload() {
        for (GameObject o : uiObjects) {
            if (o instanceof GameObjectGroup) {
                ((GameObjectGroup) o).unload();
            } else if (o instanceof GameObjectMesh) {
                ((GameObjectMesh) o).unload();
            }
        }
    }

    public static GameObject addObject(GameObject object) {
        uiObjects.add(object);
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).load();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).load();
        }
        return object;
    }

    public static GameObject addObjectWithoutLoading(GameObject object) {
        uiObjects.add(object);
        return object;
    }

    public static GameObject addObjectWithoutLoading(GameObject object, String tag) {
        object.addTag(tag);
        uiObjects.add(object);
        return object;
    }

    public static void removeObject(GameObject object) {
        uiObjects.remove(object);
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).unload();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).unload();
        }
    }

    public static UserInterface getUi() {
        return ui;
    }

    public static void setUi(UserInterface ui) {
        UserInterface.ui = ui;
    }

    private Vector3f screen(float posX, float posY, int layer) {
        return new Vector3f((posX * PIXEL * (width / 2.0f)) - (width / 2.0f) * PIXEL, (posY * PIXEL * (height / 2.0f)) + (height / 2.0f) * PIXEL, layer);

    }

    private float p(float n) {
        return n * PIXEL;
    }

    private float p(int n) {
        return n * PIXEL;
    }

}
