package engine.objects;

import java.util.ArrayList;
import java.util.List;

import engine.math.Transform;
import engine.math.Vector3f;

public abstract class GameObject {

    private Transform transform;
    private boolean visible = true;
    private List<String> tags;

    protected List<GameObject> children;
    protected GameObject parent;

    public GameObject(GameObject parent, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.transform = new Transform(position, rotation, scale);
        this.parent = parent;
        this.children = new ArrayList<>();
        parent.children.add(this);
    }

    public GameObject(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.transform = new Transform(position, rotation, scale);
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public abstract void load();

    public abstract void unload();

    private void fix() {
        fixTransform();
        fixChildren();
    }

    private void fixTransform() {
        if (parent != null) {
            transform.transformBy(parent.transform);
        }
    }

    private void fixChildren() {
        if (children == null) {
            return;
        }

        for (GameObject child : children) {
            child.fix();
        }
    }

    public void resetParent() {
        this.parent.children.remove(this);
        this.parent = null;
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
        this.parent.children.add(this);
    }

    private void addChild(GameObject child) {
        child.setParent(this);
        child.transform.setLocals(transform);
    }

    private void removeChild(GameObject child) {
        child.resetParent();
        child.transform.resetLocals();
    }

    public boolean hasChildren() {
        return (children.size() > 0);
    }

    public Transform getTransform() {
        return transform;
    }

    public List<GameObject> addChild() {
        return children;
    }

    public List<GameObject> getChildren() {
        return children;
    }

    public GameObject getParent() {
        return parent;
    }

    public Vector3f getPosition() {
        return transform.getPosition();
    }

    public Vector3f getRotation() {
        return transform.getRotation();
    }

    public Vector3f getScale() {
        return transform.getScale();
    }

    public void setPosition(Vector3f position) {
        transform.setPosition(position);
        fix();
    }

    public void setRotation(Vector3f rotation) {
        transform.setRotation(rotation);
        fix();
    }

    public void setScale(Vector3f scale) {
        transform.setScale(scale);
        fix();
    }

    public Vector3f getLocalPosition() {
        return new Vector3f(transform.getLocalPosition());
    }

    public Vector3f getLocalRotation() {
        return new Vector3f(transform.getLocalRotation());
    }

    public Vector3f getLocalScale() {
        return new Vector3f(transform.getLocalScale());
    }

    public void setLocalPosition(Vector3f position) {
        transform.setLocalPosition(position);
        fix();
    }

    public void setLocalRotation(Vector3f rotation) {
        transform.setLocalRotation(rotation);
        fix();
    }

    public void setLocalScale(Vector3f scale) {
        transform.setLocalScale(scale);
        fix();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
}
