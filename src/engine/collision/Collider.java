package engine.collision;

import engine.math.Vector3f;

public interface Collider {

    Collision getCollision(Vector3f previous, Vector3f position, Vector3f velocity, float height);

}
