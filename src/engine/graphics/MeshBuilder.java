package engine.graphics;

import engine.math.Vector2f;
import engine.math.Vector3f;
import main.World;
import java.util.List;
import org.newdawn.slick.opengl.Texture;

import java.util.ArrayList;

public class MeshBuilder {

    // Creates a cube
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

    // Creates a rectangular prism
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

    // Creates a plane
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

    public static Mesh UIRect(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f(0.0f, -1.0f * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(1.0f * size, 0.0f, 0.0f), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),
                new Vertex(new Vector3f(1.0f * size, -1.0f * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh UICenter(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f(-0.5f * size, 0.5f * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(0.5f * size, -0.5f * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(0.5f * size, 0.5f * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    public static Mesh UIOrigin(float size, float x, float y, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                new Vertex(new Vector3f((0.0f - x) * size, (-1.0f - y) * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f((0.0f - x) * size, (0.0f - y) * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f((1.0f - x) * size, (0.0f - y) * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f((1.0f - x) * size, (-1.0f - y) * size, 0.0f), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    // Creates a tiled plane
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

    public static Mesh Terrain(Material m) {
        Vertex[] vertices = new Vertex[511 * 511 * 4];
        int[] tris = new int[511 * 511 * 6];

        for (int x = 0, i = 0, j = 0; x < 511; x++) {
            for (int z = 0; z < 511; z++) {

                vertices[i] = new Vertex(new Vector3f((0.0f + x),  World.getHeightMap()[x][z + 1],  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f));
                vertices[i + 1] = new Vertex(new Vector3f((0.0f + x),  World.getHeightMap()[x][z],  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f));
                vertices[i + 2] = new Vertex(new Vector3f((1.0f + x),  World.getHeightMap()[x + 1][z],  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f));
                vertices[i + 3] = new Vertex(new Vector3f((1.0f + x),  World.getHeightMap()[x + 1][z + 1],  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f));
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

    // Creates the mesh for the player head
    public static Mesh PlayerHead(Material m) {
        float size = 0.5f;
        float atlasScale = 20.0f / 256.0f;
        Mesh mesh = new Mesh(new Vertex[] {
                //Back face
                new Vertex(new Vector3f(-0.5f * size, 1.0f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(3.0f * atlasScale, 1.0f * atlasScale)),
                new Vertex(new Vector3f(-0.5f * size, 0.0f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(3.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 0.0f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(4.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 1.0f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(4.0f * atlasScale, 1.0f * atlasScale)),

                //Front face
                new Vertex(new Vector3f(-0.5f * size, 1.0f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f * atlasScale, 1.0f * atlasScale)),
                new Vertex(new Vector3f(-0.5f * size, 0.0f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 0.0f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(2.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 1.0f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(2.0f * atlasScale, 1.0f * atlasScale)),

                //Right face
                new Vertex(new Vector3f( 0.5f * size, 1.0f * size, 0.5f * size), new Vector3f(1, 0, 0), new Vector2f(2.0f * atlasScale, 1.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 0.0f * size, 0.5f * size), new Vector3f(1, 0, 0), new Vector2f(2.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 0.0f * size, -0.5f * size), new Vector3f(1, 0, 0), new Vector2f(3.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 1.0f * size, -0.5f * size), new Vector3f(1, 0, 0), new Vector2f(3.0f * atlasScale, 1.0f * atlasScale)),

                //Left face
                new Vertex(new Vector3f(-0.5f * size, 1.0f * size, -0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(0.0f * atlasScale, 1.0f * atlasScale)),
                new Vertex(new Vector3f(-0.5f * size, 0.0f * size, -0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(0.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f(-0.5f * size, 0.0f * size,  0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(1.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f(-0.5f * size, 1.0f * size,  0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(1.0f * atlasScale, 1.0f * atlasScale)),

                //Top face
                new Vertex(new Vector3f(-0.5f * size, 1.0f * size,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f * atlasScale, 0.0f * atlasScale)),
                new Vertex(new Vector3f(-0.5f * size, 1.0f * size, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f * atlasScale, 1.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 1.0f * size, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f * atlasScale, 1.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 1.0f * size,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f * atlasScale, 0.0f * atlasScale)),

                //Bottom face
                new Vertex(new Vector3f(-0.5f * size, 0.0f * size,  0.5f * size), new Vector3f(0, -1, 0), new Vector2f(1.0f * atlasScale, 0.0f * atlasScale)),
                new Vertex(new Vector3f(-0.5f * size, 0.0f * size, -0.5f * size), new Vector3f(0, -1, 0), new Vector2f(1.0f * atlasScale, 1.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 0.0f * size, -0.5f * size), new Vector3f(0, -1, 0), new Vector2f(2.0f * atlasScale, 1.0f * atlasScale)),
                new Vertex(new Vector3f( 0.5f * size, 0.0f * size,  0.5f * size), new Vector3f(0, -1, 0), new Vector2f(2.0f * atlasScale, 0.0f * atlasScale)),
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

    // Creates the mesh for the player torso
    public static Mesh PlayerTorso(Material m) {
        float atlasScale = 20.0f / 256.0f;
        Mesh mesh = new Mesh(new Vertex[] {
                //Back face
                new Vertex(new Vector3f(-0.35f, 0.75f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(3.2f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f(-0.35f, -0.15f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(3.2f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f( 0.35f, -0.15f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(4.6f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f( 0.35f, 0.75f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(4.6f * atlasScale, 2.0f * atlasScale)),

                //Front face
                new Vertex(new Vector3f(-0.35f, 0.75f, 0.15f), new Vector3f(0, 0, 1), new Vector2f(0.8f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f(-0.35f, -0.15f, 0.15f), new Vector3f(0, 0, 1), new Vector2f(0.8f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(0.35f, -0.15f, 0.15f), new Vector3f(0, 0, 1), new Vector2f(2.2f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(0.35f, 0.75f, 0.15f), new Vector3f(0, 0, 1), new Vector2f(2.2f * atlasScale, 2.0f * atlasScale)),

                //Right face
                new Vertex(new Vector3f(0.35f, 0.75f, 0.15f), new Vector3f(1, 0, 0), new Vector2f(2.4f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f(0.35f, -0.15f, 0.15f), new Vector3f(1, 0, 0), new Vector2f(2.4f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(0.35f, -0.15f, -0.15f), new Vector3f(1, 0, 0), new Vector2f(3.0f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(0.35f, 0.75f, -0.15f), new Vector3f(1, 0, 0), new Vector2f(3.0f * atlasScale, 2.0f * atlasScale)),

                //Left face
                new Vertex(new Vector3f(-0.35f, 0.75f, -0.15f), new Vector3f(-1, 0, 0), new Vector2f(0.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f(-0.35f, -0.15f, -0.15f), new Vector3f(-1, 0, 0), new Vector2f(0.0f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(-0.35f, -0.15f, 0.15f), new Vector3f(-1, 0, 0), new Vector2f(0.6f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(-0.35f, 0.75f, 0.15f), new Vector3f(-1, 0, 0), new Vector2f(0.6f * atlasScale, 2.0f * atlasScale)),

                //Top face
                new Vertex(new Vector3f(-0.35f, 0.75f, 0.15f), new Vector3f(0, 1, 0), new Vector2f(0.8f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(-0.35f, 0.75f, -0.15f), new Vector3f(0, 1, 0), new Vector2f(0.8f * atlasScale, 4.3f * atlasScale)),
                new Vertex(new Vector3f(0.35f, 0.75f, -0.15f), new Vector3f(0, 1, 0), new Vector2f(2.2f * atlasScale, 4.3f * atlasScale)),
                new Vertex(new Vector3f(0.35f, 0.75f, 0.15f), new Vector3f(0, 1, 0), new Vector2f(2.2f * atlasScale, 3.7f * atlasScale)),

                //Bottom face
                new Vertex(new Vector3f(-0.35f,-0.15f, 0.15f), new Vector3f(0, -1, 0), new Vector2f(3.2f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(-0.35f,-0.15f, -0.15f), new Vector3f(0, -1, 0), new Vector2f(3.2f * atlasScale, 4.3f * atlasScale)),
                new Vertex(new Vector3f(0.35f, -0.15f, -0.15f), new Vector3f(0, -1, 0), new Vector2f(4.6f * atlasScale, 4.3f * atlasScale)),
                new Vertex(new Vector3f(0.35f, -0.15f, 0.15f), new Vector3f(0, -1, 0), new Vector2f(4.6f * atlasScale, 3.7f * atlasScale)),
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

    // Creates the mesh for the player arm
    public static Mesh PlayerArm(Material m) {
        float atlasScale = 20.0f / 256.0f;
        Mesh mesh = new Mesh(new Vertex[] {
                //Back face
                new Vertex(new Vector3f(-0.15f, 0.2f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(7.4f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f(-0.15f, -0.55f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(7.4f * atlasScale, 3.5f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, -0.55f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(8.0f * atlasScale, 3.5f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, 0.2f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(8.0f * atlasScale, 2.0f * atlasScale)),

                //Front face
                new Vertex(new Vector3f(-0.15f, 0.2f,  0.15f), new Vector3f(0, 0, 1), new Vector2f(5.8f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f(-0.15f, -0.55f,  0.15f), new Vector3f(0, 0, 1), new Vector2f(5.8f * atlasScale, 3.5f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, -0.55f,  0.15f), new Vector3f(0, 0, 1), new Vector2f(6.4f * atlasScale, 3.5f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, 0.2f,  0.15f), new Vector3f(0, 0, 1), new Vector2f(6.4f * atlasScale, 2.0f * atlasScale)),

                //Right face
                new Vertex(new Vector3f( 0.15f, 0.2f, 0.15f), new Vector3f(1, 0, 0), new Vector2f(6.6f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, -0.55f, 0.15f), new Vector3f(1, 0, 0), new Vector2f(6.6f * atlasScale, 3.5f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, -0.55f, -0.15f), new Vector3f(1, 0, 0), new Vector2f(7.2f * atlasScale, 3.5f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, 0.2f, -0.15f), new Vector3f(1, 0, 0), new Vector2f(7.2f * atlasScale, 2.0f * atlasScale)),

                //Left face
                new Vertex(new Vector3f(-0.15f, 0.2f, -0.15f), new Vector3f(-1, 0, 0), new Vector2f(5.0f * atlasScale, 2.0f * atlasScale)),
                new Vertex(new Vector3f(-0.15f, -0.55f, -0.15f), new Vector3f(-1, 0, 0), new Vector2f(5.0f * atlasScale, 3.5f * atlasScale)),
                new Vertex(new Vector3f(-0.15f, -0.55f,  0.15f), new Vector3f(-1, 0, 0), new Vector2f(5.6f * atlasScale, 3.5f * atlasScale)),
                new Vertex(new Vector3f(-0.15f, 0.2f,  0.15f), new Vector3f(-1, 0, 0), new Vector2f(5.6f * atlasScale, 2.0f * atlasScale)),

                //Top face
                new Vertex(new Vector3f(-0.15f, 0.2f,  0.15f), new Vector3f(0, 1, 0), new Vector2f(7.4f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(-0.15f, 0.2f, -0.15f), new Vector3f(0, 1, 0), new Vector2f(7.4f * atlasScale, 4.3f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, 0.2f, -0.15f), new Vector3f(0, 1, 0), new Vector2f(8.0f * atlasScale, 4.3f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, 0.2f,  0.15f), new Vector3f(0, 1, 0), new Vector2f(8.0f * atlasScale, 3.7f * atlasScale)),

                //Bottom face
                new Vertex(new Vector3f(-0.15f, -0.55f,  0.15f), new Vector3f(0, -1, 0), new Vector2f(5.8f * atlasScale, 3.7f * atlasScale)),
                new Vertex(new Vector3f(-0.15f, -0.55f, -0.15f), new Vector3f(0, -1, 0), new Vector2f(5.8f * atlasScale, 4.3f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, -0.55f, -0.15f), new Vector3f(0, -1, 0), new Vector2f(4.6f * atlasScale, 4.3f * atlasScale)),
                new Vertex(new Vector3f( 0.15f, -0.55f,  0.15f), new Vector3f(0, -1, 0), new Vector2f(4.6f * atlasScale, 3.7f * atlasScale)),
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

    // Creates the mesh for the player leg
    public static Mesh PlayerLeg(Material m) {
        float atlasScale = 20.0f / 256.0f;
        Mesh mesh = new Mesh(new Vertex[] {
                //Back face
                new Vertex(new Vector3f(-0.175f, 0.0f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(7.4f * atlasScale, 4.8f * atlasScale)),
                new Vertex(new Vector3f(-0.175f, -0.85f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(7.4f * atlasScale, 6.5f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, -0.85f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(8.0f * atlasScale, 6.5f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, 0.0f, -0.15f), new Vector3f(0, 0, -1), new Vector2f(8.0f * atlasScale, 4.8f * atlasScale)),

                //Front face
                new Vertex(new Vector3f(-0.175f, 0.0f,  0.15f), new Vector3f(0, 0, 1), new Vector2f(5.8f * atlasScale, 4.8f * atlasScale)),
                new Vertex(new Vector3f(-0.175f, -0.85f,  0.15f), new Vector3f(0, 0, 1), new Vector2f(5.8f * atlasScale, 6.5f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, -0.85f,  0.15f), new Vector3f(0, 0, 1), new Vector2f(6.4f * atlasScale, 6.5f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, 0.0f,  0.15f), new Vector3f(0, 0, 1), new Vector2f(6.4f * atlasScale, 4.8f * atlasScale)),

                //Right face
                new Vertex(new Vector3f( 0.175f, 0.0f, 0.15f), new Vector3f(1, 0, 0), new Vector2f(6.6f * atlasScale, 4.8f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, -0.85f, 0.15f), new Vector3f(1, 0, 0), new Vector2f(6.6f * atlasScale, 6.5f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, -0.85f, -0.15f), new Vector3f(1, 0, 0), new Vector2f(7.2f * atlasScale, 6.5f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, 0.0f, -0.15f), new Vector3f(1, 0, 0), new Vector2f(7.2f * atlasScale, 4.8f * atlasScale)),

                //Left face
                new Vertex(new Vector3f(-0.175f, 0.0f, -0.15f), new Vector3f(-1, 0, 0), new Vector2f(5.0f * atlasScale, 4.8f * atlasScale)),
                new Vertex(new Vector3f(-0.175f, -0.85f, -0.15f), new Vector3f(-1, 0, 0), new Vector2f(5.0f * atlasScale, 6.5f * atlasScale)),
                new Vertex(new Vector3f(-0.175f, -0.85f,  0.15f), new Vector3f(-1, 0, 0), new Vector2f(5.6f * atlasScale, 6.5f * atlasScale)),
                new Vertex(new Vector3f(-0.175f, 0.0f,  0.15f), new Vector3f(-1, 0, 0), new Vector2f(5.6f * atlasScale, 4.8f * atlasScale)),

                //Top face
                new Vertex(new Vector3f(-0.175f, 0.0f,  0.15f), new Vector3f(0, 1, 0), new Vector2f(7.4f * atlasScale, 7.7f * atlasScale)),
                new Vertex(new Vector3f(-0.175f, 0.0f, -0.15f), new Vector3f(0, 1, 0), new Vector2f(7.4f * atlasScale, 8.3f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, 0.0f, -0.15f), new Vector3f(0, 1, 0), new Vector2f(8.0f * atlasScale, 8.3f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, 0.0f,  0.15f), new Vector3f(0, 1, 0), new Vector2f(8.0f * atlasScale, 7.7f * atlasScale)),

                //Bottom face
                new Vertex(new Vector3f(-0.175f, -0.85f,  0.15f), new Vector3f(0, -1, 0), new Vector2f(5.8f * atlasScale, 7.7f * atlasScale)),
                new Vertex(new Vector3f(-0.175f, -0.85f, -0.15f), new Vector3f(0, -1, 0), new Vector2f(5.8f * atlasScale, 8.3f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, -0.85f, -0.15f), new Vector3f(0, -1, 0), new Vector2f(4.6f * atlasScale, 8.3f * atlasScale)),
                new Vertex(new Vector3f( 0.175f, -0.85f,  0.15f), new Vector3f(0, -1, 0), new Vector2f(4.6f * atlasScale, 7.7f * atlasScale)),
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

    public static Mesh Tree(float size, float seedA, float seedB, Material m) {

        Vertex[] treeVertices = new Vertex[] {
                //Back face
                new Vertex(new Vector3f(0.0f, size, 0.0f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f, 0.0f, -0.5f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f, -0.5f), new Vector3f(0, 0, -1), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f( 0.0f, size, 0.0f), new Vector3f(0, 0, -1), new Vector2f(0.25f, 0.0f)),

                //Front face
                new Vertex(new Vector3f(0.0f, size,  0.0f), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f, 0.0f,  0.5f), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f,  0.5f), new Vector3f(0, 0, 1), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f( 0.0f, size,  0.0f), new Vector3f(0, 0, 1), new Vector2f(0.25f, 0.0f)),

                //Right face
                new Vertex(new Vector3f( 0.0f, size, 0.0f), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f( 0.5f, 0.0f, -0.5f), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f,  0.5f), new Vector3f(1, 0, 0), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f( 0.0f, size,  0.0f), new Vector3f(1, 0, 0), new Vector2f(0.25f, 0.0f)),

                //Left face
                new Vertex(new Vector3f(0.0f, size, 0.0f), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f, 0.0f, -0.5f), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f(-0.5f, 0.0f, 0.5f), new Vector3f(-1, 0, 0), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f(0.0f, size,  0.0f), new Vector3f(-1, 0, 0), new Vector2f(0.25f, 0.0f)),

                //Bottom face
                new Vertex(new Vector3f(-0.5f, 0.0f, 0.5f), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f, 0.0f, -0.5f), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f, -0.5f), new Vector3f(0, -1, 0), new Vector2f(0.25f, 0.25f)),
                new Vertex(new Vector3f( 0.5f, 0.0f, 0.5f), new Vector3f(0, -1, 0), new Vector2f(0.25f, 0.0f)),
        };
        int[] treeTriangles = new int[] {
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

                //Bottom Face face
                16, 17, 19,
                19, 17, 18,
        };
        int branches = (int) ((size - 2.0f) * 5);
        Vertex[] vertices = new Vertex[20 + (branches * 4 * 2)]; // 4 Verts per face, 2 Faces per branch, 5 branches per size
        int[] triangles = new int[30 + (branches * 6 * 2)];
        System.arraycopy(treeVertices, 0, vertices, 0, 20);
        System.arraycopy(treeTriangles, 0, triangles, 0, 30);
        for (int i = 0; i < branches; i++) {
            float turn = (i * 0.9266462599f + i) + seedA * seedB;
            float height = i * 0.2f + 2.0f;
            float branchSize = 1 + ((float) (branches - i) / branches) * 4;
            float halfBranchSize = branchSize / 2.0f;
            float longBranchSize = (float) Math.sqrt(branchSize * branchSize + (branchSize / 2.0f) * (branchSize / 2.0f));

            Vector3f c = new Vector3f(0.0f, height, 0.0f);
            Vector3f f = new Vector3f((float) Math.sin(turn) * branchSize, height - 0.25f, (float) Math.cos(turn) * branchSize);

            Vector3f r = new Vector3f((float) Math.sin(turn + Math.PI / 2.0f) * halfBranchSize, height - 0.5f, (float) Math.cos(turn + Math.PI / 2.0f) * halfBranchSize);
            Vector3f fr = new Vector3f((float) Math.sin(turn + Math.PI / 6.0f) * longBranchSize, height - 0.75f, (float) Math.cos(turn + Math.PI / 6.0f) * longBranchSize);

            Vector3f l = new Vector3f((float) Math.sin(turn - Math.PI / 2.0f) * halfBranchSize, height - 0.5f, (float) Math.cos(turn - Math.PI / 2.0f) * halfBranchSize);
            Vector3f fl = new Vector3f((float) Math.sin(turn - Math.PI / 6.0f) * longBranchSize, height - 0.75f, (float) Math.cos(turn - Math.PI / 6.0f) * longBranchSize);

            vertices[20 + i * 8    ] = new Vertex(f, new Vector3f(1, 0, 0), new Vector2f(0.625f, 1.0f));
            vertices[20 + i * 8 + 1] = new Vertex(c, new Vector3f(1, 0, 0), new Vector2f(0.625f, 0.0f));
            vertices[20 + i * 8 + 2] = new Vertex(r, new Vector3f(1, 0, 0), new Vector2f(0.25f, 0.0f));
            vertices[20 + i * 8 + 3] = new Vertex(fr, new Vector3f(1, 0, 0), new Vector2f(0.25f, 1.0f));

            vertices[20 + i * 8 + 4] = new Vertex(fl, new Vector3f(-1, 0, 0), new Vector2f(1.0f, 1.0f));
            vertices[20 + i * 8 + 5] = new Vertex(l, new Vector3f(-1, 0, 0), new Vector2f(1.0f, 0.0f));
            vertices[20 + i * 8 + 6] = new Vertex(c, new Vector3f(-1, 0, 0), new Vector2f(0.625f, 0.0f));
            vertices[20 + i * 8 + 7] = new Vertex(f, new Vector3f(-1, 0, 0), new Vector2f(0.625f, 1.0f));

            triangles[30 + i * 12] = 20 + i * 8;
            triangles[30 + i * 12 + 1] = 20 + i * 8 + 1;
            triangles[30 + i * 12 + 2] = 20 + i * 8 + 3;
            triangles[30 + i * 12 + 3] = 20 + i * 8 + 3;
            triangles[30 + i * 12 + 4] = 20 + i * 8 + 1;
            triangles[30 + i * 12 + 5] = 20 + i * 8 + 2;

            triangles[30 + i * 12 + 6] = 20 + i * 8 + 4;
            triangles[30 + i * 12 + 7] = 20 + i * 8 + 5;
            triangles[30 + i * 12 + 8] = 20 + i * 8 + 7;
            triangles[30 + i * 12 + 9] = 20 + i * 8 + 7;
            triangles[30 + i * 12 + 10] = 20 + i * 8 + 5;
            triangles[30 + i * 12 + 11] = 20 + i * 8 + 6;
        }
        return new Mesh(vertices, triangles, m);
    }

    // CHUNK TREE

    public static Mesh TreeChunk(float size, List<Vector3f> positions, List<Integer> seedA, List<Integer> seedB, Material m) {
        int branches = (int) ((size - 2.0f) * 5);
        Vertex[] vertices = new Vertex[(20 + (branches * 4 * 2)) * positions.size()]; // 4 Verts per face, 2 Faces per branch, 5 branches per size
        int[] triangles = new int[(30 + (branches * 6 * 2)) * positions.size()];
        for (int t = 0; t < positions.size(); t++) {
            int vertIndex = (20 + (branches * 4 * 2)) * t;
            int triIndex = (30 + (branches * 6 * 2)) * t;

            float xPos = positions.get(t).getX();
            float yPos = positions.get(t).getY();
            float zPos = positions.get(t).getZ();

            Vector3f position = new Vector3f(xPos, yPos, zPos);

            Vertex[] treeVertices = new Vertex[]{
                    //Back face
                    new Vertex(new Vector3f(0.0f, size, 0.0f).add(position), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
                    new Vertex(new Vector3f(-0.8f, 0.0f, -0.8f).add(position), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.25f)),
                    new Vertex(new Vector3f(0.8f, 0.0f, -0.8f).add(position), new Vector3f(0, 0, -1), new Vector2f(0.25f, 0.25f)),
                    new Vertex(new Vector3f(0.0f, size, 0.0f).add(position), new Vector3f(0, 0, -1), new Vector2f(0.25f, 0.0f)),

                    //Front face
                    new Vertex(new Vector3f(0.0f, size, 0.0f).add(position), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                    new Vertex(new Vector3f(-0.8f, 0.0f, 0.8f).add(position), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.25f)),
                    new Vertex(new Vector3f(0.8f, 0.0f, 0.8f).add(position), new Vector3f(0, 0, 1), new Vector2f(0.25f, 0.25f)),
                    new Vertex(new Vector3f(0.0f, size, 0.0f).add(position), new Vector3f(0, 0, 1), new Vector2f(0.25f, 0.0f)),

                    //Right face
                    new Vertex(new Vector3f(0.0f, size, 0.0f).add(position), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.0f)),
                    new Vertex(new Vector3f(0.8f, 0.0f, -0.8f).add(position), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.25f)),
                    new Vertex(new Vector3f(0.8f, 0.0f, 0.8f).add(position), new Vector3f(1, 0, 0), new Vector2f(0.25f, 0.25f)),
                    new Vertex(new Vector3f(0.0f, size, 0.0f).add(position), new Vector3f(1, 0, 0), new Vector2f(0.25f, 0.0f)),

                    //Left face
                    new Vertex(new Vector3f(0.0f, size, 0.0f).add(position), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.0f)),
                    new Vertex(new Vector3f(-0.8f, 0.0f, -0.8f).add(position), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.25f)),
                    new Vertex(new Vector3f(-0.8f, 0.0f, 0.8f).add(position), new Vector3f(-1, 0, 0), new Vector2f(0.25f, 0.25f)),
                    new Vertex(new Vector3f(0.0f, size, 0.0f).add(position), new Vector3f(-1, 0, 0), new Vector2f(0.25f, 0.0f)),

                    //Bottom face
                    new Vertex(new Vector3f(-0.8f, 0.0f, 0.8f).add(position), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.0f)),
                    new Vertex(new Vector3f(-0.8f, 0.0f, -0.8f).add(position), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.25f)),
                    new Vertex(new Vector3f(0.8f, 0.0f, -0.8f).add(position), new Vector3f(0, -1, 0), new Vector2f(0.25f, 0.25f)),
                    new Vertex(new Vector3f(0.8f, 0.0f, 0.8f).add(position), new Vector3f(0, -1, 0), new Vector2f(0.25f, 0.0f)),
            };
            int[] treeTriangles = new int[]{
                    //Back face
                    vertIndex + 0, vertIndex + 1, vertIndex + 3,
                    vertIndex + 3, vertIndex + 1, vertIndex + 2,

                    //Front face
                    vertIndex + 4, vertIndex + 5, vertIndex + 7,
                    vertIndex + 7, vertIndex + 5, vertIndex + 6,

                    //Right face
                    vertIndex + 8, vertIndex + 9, vertIndex + 11,
                    vertIndex + 11, vertIndex + 9, vertIndex + 10,

                    //Left face
                    vertIndex + 12, vertIndex + 13, vertIndex + 15,
                    vertIndex + 15, vertIndex + 13, vertIndex + 14,

                    //Bottom Face face
                    vertIndex + 16, vertIndex + 17, vertIndex + 19,
                    vertIndex + 19, vertIndex + 17, vertIndex + 18,
            };
            System.arraycopy(treeVertices, 0, vertices, vertIndex, 20);
            System.arraycopy(treeTriangles, 0, triangles, triIndex, 30);
            for (int i = 0; i < branches; i++) {
                float turn = (i * (1 / 1.6180339887f))  + seedA.get(t) * seedB.get(t);
                float height = i * 0.2f + 2.0f;
                float branchSize = 1 + ((float) (branches - i) / branches) * 7;
                float halfBranchSize = branchSize / 2.0f;
                float longBranchSize = (float) Math.sqrt(branchSize * branchSize + (branchSize / 2.0f) * (branchSize / 2.0f));

                Vector3f c = new Vector3f(0.0f, height, 0.0f);
                Vector3f f = new Vector3f((float) Math.sin(turn) * branchSize, height - 0.3f * branchSize, (float) Math.cos(turn) * branchSize);

                Vector3f r = new Vector3f((float) Math.sin(turn + Math.PI / 2.0f) * halfBranchSize, height - 0.5f * branchSize, (float) Math.cos(turn + Math.PI / 2.0f) * halfBranchSize);
                Vector3f fr = new Vector3f((float) Math.sin(turn + Math.PI / 6.0f) * longBranchSize, height - 0.7f * branchSize, (float) Math.cos(turn + Math.PI / 6.0f) * longBranchSize);

                Vector3f l = new Vector3f((float) Math.sin(turn - Math.PI / 2.0f) * halfBranchSize, height - 0.5f * branchSize, (float) Math.cos(turn - Math.PI / 2.0f) * halfBranchSize);
                Vector3f fl = new Vector3f((float) Math.sin(turn - Math.PI / 6.0f) * longBranchSize, height - 0.7f * branchSize, (float) Math.cos(turn - Math.PI / 6.0f) * longBranchSize);

                vertices[vertIndex + 20 + i * 8] = new Vertex(Vector3f.add(position, f), new Vector3f(1, 0, 0), new Vector2f(0.625f, 1.0f));
                vertices[vertIndex + 20 + i * 8 + 1] = new Vertex(Vector3f.add(position, c), new Vector3f(1, 0, 0), new Vector2f(0.625f, 0.0f));
                vertices[vertIndex + 20 + i * 8 + 2] = new Vertex(Vector3f.add(position, r), new Vector3f(1, 0, 0), new Vector2f(0.25f, 0.0f));
                vertices[vertIndex + 20 + i * 8 + 3] = new Vertex(Vector3f.add(position, fr), new Vector3f(1, 0, 0), new Vector2f(0.25f, 1.0f));

                vertices[vertIndex + 20 + i * 8 + 4] = new Vertex(Vector3f.add(position, fl), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 1.0f));
                vertices[vertIndex + 20 + i * 8 + 5] = new Vertex(Vector3f.add(position, l), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 0.0f));
                vertices[vertIndex + 20 + i * 8 + 6] = new Vertex(Vector3f.add(position, c), new Vector3f(-1, 0, 0), new Vector2f(0.625f, 0.0f));
                vertices[vertIndex + 20 + i * 8 + 7] = new Vertex(Vector3f.add(position, f), new Vector3f(-1, 0, 0), new Vector2f(0.625f, 1.0f));

                triangles[triIndex + 30 + i * 12] = vertIndex + 20 + i * 8;
                triangles[triIndex + 30 + i * 12 + 1] = vertIndex + 20 + i * 8 + 1;
                triangles[triIndex + 30 + i * 12 + 2] = vertIndex + 20 + i * 8 + 3;
                triangles[triIndex + 30 + i * 12 + 3] = vertIndex + 20 + i * 8 + 3;
                triangles[triIndex + 30 + i * 12 + 4] = vertIndex + 20 + i * 8 + 1;
                triangles[triIndex + 30 + i * 12 + 5] = vertIndex + 20 + i * 8 + 2;

                triangles[triIndex + 30 + i * 12 + 6] = vertIndex + 20 + i * 8 + 4;
                triangles[triIndex + 30 + i * 12 + 7] = vertIndex + 20 + i * 8 + 5;
                triangles[triIndex + 30 + i * 12 + 8] = vertIndex + 20 + i * 8 + 7;
                triangles[triIndex + 30 + i * 12 + 9] = vertIndex + 20 + i * 8 + 7;
                triangles[triIndex + 30 + i * 12 + 10] = vertIndex + 20 + i * 8 + 5;
                triangles[triIndex + 30 + i * 12 + 11] = vertIndex + 20 + i * 8 + 6;
            }
        }
        return new Mesh(vertices, triangles, m);
    }

    public static Mesh TreeLowQuality(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                //X face
                new Vertex(new Vector3f(-0.75f * size,  1.0f * size, 0.0f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.75f * size, 0.0f, 0.0f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.75f * size, 0.0f, 0.0f), new Vector3f(0, 0, -1), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.75f * size,  1.0f * size, 0.0f), new Vector3f(0, 0, -1), new Vector2f(1.0f, 0.0f)),

                //Z face
                new Vertex(new Vector3f(0.0f,  1.0f * size,  -0.75f * size), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(0.0f, 0.0f,  -0.75f * size), new Vector3f(0, 0, 1), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(0.0f, 0.0f,  0.75f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(0.0f,  1.0f * size,  0.75f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                //Back face
                0, 1, 3,
                3, 1, 2,

                //Front face
                4, 5, 7,
                7, 5, 6
        }, m);
        return mesh;
    }

    public static Mesh TreeLowQuality(float size, List<Vector3f> list, Material m) {

        Mesh mesh = new Mesh(new Vertex[] {
                //X face
                new Vertex(new Vector3f(-0.75f * size,  1.0f * size, 0.0f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.75f * size, 0.0f, 0.0f), new Vector3f(0, 0, -1), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.75f * size, 0.0f, 0.0f), new Vector3f(0, 0, -1), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.75f * size,  1.0f * size, 0.0f), new Vector3f(0, 0, -1), new Vector2f(1.0f, 0.0f)),

                //Z face
                new Vertex(new Vector3f(0.0f,  1.0f * size,  -0.75f * size), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(0.0f, 0.0f,  -0.75f * size), new Vector3f(0, 0, 1), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(0.0f, 0.0f,  0.75f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(0.0f,  1.0f * size,  0.75f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                //Back face
                0, 1, 3,
                3, 1, 2,

                //Front face
                4, 5, 7,
                7, 5, 6
        }, m);
        return mesh;
    }


}
