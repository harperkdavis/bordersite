package engine.graphics.light;

import engine.math.Vector3;

public class Fog {

    private boolean active;
    private Vector3 color;
    private float density;

    public static Fog NOFOG = new Fog();

    public Fog() {
        active = false;
        this.color = new Vector3(0, 0, 0);
        this.density = 0;
    }

    public Fog(boolean active, Vector3 colour, float density) {
        this.color = colour;
        this.density = density;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector3 getColor() {
        return color;
    }

    public void setColor(Vector3 color) {
        this.color = color;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
