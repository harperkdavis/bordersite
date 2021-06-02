package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.graphics.text.TextMeshBuilder;
import engine.graphics.text.TextMode;
import engine.math.Vector3;
import engine.math.Vector4;
import engine.objects.GameObject;
import game.scene.Scene;
import main.Main;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class MapLoadingMenu extends Menu {

    private GameObject ld_loadingTitle;
    private GameObject ld_loadingText;

    @Override
    public void init() {
        GameObject ld_blackBackground = addObjectWithoutLoading(new GameObject(screen(1.0f, 1.0f, 3), Vector3.zero(), Vector3.one(), UiBuilder.UICenter(4.0f, Material.DEFAULT), new Vector4(0, 0, 0, 1)));
        ld_loadingTitle = addObjectWithoutLoading(new GameObject(screen(1, 0.5f, 2), Vector3.zero(), Vector3.one(), UiBuilder.UICenter(p(512.0f), Material.UI_LOADING_LOGO)));
        ld_loadingText = addObjectWithoutLoading(new GameObject(screen(1.0f, 1.0f, 2), Vector3.zero(), Vector3.one(), TextMeshBuilder.TextMesh("LOADING MAP", p(16.0f), TextMode.LEFT)));
    }

    @Override
    public void load() {
        for (GameObject object : objects) {
            object.load();
        }
    }

    @Override
    public void update() {
        ld_loadingText.setMesh(TextMeshBuilder.TextMesh(Scene.getLoader().getLoadingName() + " (" + (Math.round(Scene.getLoader().getLoadingProgress() * 1000) / 10) + "%)", p(16.0f), TextMode.CENTER));
        ld_loadingTitle.setRotation(new Vector3(0, 0, (float) Math.sin((System.currentTimeMillis() - Main.getStartTime()) / 4000.0f) * 5.0f));
    }

    @Override
    public void unload() {
        for (GameObject object : objects) {
            object.unload();
        }
    }
}
