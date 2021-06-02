package engine.objects;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh;
import engine.math.Transform;
import engine.math.Vector3;
import engine.math.Vector4;

public class GameObject {

    private final Transform transform;
    private boolean visible = true;
    private List<String> tags;

    protected List<GameObject> children;
    protected GameObject parent;

    private Mesh mesh;
    private Vector4 color;

    public GameObject(Vector3 position, Vector3 rotation, Vector3 scale, Mesh mesh) {
        this(position, rotation, scale, mesh, new Vector4(1, 1, 1, 1));
    }

    public GameObject(Vector3 position, Vector3 rotation, Vector3 scale, Mesh mesh, Vector4 color) {
        this.transform = new Transform(position, rotation, scale);
        this.mesh = mesh;
        this.color = color;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public GameObject(Vector3 position, Mesh mesh) {
        this(position, Vector3.zero(), Vector3.one(), mesh);
    }

    public GameObject(Vector3 position, Mesh mesh, Vector4 color) {
        this(position, Vector3.zero(), Vector3.one(), mesh, color);
    }

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
        transform.setLocals(parent.getTransform());
    }

    public void addChild(GameObject child) {
        child.setParent(this);
    }

    public void removeChild(GameObject child) {
        child.resetParent();
        child.transform.resetLocals();
    }

    public boolean hasChildren() {
        return (children.size() > 0);
    }

    public Transform getTransform() {
        return transform;
    }

    public List<GameObject> getChildren() {
        return children;
    }

    public GameObject getParent() {
        return parent;
    }

    public Vector3 getPosition() {
        return transform.getPosition();
    }

    public Vector3 getRotation() {
        return transform.getRotation();
    }

    public Vector3 getScale() {
        return transform.getScale();
    }

    public void setPosition(Vector3 position) {
        transform.setPosition(position);
        // fix();
    }

    public void setRotation(Vector3 rotation) {
        transform.setRotation(rotation);
        // fix();
    }

    public void setScale(Vector3 scale) {
        transform.setScale(scale);
        // fix();
    }

    public Vector3 getLocalPosition() {
        return new Vector3(transform.getLocalPosition());
    }

    public Vector3 getLocalRotation() {
        return new Vector3(transform.getLocalRotation());
    }

    public void setLocalPosition(Vector3 position) {
        transform.setLocalPosition(position);
        fix();
    }

    public void setLocalRotation(Vector3 rotation) {
        transform.setLocalRotation(rotation);
        fix();
    }

    public void load() {
        mesh.create();
    }

    public void setMesh(Mesh mesh) {
        this.mesh.updateMesh(mesh);
    }

    public void setMaterial(Material material) {
        this.mesh.setMaterial(material);
    }

    public void unload() {
        mesh.destroy();
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector4 getColor() {
        return color;
    }

    public void setColor(Vector4 color) {
        this.color = color;
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
