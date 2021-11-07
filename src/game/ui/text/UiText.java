package game.ui.text;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.GameObject;

import static game.ui.UserInterface.p;

public class UiText extends GameObject {

    private static FontFile MAIN_FONT = new FontFile("bordersite.fnt");

    private Vector3f position;
    private String text;
    private int font, horizontal, vertical;
    private float maxWidth;

    public UiText(Vector3f position, String text, int font, float maxWidth, int horizontal, int vertical) {
        super(position, createTextMesh(text, maxWidth, horizontal, vertical, font));
        this.position = position;
        this.text = text;
        this.font = font;
        this.maxWidth = maxWidth;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public UiText(Vector3f position, String text, int font, float maxWidth) {
        this(position, text, font, maxWidth, 0, 0);
    }

    public UiText(Vector3f position, String text) {
        this(position, text, 0, Float.MAX_VALUE, 0, 0);
    }

    public UiText(Vector3f position, String text, int font) {
        this(position, text, font, Float.MAX_VALUE, 0, 0);
    }

    private void updateMesh() {
        this.setMesh(createTextMesh(text, maxWidth, horizontal, vertical, font));
    }

    public void setText(String text) {
        if (!this.text.equals(text)) {
            this.text = text;
            updateMesh();
        }
    }


    private static Mesh createTextMesh(String text, float width, int textHorizontal, int textVertical, int font) {
        String[] words = text.split(" ");
        int totalWidth = 0;
        for (int i = 0; i < words.length; i++) {
            totalWidth += words[i].length();
        }

        Vertex[] vertices = new Vertex[totalWidth * 4];
        int[] tris = new int[totalWidth * 6];
        FontFile currentFont;
        Material fontMaterial;
        switch(font) {
            case 1 -> {
                currentFont = MAIN_FONT;
                fontMaterial = Material.FONT_MAIN;
            }
            default -> {
                currentFont = MAIN_FONT;
                fontMaterial = Material.FONT_MAIN_STROKE;
            }
        }

        Vector3f cursor = new Vector3f(0, 0, 0);

        int j = 0;
        int k = 0;

        for (int w = 0; w < words.length; w++) {
            String word = words[w];
            Vector3f cursorCopy = new Vector3f(cursor);
            for (int i = 0; i < word.length(); i++) {
                FontCharacter character = currentFont.getFontCharacter(word.charAt(i));
                cursorCopy.add(character.getxAdvance(), 0, 0);
                if (cursorCopy.getX() > width) {
                    cursor.setX(0);
                    cursor.add(0, p(20), 0);
                }
            }
            for (int i = 0; i < word.length(); i++) {

                FontCharacter character = currentFont.getFontCharacter(word.charAt(i));
                if (character == null) {
                    cursor.add(currentFont.getSpaceWidth(), 0, 0);
                    continue;
                }

                Vector3f topLeft = cursor.plus(character.getxOffset(), -character.getyOffset() - character.getSizeY(), 0);

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
            cursor.add(currentFont.getSpaceWidth(), 0, 0);

        }

        return new Mesh(vertices, tris, fontMaterial);
    }

}
