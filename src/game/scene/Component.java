package game.scene;

import engine.collision.Collider3f;
import engine.objects.GameObject;

public class Component {

    private final GameObject baseObject;
    private final Collider3f collider;

    public Component(GameObject baseObject, Collider3f collider) {
        this.baseObject = baseObject;
        this.collider = collider;
    }

    public GameObject getBaseObject() {
        return baseObject;
    }

    public Collider3f getCollider() {
        return collider;
    }
}
