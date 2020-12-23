package engine.graphics;

import engine.math.Vector2f;
import engine.math.Vector3f;

public class MeshBuilder {

    public static Mesh Cube(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                //Back face
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(1.0f, 0.0f)),

                //Front face
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f, 0.0f)),

                //Right face
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(1, 0, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Left face
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Top face
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),

                //Bottom face
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(0, -1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(0, -1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(0, -1, 0), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                //Back face
                0, 1, 3,
                3, 1, 2,

                //Front face
                4, 5, 7,
                7, 5, 6,

                //Right face
                8, 9, 11,
                11, 9, 10,

                //Left face
                12, 13, 15,
                15, 13, 14,

                //Top face
                16, 17, 19,
                19, 17, 18,

                //Bottom face
                20, 21, 23,
                23, 21, 22
        }, m);
        return mesh;
    }

    public static Mesh Rect(Vector3f a, Vector3f b, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                //Back face
                new Vertex(new Vector3f(a.getX(), a.getY(), a.getZ()), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(a.getX(), b.getY(), a.getZ()), new Vector3f(0, 0, -1), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(), b.getY(), a.getZ()), new Vector3f(0, 0, -1), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(), a.getY(), a.getZ()), new Vector3f(0, 0, -1), new Vector2f(1.0f, 0.0f)),

                //Front face
                new Vertex(new Vector3f(a.getX(), a.getY(), b.getZ()), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(a.getX(), b.getY(), b.getZ()), new Vector3f(0, 0, 1), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(), b.getY(), b.getZ()), new Vector3f(0, 0, 1), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(), a.getY(), b.getZ()), new Vector3f(0, 0, 1), new Vector2f(1.0f, 0.0f)),

                //Right face
                new Vertex(new Vector3f(b.getX(), a.getY(), a.getZ()), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(b.getX(), b.getY(), a.getZ()), new Vector3f(1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(), b.getY(), b.getZ()), new Vector3f(1, 0, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(), a.getY(), b.getZ()), new Vector3f(1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Left face
                new Vertex(new Vector3f(a.getX(), a.getY(), a.getZ()), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(a.getX(), b.getY(), a.getZ()), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(a.getX(), b.getY(), b.getZ()), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(a.getX(), a.getY(), b.getZ()), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Top face
                new Vertex(new Vector3f(a.getX(),  a.getY(), b.getZ()), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(a.getX(),  a.getY(), a.getZ()), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(),  a.getY(), a.getZ()), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(),  a.getY(), b.getZ()), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),

                //Bottom face
                new Vertex(new Vector3f(a.getX(), b.getY(), b.getZ()), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(a.getX(), b.getY(), a.getZ()), new Vector3f(0, -1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(), b.getY(), a.getZ()), new Vector3f(0, -1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(b.getX(), b.getY(), b.getZ()), new Vector3f(0, -1, 0), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                //Back face
                0, 1, 3,
                3, 1, 2,

                //Front face
                4, 5, 7,
                7, 5, 6,

                //Right face
                8, 9, 11,
                11, 9, 10,

                //Left face
                12, 13, 15,
                15, 13, 14,

                //Top face
                16, 17, 19,
                19, 17, 18,

                //Bottom face
                20, 21, 23,
                23, 21, 22
        }, m);
        return mesh;
    }

    public static Mesh Plane(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f(-0.5f * size,  0.0f,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size,  0.0f, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.0f, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.0f,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh TiledPlane(int size, Material m) {
        Vertex[] vertices = new Vertex[size * size * 4];
        int[] tris = new int[size * size * 6];
        for (int x = 0, i = 0, j = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                vertices[i] = new Vertex(new Vector3f((0.0f + x),  0.0f,  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f));
                vertices[i + 1] = new Vertex(new Vector3f((0.0f + x),  0.0f,  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f));
                vertices[i + 2] = new Vertex(new Vector3f((1.0f + x),  0.0f,  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f));
                vertices[i + 3] = new Vertex(new Vector3f((1.0f + x),  0.0f,  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f));
                tris[j] = i;
                tris[j + 1] = i + 1;
                tris[j + 2] = i + 3;
                tris[j + 3] = i + 3;
                tris[j + 4] = i + 1;
                tris[j + 5] = i + 2;

                i += 4;
                j += 6;
            }
        }

        return new Mesh(vertices, tris, m);
    }

    public static Mesh TextMesh(String text, float characterHeight, TextMode textMode) {
        text = " " + text;
        Material textMaterial = new Material("/textures/gamefont.png");
        Vertex[] vertices = new Vertex[text.length() * 4];
        int[] tris = new int[text.length() * 6];
        float xUnit = 1.0f / 16.0f;
        float yUnit = 1.0f / 8.0f;
        float metric = characterHeight / 1.5f;
        float textWidth = text.length() * metric;
        float modifier = 0;
        float length = 0;
        switch (textMode) {
            case LEFT:
                modifier = 0;
                break;
            case CENTER:
                modifier = -textWidth / 2;
                break;
            case RIGHT:
                modifier = -textWidth;
                break;
        }

        for (int i = 0, j = 0, k = 0; i < text.length(); i++) {

            int character = text.charAt(i) - 32;
            int xTex = (character % 16);
            int yTex = (character / 16);

            vertices[j] = new Vertex(new Vector3f(modifier + length * metric, characterHeight, -length / 1000), new Vector3f(0, 1, 0), new Vector2f(xTex * xUnit, yTex * yUnit ));
            vertices[j + 1] = new Vertex(new Vector3f(modifier + length * metric, 0, -length / 1000), new Vector3f(0, 1, 0), new Vector2f(xTex * xUnit, yTex * yUnit + yUnit / 2));
            vertices[j + 2] = new Vertex(new Vector3f(modifier + length * metric + characterHeight, 0, -length / 1000), new Vector3f(0, 1, 0), new Vector2f(xTex * xUnit + xUnit, yTex * yUnit + yUnit / 2));
            vertices[j + 3] = new Vertex(new Vector3f(modifier + length * metric + characterHeight, characterHeight, -length / 1000), new Vector3f(0, 1, 0), new Vector2f(xTex * xUnit + xUnit, yTex * yUnit));

            tris[k] = j;
            tris[k + 1] = j + 1;
            tris[k + 2] = j + 3;
            tris[k + 3] = j + 3;
            tris[k + 4] = j + 1;
            tris[k + 5] = j + 2;

            j += 4;
            k += 6;

            if (("" + text.charAt(i)).toUpperCase().equals("" + text.charAt(i))) {
                length += 0.2f;
            }
            if (text.charAt(i) == '.' || text.charAt(i) == ',' || text.charAt(i) == '!') {
                length += 0.2f;
            } else if (text.charAt(i) == 'i' || text.charAt(i) == 'I' || text.charAt(i) == 'j') {
                length += 0.5f;
            } else if (text.charAt(i) == 'l' || text.charAt(i) == 'f') {
                length += 0.6f;
            } else if (text.charAt(i) == 't' || text.charAt(i) == 'r' || text.charAt(i) == 's') {
                length += 0.8f;
            } else if (text.charAt(i) == 'w' || text.charAt(i) == 'W' || text.charAt(i) == 'M' || text.charAt(i) == 'Q' || text.charAt(i) == 'O' || text.charAt(i) == 'D') {
                length += 1.4f;
            } else {
                length += 1;
            }
        }

        return new Mesh(vertices, tris, textMaterial);
    }

}
