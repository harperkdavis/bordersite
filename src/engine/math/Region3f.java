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

    public Vector3f collision(Vector3f point, float threshold) {
        if (!isWithin(point, threshold)) {
            return point;
        }

        Vector3f newPoint = new Vector3f(point);

        float xVector = Math.min(Math.abs(point.getX() - minX), Math.abs(point.getX() - maxX));
        float yVector = Math.min(Math.abs(point.getY() - minY), Math.abs(point.getY() - maxY));
        float zVector = Math.min(Math.abs(point.getZ() - minZ), Math.abs(point.getZ() - maxZ));

        threshold += 0.2f;

        if (yVector <= xVector && yVector <= zVector) { // Push Y
            if (Math.abs(point.getY() - minY) >= Math.abs(point.getY() - maxY)) { // Push Y to MAX
                newPoint.setY(maxY + threshold);
            } else { // Push Y to MIN
                newPoint.setY(minY - threshold);
            }
        } else if (xVector <= yVector && xVector <= zVector) { // Push X
            if (Math.abs(point.getX() - minX) >= Math.abs(point.getX() - maxX)) { // Push X to MAX
                newPoint.setX(maxX + threshold);
            } else { // Push X to MIN
                newPoint.setX(minX - threshold);
            }
        } else if (zVector <= xVector && zVector <= yVector) { // Push Z
            if (Math.abs(point.getZ() - minZ) >= Math.abs(point.getZ() - maxZ)) { // Push Z to MAX
                newPoint.setZ(maxZ + threshold);
            } else { // Push Z to MIN
                newPoint.setZ(minZ - threshold);
            }
        }

        return newPoint;
    }
}
