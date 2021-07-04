package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.math.Vector3f;
import engine.math.Vector4f;
import engine.objects.GameObject;
import game.scene.Scene;
import main.Main;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class MapLoadingMenu extends Menu {

    private GameObject ld_loadingTitle;
    private UiText ld_loadingText;

    @Override
    public void init() {
        GameObject ld_blackBackground = addObjectWithoutLoading(new GameObject(screen(1.0f, 1.0f, 3), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(4.0f, Material.DEFAULT), new Vector4f(0, 0, 0, 1)));
        ld_loadingTitle = addObjectWithoutLoading(new GameObject(screen(1, 0.5f, 2), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(512.0f), Material.UI_LOADING_LOGO)));
        ld_loadingText = (UiText) addObjectWithoutLoading(new UiText(screen(1.0f, 1.0f, 2), "LOADING MAP"));
    }

    @Override
    public void load() {
        for (GameObject object : objects) {
            object.load();
        }
    }

    @Override
    public void update() {
        ld_loadingText.setText(Scene.getLoader().getLoadingName() + " (" + (Math.round(Scene.getLoader().getLoadingProgress() * 1000) / 10) + "%)");
        ld_loadingTitle.setRotation(new Vector3f(0, 0, (float) Math.sin((System.currentTimeMillis() - Main.getStartTime()) / 4000.0f) * 5.0f));
    }

    @Override
    public void unload() {
        for (GameObject object : objects) {
            object.unload();
        }
    }
}
