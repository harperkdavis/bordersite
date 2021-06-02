package engine.graphics.text;

import engine.graphics.Material;
import engine.graphics.mesh.Mesh;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2;
import engine.math.Vector3;

public class TextMeshBuilder {

    private static final Material ubuntuMaterial = Material.FONT_UBUNTU;
    private static final Material kremlinMaterial = Material.FONT_KREMLIN;

    // Creates the mesh for text
    public static Mesh TextMesh(String text, float characterHeight, TextMode textMode) {
        return createTextMesh(text, characterHeight, textMode, false);
    }

    public static Mesh TextMesh(String text, float characterHeight, TextMode textMode, boolean kremlin) {
        return createTextMesh(text, characterHeight, textMode, kremlin);
    }

    private static Mesh createTextMesh(String text, float characterHeight, TextMode textMode, boolean kremlinFont) {
        text = " " + text;
        Material textMaterial = kremlinFont ? kremlinMaterial : ubuntuMaterial;
        Vertex[] vertices = new Vertex[text.length() * 4];
        int[] tris = new int[text.length() * 6];
        float xUnit = 1.0f / 16.0f;
        float yUnit = 1.0f / 8.0f;
        float metric = characterHeight / 1.2f;
        float textWidth = textLength(text, kremlinFont) * metric;
        float modifier = 0;
        float length = 0;
        modifier = switch (textMode) {
            case LEFT -> 0;
            case CENTER -> -textWidth / 2;
            case RIGHT -> -textWidth;
        };

        for (int i = 0, j = 0, k = 0; i < text.length(); i++) {

            int character = text.charAt(i) - 32;
            int xTex = (character % 16);
            int yTex = (character / 16);

            vertices[j] = new Vertex(new Vector3(modifier + length * metric, characterHeight, -length / 1000), new Vector2(xTex * xUnit, yTex * yUnit ));
            vertices[j + 1] = new Vertex(new Vector3(modifier + length * metric, 0, -length / 1000), new Vector2(xTex * xUnit, yTex * yUnit + yUnit / 2));
            vertices[j + 2] = new Vertex(new Vector3(modifier + length * metric + characterHeight, 0, -length / 1000), new Vector2(xTex * xUnit + xUnit, yTex * yUnit + yUnit / 2));
            vertices[j + 3] = new Vertex(new Vector3(modifier + length * metric + characterHeight, characterHeight, -length / 1000), new Vector2(xTex * xUnit + xUnit, yTex * yUnit));

            tris[k] = j;
            tris[k + 1] = j + 1;
            tris[k + 2] = j + 3;
            tris[k + 3] = j + 3;
            tris[k + 4] = j + 1;
            tris[k + 5] = j + 2;

            j += 4;
            k += 6;
            if (!kremlinFont) {
                length += 0.6f;
            } else {
                length += (text.charAt(i) == 'i' || text.charAt(i) == 'I' || text.charAt(i) == '1' || text.charAt(i) == '.' || text.charAt(i) == ',' || text.charAt(i) == '\"') ? 0.35f : 0.95f;
            }
        }

        return new Mesh(vertices, tris, textMaterial);
    }

    private static float textLength(String text, boolean kremlin) {
        float length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (!kremlin) {
                length += 0.55f;
            } else {
                length += (text.charAt(i) == 'i' || text.charAt(i) == 'I' || text.charAt(i) == '1' || text.charAt(i) == '.' || text.charAt(i) == ',' || text.charAt(i) == '\"') ? 0.5f : 1.2f;
            }
        }
        return length;
    }

}
