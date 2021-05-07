package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.graphics.text.TextMeshBuilder;
import engine.graphics.text.TextMode;
import engine.math.Vector3f;
import engine.objects.GameObject;
import game.world.World;
import main.Main;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class LoadingMenu extends Menu {

    private GameObject ld_loadingTitle;
    private GameObject ld_loadingText;

    @Override
    public void init() {
        GameObject ld_blackBackground = addObjectWithoutLoading(new UiObject(screen(1.0f, 1.0f, 3), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(4.0f, new Material("/textures/black.png"))));
        ld_loadingTitle = addObjectWithoutLoading(new UiObject(screen(1, 0.5f, 2), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(512.0f), new Material("/textures/loading-logo.png"))));
        ld_loadingText = addObjectWithoutLoading(new UiObject(screen(1.0f, 1.0f, 2), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("LOADING MAP", p(16.0f), TextMode.LEFT)));
    }

    @Override
    public void load() {
        for (GameObject object : objects) {
            object.load();
        }
    }

    @Override
    public void update() {
        ((UiObject) ld_loadingText).setMesh(TextMeshBuilder.TextMesh(World.getLoader().getLoadingName() + " (" + (Math.round(World.getLoader().getLoadingProgress() * 1000) / 10) + "%)", p(16.0f), TextMode.CENTER));
        ld_loadingTitle.setRotation(new Vector3f(0, 0, (float) Math.sin(Main.getElapsedTime() / 4000.0f) * 5.0f));
    }

    @Override
    public void unload() {
        for (GameObject object : objects) {
            object.unload();
        }
    }
}
