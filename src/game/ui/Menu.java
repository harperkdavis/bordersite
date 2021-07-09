package game.ui;

import engine.graphics.render.Renderer;
import engine.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {

    protected List<GameObject> objects;

    public Menu() {
        objects = new ArrayList<>();
        init();
    }

    public abstract void init();
    public abstract void update();

    public void render() {
        for (GameObject o : objects) {
            Renderer.getUi().render(o);
        }
    }
    public abstract void unload();

    public void setVisible(boolean visible) {
        for (GameObject go : objects) {
            go.setVisible(visible);
        }
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
