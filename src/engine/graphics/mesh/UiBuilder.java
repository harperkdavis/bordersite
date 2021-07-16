package engine.graphics.mesh;

import engine.graphics.Material;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2f;
import engine.math.Vector3f;

public class UiBuilder {

    public static Mesh UIRect(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f(0.0f, -1.0f * size, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(0.0f, 0.0f, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(1.0f * size, 0.0f, 0), new Vector2f(1.0f, 0.0f)),
                new Vertex(new Vector3f(1.0f * size, -1.0f * size, 0), new Vector2f(1.0f, 1.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh UIRectUV(float size, float xSize, float ySize, Material m, Vector2f a, Vector2f b) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f(0.0f, -1.0f * size * ySize, 0), new Vector2f(a.getX(), a.getY())),
                new Vertex(new Vector3f(0.0f, 0.0f, 0), new Vector2f(a.getX(), b.getY())),
                new Vertex(new Vector3f(1.0f * size * xSize, 0.0f, 0), new Vector2f(b.getX(), b.getY())),
                new Vertex(new Vector3f(1.0f * size * xSize, -1.0f * size * ySize, 0), new Vector2f(b.getX(), a.getY())),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh UICenter(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f(-0.5f * size, 0.5f * size, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(0.5f * size, -0.5f * size, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(0.5f * size, 0.5f * size,  0), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh UICenterUV(float size, float xSize, float ySize, Material m, Vector2f a, Vector2f b) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f(-0.5f * size * xSize, 0.5f * size * ySize, 0), new Vector2f(a.getX(), a.getY())),
                new Vertex(new Vector3f(-0.5f * size * xSize, -0.5f * size * ySize, 0), new Vector2f(a.getX(), b.getY())),
                new Vertex(new Vector3f(0.5f * size * xSize, -0.5f * size * ySize, 0), new Vector2f(b.getX(), b.getY())),
                new Vertex(new Vector3f(0.5f * size * xSize, 0.5f * size * ySize,  0), new Vector2f(b.getX(), a.getY())),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh UIOrigin(float size, float x, float y, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f((0.0f - x) * size, (-1.0f - y) * size, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f((0.0f - x) * size, (0.0f - y) * size, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f((1.0f - x) * size, (0.0f - y) * size, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f((1.0f - x) * size, (-1.0f - y) * size, 0), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh UIOriginUV(float size, float xSize, float ySize, float x, float y, Material m, Vector2f a, Vector2f b) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f((0.0f - x) * size * xSize, (-1.0f - y) * size * ySize, 0), new Vector2f(a.getX(), b.getY())),
                new Vertex(new Vector3f((0.0f - x) * size * xSize, (0.0f - y) * size * ySize, 0), new Vector2f(a.getX(), a.getY())),
                new Vertex(new Vector3f((1.0f - x) * size * xSize, (0.0f - y) * size * ySize, 0), new Vector2f(b.getX(), a.getY())),
                new Vertex(new Vector3f((1.0f - x) * size * xSize, (-1.0f - y) * size * ySize, 0), new Vector2f(b.getX(), b.getY())),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

}
