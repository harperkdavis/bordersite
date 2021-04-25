package engine.graphics.light;

import engine.math.Vector3f;

public class SpotLight {

    public final static SpotLight IDENTITY = new SpotLight(PointLight.IDENTITY, Vector3f.zero(), 0);
    private PointLight pointLight;
    private Vector3f coneDirection;
    private float cutoff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutoff) {
        this.pointLight = pointLight;
        this.coneDirection =coneDirection;
        this.cutoff = cutoff;
    }

    public SpotLight(SpotLight spotLight) {
        this.pointLight = spotLight.pointLight;
        this.coneDirection = spotLight.coneDirection;
        this.cutoff = spotLight.cutoff;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }
}
