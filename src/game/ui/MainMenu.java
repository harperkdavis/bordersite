package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.graphics.text.TextMeshBuilder;
import engine.graphics.text.TextMode;
import engine.io.Input;
import engine.math.Vector3f;
import engine.objects.GameObject;
import org.lwjgl.glfw.GLFW;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class MainMenu extends Menu {

    private GameObject mm_gameLogo;
    private GameObject mm_gameVersion;

    private GameObject mm_mapBackground;
    private GameObject mm_blackTransparent;

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

    @Override
    public void init() {
        mm_mapBackground = addObjectWithoutLoading(
                new UiObject(screen(1.0f, 0.0f, 3),
                Vector3f.zero(), Vector3f.one(),
                UiBuilder.UICenter(p(4096.0f), new Material("/textures/map/map.png"))));

        mm_blackTransparent = addObjectWithoutLoading(
                new UiObject(screen(0.0f, 0.0f, 2),
                Vector3f.zero(), Vector3f.one(),
                        UiBuilder.UIRect(4.0f, new Material("/textures/black-transparent.png"))));

        mm_gameLogo = addObjectWithoutLoading(
                new UiObject(screen(p(268), -1.0f, 1),
                Vector3f.zero(), Vector3f.one(),
                        UiBuilder.UICenter(p(1024.0f), new Material("/textures/bordersite-logo.png"))));

        mm_gameVersion = addObjectWithoutLoading(
                new UiObject(screen(p(3), -1.0f, 1),
                Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("ALPHA V0.1",
                p(32.0f), TextMode.LEFT)));

        mm_optionsButton = addObjectWithoutLoading(
                new UiObject(screen(0.0f + p(3), 1.0f + p(50), 1),
                Vector3f.zero(), Vector3f.one(),
                TextMeshBuilder.TextMesh("OPTIONS", p(40.0f), TextMode.LEFT)));

        mm_playButton = addObjectWithoutLoading(
                new UiObject(screen(0.0f + p(3), 1.0f, 1),
                Vector3f.zero(), Vector3f.one(),
                TextMeshBuilder.TextMesh("PLAY", p(40.0f), TextMode.LEFT)));

        mm_creditsButton = addObjectWithoutLoading(
                new UiObject(screen(0.0f + p(3), 1.0f - p(50), 1),
                Vector3f.zero(), Vector3f.one(),
                TextMeshBuilder.TextMesh("CREDITS", p(40.0f), TextMode.LEFT)));

        mm_sliderButton = addObjectWithoutLoading(new UiObject(screen(1.0f, -2.0f, 1),
                Vector3f.zero(), Vector3f.one(),
                UiBuilder.UICenter(p(1024.0f), new Material("/textures/slider.png"))));
        mm_slider = addObjectWithoutLoading(new UiObject(screen(1.0f, -2.0f, 1),
                Vector3f.zero(), Vector3f.one(),
                UiBuilder.UICenter(p(64.0f), new Material("/textures/slider-button.png"))));
    }

    @Override
    public void load() {
        for (GameObject object : objects) {
            object.load();
        }
    }

    @Override
    public void update() {

        float mouseXNormalized = UserInterface.getNormMouseX();
        float mouseYNormalized = UserInterface.getNormMouseY();

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

        mm_mapBackground.setPosition(Vector3f.lerpdt(mm_mapBackground.getPosition(), screen(1.0f, 0.0f, 3).add(Vector3f.divide(displacement, new Vector3f(2, 2, 2))), 0.1f));

        mm_gameLogo.setPosition(Vector3f.lerpdt(mm_gameLogo.getPosition(), screen(p(268), p(80), 1).add(displacement), 0.2f));
        mm_gameVersion.setPosition(Vector3f.lerpdt(mm_gameVersion.getPosition(), screen(p(3), p(160), 1).add(displacement), 0.2f));

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
            mm_optionsButton.setScale(Vector3f.lerpdt(mm_optionsButton.getScale(), new Vector3f(optionsScale, optionsScale, 1.0f), 0.2f));
            mm_playButton.setScale(Vector3f.lerpdt(mm_playButton.getScale(), new Vector3f(playScale, playScale, 1.0f), 0.2f));
            mm_creditsButton.setScale(Vector3f.lerpdt(mm_creditsButton.getScale(), new Vector3f(creditsScale, creditsScale, 1.0f), 0.2f));

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

        mm_optionsButton.setPosition(Vector3f.lerpdt(mm_optionsButton.getPosition(), screen(0.0f, optionsY + optionsDisplace, 1).add(displacement), 0.2f));
        mm_playButton.setPosition(Vector3f.lerpdt(mm_playButton.getPosition(), screen(0.0f, playY + playDisplace, 1).add(displacement), 0.2f));
        mm_creditsButton.setPosition(Vector3f.lerpdt(mm_creditsButton.getPosition(), screen(0.0f, creditsY + creditsDisplace, 1).add(displacement), 0.2f));

        // OPTIONS MENU
        mm_slider.setPosition(Vector3f.lerpdt(mm_slider.getPosition(), screen(1.0f, 2.0f, 1).add(displacement), 0.2f));
        mm_sliderButton.setPosition(Vector3f.lerpdt(mm_sliderButton.getPosition(), screen(1.0f, 2.0f, 1).add(displacement), 0.2f));
    }

    @Override
    public void unload() {
        for (GameObject object : objects) {
            object.unload();
        }
    }
}
