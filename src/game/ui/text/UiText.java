package game.ui.text;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;

import static game.ui.UserInterface.p;

public class UiText extends GameObject {

    private static FontFile MAIN_FONT = new FontFile("main.fnt");

    private Vector3f position;
    private String text;
    private int font, horizontal, vertical;

    public UiText(Vector3f position, String text, int font, int horizontal, int vertical) {
        super(position, createTextMesh(text, horizontal, vertical, font));
        this.position = position;
        this.text = text;
        this.font = font;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public UiText(Vector3f position, String text) {
        this(position, text, 0, 0, 0);
    }

    public UiText(Vector3f position, String text, int font) {
        this(position, text, font, 0, 0);
    }

    private void updateMesh() {
        this.setMesh(createTextMesh(text, horizontal, vertical, font));
    }

    public void setText(String text) {
        this.text = text;
        updateMesh();
    }


    private static Mesh createTextMesh(String text, int textHorizontal, int textVertical, int font) {
        Vertex[] vertices = new Vertex[text.replace(" ", "").length() * 4];
        int[] tris = new int[text.replace(" ", "").length() * 6];
        FontFile currentFont;
        switch(font) {
            default -> {
                currentFont = MAIN_FONT;
            }
        }

        Vector3f cursor = new Vector3f(0, 0, 0);

        for (int i = 0, j = 0, k = 0; i < text.length(); i++) {

            FontCharacter character = currentFont.getFontCharacter(text.charAt(i));
            if (character == null) {
                cursor.add(currentFont.getSpaceWidth(), 0, 0);
                continue;
            }
            // TODO
            Vector3f topLeft = cursor.plus(character.getxOffset(), -currentFont.getLineHeight(), 0);

            vertices[j] = new Vertex(topLeft, new Vector2f(character.getxTextureCoord(), character.getYMaxTextureCoord()));
            vertices[j + 1] = new Vertex(topLeft.plus(0, character.getSizeY(), 0), new Vector2f(character.getxTextureCoord(), character.getyTextureCoord()));
            vertices[j + 2] = new Vertex(topLeft.plus(character.getSizeX(), character.getSizeY(), 0), new Vector2f(character.getXMaxTextureCoord(), character.getyTextureCoord()));
            vertices[j + 3] = new Vertex(topLeft.plus(character.getSizeX(), 0, 0), new Vector2f(character.getXMaxTextureCoord(), character.getYMaxTextureCoord()));

            cursor.add(character.getxAdvance(), 0, 0);

            tris[k] = j;
            tris[k + 1] = j + 2;
            tris[k + 2] = j + 1;
            tris[k + 3] = j;
            tris[k + 4] = j + 3;
            tris[k + 5] = j + 2;

            j += 4;
            k += 6;
        }

        return new Mesh(vertices, tris, Material.FONT_MAIN_STROKE);
    }

}
