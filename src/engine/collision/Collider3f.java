package engine.collision;

import engine.math.Vector3f;

public interface Collider3f {

    boolean isGrounded(Vector3f point);
    float getGroundedHeight(Vector3f point);
    Collision getCollision(Vector3f previous, Vector3f position, Vector3f velocity, float height);

}
