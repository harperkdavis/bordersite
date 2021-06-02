package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2;
import engine.math.Vector3;
import engine.math.Vector4;
import engine.objects.GameObject;

import static game.ui.UserInterface.screen;

public class UiPanel extends GameObject {

    public UiPanel(float x1, float y1, float x2, float y2, float l, Vector4 color) {
        super(new Vector3(0, 0, l), Vector3.zero(), Vector3.one(), createPanel(x1, y1, x2, y2, Material.DEFAULT), color);
    }

    public UiPanel(float x1, float y1, float x2, float y2, float l, float transparency) {
        super(new Vector3(0, 0, l), Vector3.zero(), Vector3.one(), createPanel(x1, y1, x2, y2, Material.DEFAULT), new Vector4(0, 0, 0, transparency));
    }

    protected static Mesh createPanel(float x1, float y1, float x2, float y2, Material m) {
        Vector3 pointAA = screen(x1, y1, 0);
        Vector3 pointAB = screen(x1, y2, 0);
        Vector3 pointBB = screen(x2, y2, 0);
        Vector3 pointBA = screen(x2, y1, 0);
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(pointAA, new Vector2(0.0f, 0.0f)),
                new Vertex(pointAB, new Vector2(0.0f, 1.0f)),
                new Vertex(pointBB, new Vector2(1.0f, 1.0f)),
                new Vertex(pointBA, new Vector2(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }
}
