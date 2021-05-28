package engine.graphics.light;

import engine.math.Vector3f;

public class PointLight {

    public final static PointLight IDENTITY = new PointLight(Vector3f.zero(), Vector3f.zero(), 0, 0, 0);
    private Vector3f color;
    private Vector3f position;
    protected float intensity;
    private float linear, quadratic;

    public PointLight(Vector3f position) {
        this.color = Vector3f.one();
        this.position = position;
        this.intensity = 4;
        this.quadratic = 1;
        this.linear = 2;
    }

    public PointLight(Vector3f position, Vector3f color) {
        this.color = color;
        this.position = position;
        this.intensity = 4;
        this.quadratic = 1;
        this.linear = 2;
    }

    public PointLight(Vector3f position, Vector3f color, float intensity) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
        this.quadratic = 1;
        this.linear = 2;
    }

    public PointLight(Vector3f position, Vector3f color, float intensity, float linear, float quadratic) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;

        this.linear = linear;
        this.quadratic = quadratic;
    }

    public PointLight(PointLight pointLight) {
        this(new Vector3f(pointLight.getPosition()), new Vector3f(pointLight.getColor()),
                pointLight.getIntensity(), pointLight.linear, pointLight.quadratic);
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getLinear() {
        return linear;
    }

    public float getQuadratic() {
        return quadratic;
    }
}
