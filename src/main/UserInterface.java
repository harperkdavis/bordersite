package main;

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

public class UserInterface {

    private static UserInterface ui;

    private float PIXEL = 0.0025f;

    private static List<GameObject> uiObjects = new ArrayList<>();

    private boolean inMainMenu = true;

    private GameObject mm_gameLogo;
    private GameObject mm_gameVersion;
    private GameObject mm_mapBackground;

    private GameObject mm_playButton;
    private GameObject mm_optionsButton;
    private GameObject mm_creditsButton;

    private int width;
    private int height;

    public UserInterface(int width, int height) {
        this.width = width;
        this.height = height;

        PIXEL = 1.0f / (height / 2.25f);

        mm_mapBackground = addObjectWithoutLoading(new GameObjectMesh(screen(0.0f, 0.0f, 0), Vector3f.zero(), Vector3f.one(), MeshBuilder.UIRect(4096.0f * PIXEL, new Material("/textures/bordersite-map.png"))));

        addObjectWithoutLoading(new GameObjectMesh(screen(0.0f, 0.0f, 1), Vector3f.zero(), Vector3f.one(), MeshBuilder.UIRect(4.0f, new Material("/textures/black-transparent.png"))));

        mm_gameLogo = addObjectWithoutLoading(new GameObjectMesh(screen(0.0f, 0.0f, 2), Vector3f.zero(), Vector3f.one(), MeshBuilder.UICenter(1024.0f * PIXEL, new Material("/textures/bordersite-logo.png"))));
        mm_gameVersion = addObjectWithoutLoading(new GameObjectMesh(screen(0.0f, 0.0f, 2), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("ALPHA V0.1", PIXEL * 24.0f, TextMode.LEFT)));


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
            mm_mapBackground.setPosition(Vector3f.lerp(mm_mapBackground.getPosition(), new Vector3f(mouseXNormalized * 40.0f * PIXEL, mouseYNormalized * 40.0f * PIXEL, 2.0f), 0.1f));
            mm_mapBackground.setRotation(Vector3f.lerp(mm_mapBackground.getRotation(), new Vector3f(mouseXNormalized * 10.0f, 0.0f + mouseYNormalized * 10.0f, 0.0f), 0.1f));

            mm_gameLogo.setPosition(Vector3f.lerp(mm_gameLogo.getPosition(), screen(0.5f, -0.5f, 2), 0.04f * Window.getDeltaTime()));
            mm_gameVersion.setPosition(Vector3f.lerp(mm_gameVersion.getPosition(), screen(0.0f, 0.0f, 2), 0.02f * Window.getDeltaTime()));
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
        return new Vector3f((posX * PIXEL * (width / 2.0f)) - (width / 2.0f) * PIXEL, (posY * PIXEL * (height / 2.0f)) + (height / 2.0f) * PIXEL, 100.0f - 0.5f * layer);

    }

}
