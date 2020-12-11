package engine.math;

import engine.math.Mathf;
import engine.math.Vector3f;

public class Region3f {

    public Vector3f start;
    public Vector3f end;

    public float minX;
    public float maxX;
    public float minY;
    public float maxY;
    public float minZ;
    public float maxZ;

    public Region3f(Vector3f start, Vector3f end) {
        this.start = start;
        this.end = end;

        minX = Math.min(start.getX(), end.getX());
        maxX = Math.max(start.getX(), end.getX());
        minY = Math.min(start.getY(), end.getY());
        maxY = Math.max(start.getY(), end.getY());
        minZ = Math.min(start.getZ(), end.getZ());
        maxZ = Math.max(start.getZ(), end.getZ());
    }

    public boolean isWithin(Vector3f point) {
        return (point.getX() >= minX && point.getX() <= maxX) && (point.getY() >= minY && point.getY() <= maxY) && (point.getZ() >= minZ && point.getZ() <= maxZ);
    }

    public Vector3f closestPoint(Vector3f point) {
        float x = Mathf.clamp(point.getX(), minX, maxX);
        float y = Mathf.clamp(point.getY(), minY, maxY);
        float z = Mathf.clamp(point.getZ(), minZ, maxZ);

        return new Vector3f(x, y, z);
    }

    public boolean isWithin(Vector3f point, float radius) {
        Vector3f close = closestPoint(point);

        float distance = (close.getX() - point.getX()) * (close.getX() - point.getX()) + (close.getY() - point.getY()) * (close.getY() - point.getY()) + (close.getZ() - point.getZ()) * (close.getZ() - point.getZ());

        return distance * distance < radius * radius;
    }

    public Vector3f collision(Vector3f point, Vector3f movement, float threshold) {

        Vector3f pointMovement = Vector3f.add(point, movement);

        if (!isWithin(pointMovement, threshold)) {
            return pointMovement;
        }

        Vector3f newPoint = new Vector3f(pointMovement);

        if (pointMovement.getY() >= minY && pointMovement.getY() <= maxY) {
            // MINX SIDE
            if (Mathf.intersect(minX, minZ, minX, maxZ, point.getX(), point.getZ(), pointMovement.getX(), pointMovement.getZ())) {
                newPoint.setX(minX - 0.001f);
            }
            // MAXX SIDE
            if (Mathf.intersect(maxX, minZ, maxX, maxZ, point.getX(), point.getZ(), pointMovement.getX(), pointMovement.getZ())) {
                newPoint.setX(maxX + 0.001f);
            }
            // MINZ SIDE
            if (Mathf.intersect(minX, minZ, maxX, minZ, point.getX(), point.getZ(), pointMovement.getX(), pointMovement.getZ())) {
                newPoint.setZ(minZ - 0.001f);
            }
            // MAXZ SIDE
            if (Mathf.intersect(minX, maxZ, maxX, maxZ, point.getX(), point.getZ(), pointMovement.getX(), pointMovement.getZ())) {
                newPoint.setZ(maxZ + 0.001f);
            }
        }
        return newPoint;
    }

}
