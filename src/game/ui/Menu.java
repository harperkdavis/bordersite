package game.ui;

import engine.graphics.render.UiRenderer;
import engine.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {

    protected List<GameObject> objects;
    private boolean menuIsVisible = false;

    public Menu() {
        objects = new ArrayList<>();
        initialize();
    }

    public void initialize() {
        init();
    }

    public abstract void init();
    public abstract void update();

    public void render() {
        if (!menuIsVisible) {
            return;
        }
        for (GameObject o : objects) {
            UiRenderer.render(o);
        }
    }
    public abstract void unload();

    public void setVisible(boolean visible) {
        menuIsVisible = visible;
    }

    public GameObject addObject(GameObject object) {
        objects.add(object);
        return object;
    }

    public void removeObject(GameObject object) {
        objects.remove(object);
        object.unload();
    }

}
