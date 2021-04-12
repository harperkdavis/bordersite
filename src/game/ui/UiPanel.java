package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh2f;
import engine.graphics.vertex.Vertex2f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.math.Vector4f;

import static game.ui.UserInterface.screen;

public class UiPanel extends UiObject {

    public UiPanel(float x1, float y1, float x2, float y2, float l, Vector4f color) {
        super(new Vector3f(0, 0, l), Vector3f.zero(), Vector3f.one(), createPanel(x1, y1, x2, y2, new Material("/textures/default.png")), color);
    }

    public UiPanel(float x1, float y1, float x2, float y2, float l, float transparency) {
        super(new Vector3f(0, 0, l), Vector3f.zero(), Vector3f.one(), createPanel(x1, y1, x2, y2, new Material("/textures/default.png")), new Vector4f(0, 0, 0, transparency));
    }

    protected static Mesh2f createPanel(float x1, float y1, float x2, float y2, Material m) {
        Vector2f pointAA = screen(x1, y1);
        Vector2f pointAB = screen(x1, y2);
        Vector2f pointBB = screen(x2, y2);
        Vector2f pointBA = screen(x2, y1);
        Mesh2f mesh = new Mesh2f(new Vertex2f[] {
                new Vertex2f(pointAA, new Vector2f(0.0f, 0.0f)),
                new Vertex2f(pointAB, new Vector2f(0.0f, 1.0f)),
                new Vertex2f(pointBB, new Vector2f(1.0f, 1.0f)),
                new Vertex2f(pointBA, new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }
}
