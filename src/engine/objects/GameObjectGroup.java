package engine.objects;

import java.util.List;
import engine.graphics.Mesh;
import engine.math.Vector3f;

public class GameObjectGroup extends GameObject {

    private List<GameObject> children;

    public GameObjectGroup(Vector3f position, Vector3f rotation, Vector3f scale, List<GameObject> children) {
        super(position, rotation, scale);
        if (children != null) {
            if (children.size() != 0) {
                this.children.addAll(children);
            }
        }
    }

    public List<GameObject> getChildren() {
        return children;
    }

    public void load() {
        for (GameObject object : children) {
            if (object instanceof GameObjectMesh) {
                ((GameObjectMesh) object).load();
            } else if (object instanceof  GameObjectGroup) {
                ((GameObjectGroup) object).load();
            }
        }
    }

    public void unload() {
        for (GameObject object : children) {
            if (object instanceof GameObjectMesh) {
                ((GameObjectMesh) object).unload();
            } else if (object instanceof  GameObjectGroup) {
                ((GameObjectGroup) object).unload();
            }
        }
    }

}