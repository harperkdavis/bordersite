package game.scene;

import engine.collision.Collider;
import engine.objects.GameObject;

public class Component {

    private final GameObject baseObject;
    private final Collider collider;

    public Component(GameObject baseObject, Collider collider) {
        this.baseObject = baseObject;
        this.collider = collider;
    }

    public GameObject getBaseObject() {
        return baseObject;
    }

    public Collider getCollider() {
        return collider;
    }
}
