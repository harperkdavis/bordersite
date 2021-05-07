package engine.graphics.mesh;

import engine.graphics.Material;
import engine.graphics.vertex.Vertex2f;
import engine.math.Vector2f;

public class UiBuilder {

    public static Mesh2f UIRect(float size, Material m) {
        Mesh2f mesh = new Mesh2f(new Vertex2f[] {
                new Vertex2f(new Vector2f(0.0f, -1.0f * size), new Vector2f(0.0f, 1.0f)),
                new Vertex2f(new Vector2f(0.0f, 0.0f), new Vector2f(0.0f, 0.0f)),
                new Vertex2f(new Vector2f(1.0f * size, 0.0f), new Vector2f(1.0f, 0.0f)),
                new Vertex2f(new Vector2f(1.0f * size, -1.0f * size), new Vector2f(1.0f, 1.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh2f UICenter(float size, Material m) {
        Mesh2f mesh = new Mesh2f(new Vertex2f[] {
                new Vertex2f(new Vector2f(-0.5f * size, 0.5f * size), new Vector2f(0.0f, 0.0f)),
                new Vertex2f(new Vector2f(-0.5f * size, -0.5f * size), new Vector2f(0.0f, 1.0f)),
                new Vertex2f(new Vector2f(0.5f * size, -0.5f * size), new Vector2f(1.0f, 1.0f)),
                new Vertex2f(new Vector2f(0.5f * size, 0.5f * size), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh2f UIOrigin(float size, float x, float y, Material m) {
        Mesh2f mesh = new Mesh2f(new Vertex2f[] {
                new Vertex2f(new Vector2f((0.0f - x) * size, (-1.0f - y) * size), new Vector2f(0.0f, 0.0f)),
                new Vertex2f(new Vector2f((0.0f - x) * size, (0.0f - y) * size), new Vector2f(0.0f, 1.0f)),
                new Vertex2f(new Vector2f((1.0f - x) * size, (0.0f - y) * size), new Vector2f(1.0f, 1.0f)),
                new Vertex2f(new Vector2f((1.0f - x) * size, (-1.0f - y) * size), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

}
