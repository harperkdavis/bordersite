package engine.components;

import engine.collision.Collision;
import engine.math.Vector3f;
import engine.objects.GameObject;

public interface Component {

    GameObject getObject();
    Collision getCollision(Vector3f previous, Vector3f position, Vector3f velocity, float height, boolean isGrounded);
    boolean isGrounded(Vector3f position);

}
