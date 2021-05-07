package engine.collision;

import engine.math.Mathf;
import engine.math.Vector3f;

public class BoxCollider3f implements Collider3f {

    public Vector3f start;
    public Vector3f end;

    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;
    private final float minZ;
    private final float maxZ;

    private final float inflatedMinX;
    private final float inflatedMaxX;
    private final float inflatedMinZ;
    private final float inflatedMaxZ;

    public BoxCollider3f(Vector3f start, Vector3f end) {
        this.start = start;
        this.end = end;

        minX = Math.min(start.getX(), end.getX());
        maxX = Math.max(start.getX(), end.getX());
        minY = Math.min(start.getY(), end.getY());
        maxY = Math.max(start.getY(), end.getY());
        minZ = Math.min(start.getZ(), end.getZ());
        maxZ = Math.max(start.getZ(), end.getZ());

        inflatedMinX = minX - 0.3f;
        inflatedMaxX = maxX + 0.3f;
        inflatedMinZ = minZ - 0.3f;
        inflatedMaxZ = maxZ + 0.3f;

    }

    private boolean isWithinBounds(Vector3f point) {
        return (point.getX() >= minX && point.getX() <= maxX) && (point.getY() >= minY && point.getY() <= maxY) && (point.getZ() >= minZ && point.getZ() <= maxZ);
    }

    private boolean isWithinInflatedXY(Vector3f point) {
        return (point.getX() > inflatedMinX && point.getX() < inflatedMaxX) && (point.getZ() > inflatedMinZ && point.getZ() < inflatedMaxZ);
    }

    @Override
    public boolean isGrounded(Vector3f point) {
        Vector3f newPoint = new Vector3f(point).add(new Vector3f(0, -0.02f, 0));

        return (isWithinInflatedXY(point) && newPoint.getY() < maxY && newPoint.getY() > (minY + maxY) / 2.0f);
    }

    @Override
    public float getGroundedHeight(Vector3f point) {
        if (isWithinInflatedXY(point)) {
            return maxY;
        }
        return 0;
    }

    @Override
    public Collision getCollision(Vector3f previous, Vector3f position, Vector3f velocity, float height) {


        boolean isGrounded = false;
        Vector3f newPoint = new Vector3f(position), newVelocity = new Vector3f(velocity);

        if (position.getY() + height >= minY && position.getY() <= maxY - 0.1f) {
            // MIN X
            if (Mathf.intersect(position.getX(), position.getZ(), previous.getX(), previous.getZ(), inflatedMinX, inflatedMaxZ, inflatedMinX, inflatedMinZ)) {
                newPoint.setX(inflatedMinX);
                newVelocity.setX(0);
            }
            // MAX X
            if (Mathf.intersect(previous.getX(), previous.getZ(), position.getX(), position.getZ(), inflatedMaxX, inflatedMinZ, inflatedMaxX, inflatedMaxZ)) {
                newPoint.setX(inflatedMaxX);
                newVelocity.setX(0);
            }
            // MIN Z
            if (Mathf.intersect(previous.getX(), previous.getZ(), position.getX(), position.getZ(), inflatedMinX, inflatedMinZ, inflatedMaxX, inflatedMinZ)) {
                newPoint.setZ(inflatedMinZ);
                newVelocity.setZ(0);
            }
            // MAX Z
            if (Mathf.intersect(position.getX(), position.getZ(), previous.getX(), previous.getZ(), inflatedMaxX, inflatedMaxZ, inflatedMinX, inflatedMaxZ)) {
                newPoint.setZ(inflatedMaxZ);
                newVelocity.setZ(0);
            }
        } else if (position.getY() + height >= minY && position.getY() <= maxY && position.getY() >= maxY - 0.1f) {
            position.setY(maxY);
        }
        if (isWithinInflatedXY(newPoint)) {
            if (position.getY() + height > minY && position.getY() < maxY) {
                newPoint.setY(minY - height);
                newVelocity.setY(0);
            }
            if (position.getY() < maxY && position.getY() > (minY + maxY) / 2.0f) {
                newPoint.setY(maxY);
                newVelocity.setY(0);
                isGrounded = true;
            }
        }

        return new Collision(newPoint, newVelocity, isGrounded);

    }

    private Vector3f closestPoint(Vector3f point) {
        float x = Mathf.clamp(point.getX(), minX, maxX);
        float y = Mathf.clamp(point.getY(), minY, maxY);
        float z = Mathf.clamp(point.getZ(), minZ, maxZ);

        return new Vector3f(x, y, z);
    }

    private boolean isWithin(Vector3f point, float radius) {
        Vector3f close = closestPoint(point);

        float distance = (close.getX() - point.getX()) * (close.getX() - point.getX()) + (close.getY() - point.getY()) * (close.getY() - point.getY()) + (close.getZ() - point.getZ()) * (close.getZ() - point.getZ());

        return distance * distance < radius * radius;
    }

}
