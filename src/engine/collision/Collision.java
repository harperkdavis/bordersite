package engine.collision;

import engine.math.Vector3;

public class Collision {

    private final Vector3 positionResult;
    private final Vector3 velocityResult;
    private final boolean resultGrounded;

    public Collision(Vector3 positionResult, Vector3 velocityResult, boolean resultGrounded) {
        this.positionResult = positionResult;
        this.velocityResult = velocityResult;
        this.resultGrounded = resultGrounded;
    }

    public Vector3 getPositionResult() {
        return positionResult;
    }

    public Vector3 getVelocityResult() {
        return velocityResult;
    }

    public boolean isResultGrounded() {
        return resultGrounded;
    }

}
