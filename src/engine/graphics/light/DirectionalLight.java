package engine.graphics.light;

import engine.math.Vector3;

public class DirectionalLight {

    private Vector3 color;
    private Vector3 direction;
    private float intensity;

    public DirectionalLight(Vector3 color, Vector3 direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public DirectionalLight(DirectionalLight light) {
        this(new Vector3(light.getColor()), new Vector3(light.getDirection()), light.getIntensity());
    }

    public Vector3 getColor() {
        return color;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setColor(Vector3 color) {
        this.color = color;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
