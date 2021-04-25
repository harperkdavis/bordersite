package engine.components;

import engine.collision.Collision3f;
import engine.math.Vector3f;

public abstract class ComponentBuilder {

    private Vector3f a;
    private Vector3f b;
    private Collision3f region;

    public ComponentBuilder(Vector3f a, Vector3f b) {
        this.a = a;
        this.b = b;
    }

}
