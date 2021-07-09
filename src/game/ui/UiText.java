package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;

import static game.ui.UserInterface.p;

public class UiText extends GameObject {

    private Vector3f position;
    private String text;
    private float characterHeight;
    private int font, horizontal, vertical;

    public UiText(Vector3f position, String text, float characterHeight, int font, int horizontal, int vertical) {
        super(position, createTextMesh(text, characterHeight, horizontal, vertical, font));
        this.position = position;
        this.text = text;
        this.characterHeight = characterHeight;
        this.font = font;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public UiText(Vector3f position, String text) {
        this(position, text, p(16), 0, 0, 0);
    }

    public UiText(Vector3f position, String text, int font) {
        this(position, text, font == 0 ? p(16) : p(32), font, 0, 0);
    }

    private void updateMesh() {
        this.setMesh(createTextMesh(text, characterHeight, horizontal, vertical, font));
    }

    public void setText(String text) {
        this.text = text;
        updateMesh();
    }


    private static Mesh createTextMesh(String text, float characterHeight, int textHorizontal, int textVertical, int font) {
        text = " " + text;
        Vertex[] vertices = new Vertex[text.length() * 4];
        int[] tris = new int[text.length() * 6];
        float xUnit = 1.0f / 16.0f;
        float yUnit = 1.0f / 16.0f;
        float metric = characterHeight;
        float textWidth = 0.55f * text.length() * metric;
        float length = -0.8f;
        float modifier = switch (textHorizontal) {
            default -> 0;
            case 1 -> -textWidth / 2;
            case 2 -> -textWidth;
        };

        for (int i = 0, j = 0, k = 0; i < text.length(); i++) {

            int character = text.charAt(i) - 32;
            int xTex = (character % 16);
            int yTex = (character / 16);

            vertices[j] = new Vertex(new Vector3f(modifier + length * characterHeight, 0, -length / 1000), new Vector2f(xTex * xUnit, yTex * yUnit ));
            vertices[j + 1] = new Vertex(new Vector3f(modifier + length * characterHeight, -characterHeight, -length / 1000), new Vector2f(xTex * xUnit, yTex * yUnit + yUnit));
            vertices[j + 2] = new Vertex(new Vector3f(modifier + length * characterHeight + characterHeight, -characterHeight, -length / 1000), new Vector2f(xTex * xUnit + xUnit, yTex * yUnit + yUnit));
            vertices[j + 3] = new Vertex(new Vector3f(modifier + length * characterHeight + characterHeight, 0, -length / 1000), new Vector2f(xTex * xUnit + xUnit, yTex * yUnit));

            tris[k] = j;
            tris[k + 1] = j + 1;
            tris[k + 2] = j + 3;
            tris[k + 3] = j + 3;
            tris[k + 4] = j + 1;
            tris[k + 5] = j + 2;

            j += 4;
            k += 6;
            if (!(font == 1)) {
                length += 0.55f;
            }
        }

        return new Mesh(vertices, tris, (font == 0 ? Material.FONT_SYNE_SMALL : Material.FONT_SYNE_BIG));
    }

}
