package engine.graphics.light;

import engine.math.Vector3f;

public class PointLight {

    public final static PointLight IDENTITY = new PointLight(Vector3f.zero(), Vector3f.zero(), 0, 0, 0);
    private Vector3f color;
    private Vector3f position;
    protected float radius;
    private float linear, quadratic;

    private static float ATT_CONSTANT = 1.0f, ATT_LINEAR = 0.7f, ATT_QUADRATIC = 1.8f;

    public PointLight(Vector3f position) {
        this(position, Vector3f.one());
    }

    public PointLight(Vector3f position, Vector3f color) {
        this(position, color, 16);
    }

    public PointLight(Vector3f position, Vector3f color, float radius) {
        this(position, color, radius, ATT_LINEAR, ATT_QUADRATIC);
    }

    public PointLight(Vector3f position, Vector3f color, float radius, float linear, float quadratic) {
        this.color = color;
        this.position = position;
        this.radius = radius;
        this.linear = linear;
        this.quadratic = quadratic;
    }

    public PointLight(PointLight pointLight) {
        this(new Vector3f(pointLight.getPosition()), new Vector3f(pointLight.getColor()),
                pointLight.getRadius(), pointLight.linear, pointLight.quadratic);
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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float intensity) {
        this.radius = intensity;
    }

    public float getLinear() {
        return linear;
    }

    public float getQuadratic() {
        return quadratic;
    }
}
