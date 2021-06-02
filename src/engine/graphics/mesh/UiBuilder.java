package engine.graphics.mesh;

import engine.graphics.Material;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2;
import engine.math.Vector3;

public class UiBuilder {

    public static Mesh UIRect(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3(0.0f, -1.0f * size, 0), new Vector2(0.0f, 1.0f)),
                new Vertex(new Vector3(0.0f, 0.0f, 0), new Vector2(0.0f, 0.0f)),
                new Vertex(new Vector3(1.0f * size, 0.0f, 0), new Vector2(1.0f, 0.0f)),
                new Vertex(new Vector3(1.0f * size, -1.0f * size, 0), new Vector2(1.0f, 1.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh UICenter(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3(-0.5f * size, 0.5f * size, 0), new Vector2(0.0f, 0.0f)),
                new Vertex(new Vector3(-0.5f * size, -0.5f * size, 0), new Vector2(0.0f, 1.0f)),
                new Vertex(new Vector3(0.5f * size, -0.5f * size, 0), new Vector2(1.0f, 1.0f)),
                new Vertex(new Vector3(0.5f * size, 0.5f * size,  0), new Vector2(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh UIOrigin(float size, float x, float y, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3((0.0f - x) * size, (-1.0f - y) * size, 0), new Vector2(0.0f, 0.0f)),
                new Vertex(new Vector3((0.0f - x) * size, (0.0f - y) * size, 0), new Vector2(0.0f, 1.0f)),
                new Vertex(new Vector3((1.0f - x) * size, (0.0f - y) * size, 0), new Vector2(1.0f, 1.0f)),
                new Vertex(new Vector3((1.0f - x) * size, (-1.0f - y) * size, 0), new Vector2(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

}
