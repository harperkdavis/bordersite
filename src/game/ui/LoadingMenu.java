package game.ui;

import engine.graphics.Material;
import engine.graphics.MeshBuilder;
import engine.graphics.TextMeshBuilder;
import engine.graphics.TextMode;
import engine.math.Vector3f;
import engine.objects.GameObject;
import engine.objects.GameObjectGroup;
import engine.objects.GameObjectMesh;
import game.world.World;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class LoadingMenu extends Menu {

    private GameObject ld_blackBackground;
    private GameObject ld_loadingTitle;
    private GameObject ld_loadingText;

    @Override
    public void init() {
        ld_blackBackground = addObjectWithoutLoading(new GameObjectMesh(screen(1.0f, -1.0f, 3), Vector3f.zero(), Vector3f.one(), MeshBuilder.UICenter(4.0f, new Material("/textures/black.png"))));
        ld_loadingTitle = addObjectWithoutLoading(new GameObjectMesh(screen(0.66f, 0.0f, 2), Vector3f.zero(), Vector3f.one(), MeshBuilder.UIRect(p(1024.0f), new Material("/textures/loading-logo.png"))));
        ld_loadingText = addObjectWithoutLoading(new GameObjectMesh(screen(1.0f, -1.0f, 2), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("LOADING MAP", p(16.0f), TextMode.LEFT)));
    }

    @Override
    public void load() {
        for (GameObject object : objects) {
            if (object instanceof GameObjectGroup) {
                ((GameObjectGroup) object).load();
            } else if (object instanceof GameObjectMesh) {
                ((GameObjectMesh) object).load();
            }
        }
    }

    @Override
    public void update() {
        ((GameObjectMesh) ld_loadingText).setMesh(TextMeshBuilder.TextMesh(World.getLoader().getLoadingName() + " (" + (Math.round(World.getLoader().getLoadingProgress() * 1000) / 10) + "%)", p(32.0f), TextMode.CENTER, false));
    }

    @Override
    public void unload() {
        for (GameObject object : objects) {
            if (object instanceof GameObjectGroup) {
                ((GameObjectGroup) object).unload();
            } else if (object instanceof GameObjectMesh) {
                ((GameObjectMesh) object).unload();
            }
        }
    }
}
