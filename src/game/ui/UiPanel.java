package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.math.Vector4f;
import engine.objects.GameObject;

import static game.ui.UserInterface.screen;

public class UiPanel extends GameObject {

    public UiPanel(float x1, float y1, float x2, float y2, float l, Vector4f color) {
        super(new Vector3f(0, 0, l), Vector3f.zero(), Vector3f.one(), createPanel(x1, y1, x2, y2, Material.DEFAULT), color);
    }

    public UiPanel(float x1, float y1, float x2, float y2, float l, float transparency) {
        super(new Vector3f(0, 0, l), Vector3f.zero(), Vector3f.one(), createPanel(x1, y1, x2, y2, Material.DEFAULT), new Vector4f(0, 0, 0, transparency));
    }

    protected static Mesh createPanel(float x1, float y1, float x2, float y2, Material m) {
        Vector3f pointAA = screen(x1, y1, 0);
        Vector3f pointAB = screen(x1, y2, 0);
        Vector3f pointBB = screen(x2, y2, 0);
        Vector3f pointBA = screen(x2, y1, 0);
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(pointAA, new Vector2f(0.0f, 0.0f)),
                new Vertex(pointAB, new Vector2f(0.0f, 1.0f)),
                new Vertex(pointBB, new Vector2f(1.0f, 1.0f)),
                new Vertex(pointBA, new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }
}
