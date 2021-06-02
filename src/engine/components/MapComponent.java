package engine.components;

import engine.collision.Collider;
import engine.objects.GameObject;

public abstract class MapComponent {

    protected GameObject gameObject;
    protected boolean isCollidable;
    protected Collider collider;

}
