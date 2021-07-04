package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.math.Vector3f;
import engine.objects.GameObject;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class InGameMenu extends Menu {

    private GameObject crosshair;

    @Override
    public void init() {
        crosshair = addObjectWithoutLoading(new GameObject(screen(1, 1, 1), UiBuilder.UICenter(p(32), Material.UI_CROSSHAIR)));
    }

    @Override
    public void load() {
        crosshair.load();
    }

    @Override
    public void update() {

    }

    @Override
    public void unload() {

    }

}
