package engine.collision;

import engine.math.Mathf;
import engine.math.Vector3f;

public class RampCollider3f implements Collider3f {

    public Vector3f start;
    public Vector3f end;

    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;
    private final float minZ;
    private final float maxZ;

    private float inflatedMinX;
    private float inflatedMaxX;
    private float inflatedMinZ;
    private float inflatedMaxZ;

    private int direction;

    public RampCollider3f(Vector3f start, Vector3f end, int direction) {
        this.start = start;
        this.end = end;

        minX = Math.min(start.getX(), end.getX());
        maxX = Math.max(start.getX(), end.getX());
        minY = Math.min(start.getY(), end.getY());
        maxY = Math.max(start.getY(), end.getY());
        minZ = Math.min(start.getZ(), end.getZ());
        maxZ = Math.max(start.getZ(), end.getZ());

        inflatedMinX = minX;
        inflatedMaxX = maxX;
        inflatedMinZ = minZ;
        inflatedMaxZ = maxZ;

        this.direction = direction;
    }

    private float getHeightAt(Vector3f point) {
        switch(direction) {
            default -> {
                return Mathf.lerp(minY, maxY, (point.getZ() - minZ) / (maxZ - minZ));
            }
            case 1 -> {
                return Mathf.lerp(minY, maxY, (point.getX() - minX) / (maxX - minX));
            }
            case 2 -> {
                return Mathf.lerp(maxY, minY, (point.getZ() - minZ) / (maxZ - minZ));
            }
            case 3 -> {
                return Mathf.lerp(maxY, minY, (point.getX() - minX) / (maxX - minX));
            }
        }
    }

    private boolean isWithinBounds(Vector3f point) {
        return (point.getX() >= minX && point.getX() <= maxX) && (point.getY() >= minY && point.getY() <= getHeightAt(point)) && (point.getZ() >= minZ && point.getZ() <= maxZ);
    }

    private boolean isWithinInflatedXY(Vector3f point) {
        return (point.getX() > (direction == 3 ? minX : inflatedMinX) && point.getX() < (direction == 1 ? maxX : inflatedMaxX)) && (point.getZ() > (direction == 0 ? minZ : inflatedMinZ) && point.getZ() < (direction == 2 ? maxZ : inflatedMaxZ));
    }

    @Override
    public boolean isGrounded(Vector3f point) {
        Vector3f newPointA = new Vector3f(point).add(new Vector3f(0, -0.02f, 0));
        Vector3f newPointB = new Vector3f(point).add(new Vector3f(0, -0.2f, 0));

        return (isWithinInflatedXY(point)) && ((newPointA.getY() < getHeightAt(newPointA) && newPointA.getY() > minY - 0.03f) || (newPointB.getY() < getHeightAt(newPointB) && newPointB.getY() > minY - 0.03f));
    }

    @Override
    public float getGroundedHeight(Vector3f point) {
        if (isWithinInflatedXY(point)) {
            return getHeightAt(point);
        }
        return 0;
    }

    @Override
    public Collision getCollision(Vector3f previous, Vector3f position, Vector3f velocity, float height) {

        float inflate = 0.18f;

        inflatedMinX = minX - inflate;
        inflatedMaxX = maxX + inflate;
        inflatedMinZ = minZ - inflate;
        inflatedMaxZ = maxZ + inflate;

        boolean isGrounded = false;
        Vector3f newPoint = new Vector3f(position), newVelocity = new Vector3f(velocity);

        if (position.getY() + height >= minY && position.getY() <= getHeightAt(position) - 0.2f) {
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
        } else if (position.getY() + height >= minY && position.getY() <= getHeightAt(position) && position.getY() >= getHeightAt(position) - 0.1f) {
            position.setY(getHeightAt(position));
        }
        if (isWithinInflatedXY(newPoint)) {
            if (position.getY() + height > minY && position.getY() < getHeightAt(position)) {
                newPoint.setY(minY - height);
                newVelocity.setY(0);
            }
            if (position.getY() < getHeightAt(position) && position.getY() > minY - 0.03f) {
                newPoint.setY(getHeightAt(position));
                newVelocity.setY(0);
                isGrounded = true;
            }
        }

        return new Collision(newPoint, newVelocity, isGrounded);

    }

    private Vector3f closestPoint(Vector3f point) {
        float x = Mathf.clamp(point.getX(), minX, maxX);
        float y = Mathf.clamp(point.getY(), minY, getHeightAt(point));
        float z = Mathf.clamp(point.getZ(), minZ, maxZ);

        return new Vector3f(x, y, z);
    }

    private boolean isWithin(Vector3f point, float radius) {
        Vector3f close = closestPoint(point);

        float distance = (close.getX() - point.getX()) * (close.getX() - point.getX()) + (close.getY() - point.getY()) * (close.getY() - point.getY()) + (close.getZ() - point.getZ()) * (close.getZ() - point.getZ());

        return distance * distance < radius * radius;
    }

}
