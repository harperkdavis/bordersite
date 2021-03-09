package game.ui;

import engine.graphics.Renderer;
import engine.objects.GameObject;
import engine.objects.GameObjectGroup;
import engine.objects.GameObjectMesh;

import java.util.ArrayList;
import java.util.List;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public abstract class Menu {

    protected List<GameObject> objects;

    public Menu() {
        objects = new ArrayList<>();
        init();
    }

    public abstract void init();
    public abstract void load();
    public abstract void update();
    public void render(Renderer renderer) {
        for (GameObject o : objects) {
            renderer.renderMesh(o, null);
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
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).load();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).load();
        }
        return object;
    }

    public GameObject addObjectWithoutLoading(GameObject object) {
        objects.add(object);
        return object;
    }

    public void removeObject(GameObject object) {
        objects.remove(object);
        if (object instanceof GameObjectGroup) {
            ((GameObjectGroup) object).unload();
        } else if (object instanceof GameObjectMesh) {
            ((GameObjectMesh) object).unload();
        }
    }

}
