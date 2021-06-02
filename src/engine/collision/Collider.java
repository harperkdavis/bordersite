package engine.collision;

import engine.math.Vector3;

public interface Collider {

    Collision getCollision(Vector3 previous, Vector3 position, Vector3 velocity, float height);

}
