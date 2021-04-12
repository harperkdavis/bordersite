package engine.graphics.mesh;

import engine.graphics.Material;
import engine.graphics.vertex.Vertex3f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.graphics.mesh.Mesh3f;
import game.world.World;

public class MeshBuilder {

    // Creates a cube
    public static Mesh3f Cube(float size, Material m) {
        Mesh3f mesh = new Mesh3f(new Vertex3f[] {
                //Back face
                new Vertex3f(new Vector3f(-0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(0, 0, -1), new Vector2f(1.0f, 0.0f)),

                //Front face
                new Vertex3f(new Vector3f(-0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(-0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(0, 0, 1), new Vector2f(1.0f, 0.0f)),

                //Right face
                new Vertex3f(new Vector3f( 0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f( 0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(1, 0, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Left face
                new Vertex3f(new Vector3f(-0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f(-0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f(-0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Top face
                new Vertex3f(new Vector3f(-0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(-0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size,  0.5f * size, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size,  0.5f * size,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),

                //Bottom face
                new Vertex3f(new Vector3f(-0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(0, -1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size, -0.5f * size, -0.5f * size), new Vector3f(0, -1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size, -0.5f * size,  0.5f * size), new Vector3f(0, -1, 0), new Vector2f(1.0f, 0.0f)),
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
    public static Mesh3f Rect(Vector3f a, Vector3f b, Material m) {
        Mesh3f mesh = new Mesh3f(new Vertex3f[] {
                //Back face
                new Vertex3f(new Vector3f(a.getX(), a.getY(), a.getZ()), new Vector3f(0, 0, -1), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(a.getX(), b.getY(), a.getZ()), new Vector3f(0, 0, -1), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(), b.getY(), a.getZ()), new Vector3f(0, 0, -1), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(), a.getY(), a.getZ()), new Vector3f(0, 0, -1), new Vector2f(1.0f, 0.0f)),

                //Front face
                new Vertex3f(new Vector3f(a.getX(), a.getY(), b.getZ()), new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(a.getX(), b.getY(), b.getZ()), new Vector3f(0, 0, 1), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(), b.getY(), b.getZ()), new Vector3f(0, 0, 1), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(), a.getY(), b.getZ()), new Vector3f(0, 0, 1), new Vector2f(1.0f, 0.0f)),

                //Right face
                new Vertex3f(new Vector3f(b.getX(), a.getY(), a.getZ()), new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(b.getX(), b.getY(), a.getZ()), new Vector3f(1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(), b.getY(), b.getZ()), new Vector3f(1, 0, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(), a.getY(), b.getZ()), new Vector3f(1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Left face
                new Vertex3f(new Vector3f(a.getX(), a.getY(), a.getZ()), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(a.getX(), b.getY(), a.getZ()), new Vector3f(-1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f(a.getX(), b.getY(), b.getZ()), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f(a.getX(), a.getY(), b.getZ()), new Vector3f(-1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Top face
                new Vertex3f(new Vector3f(a.getX(),  a.getY(), b.getZ()), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(a.getX(),  a.getY(), a.getZ()), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(),  a.getY(), a.getZ()), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(),  a.getY(), b.getZ()), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),

                //Bottom face
                new Vertex3f(new Vector3f(a.getX(), b.getY(), b.getZ()), new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(a.getX(), b.getY(), a.getZ()), new Vector3f(0, -1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(), b.getY(), a.getZ()), new Vector3f(0, -1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f(b.getX(), b.getY(), b.getZ()), new Vector3f(0, -1, 0), new Vector2f(1.0f, 0.0f)),
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
    public static Mesh3f Plane(float size, Material m) {
        Mesh3f mesh = new Mesh3f(new Vertex3f[] {
                new Vertex3f(new Vector3f(-0.5f * size,  0.0f,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex3f(new Vector3f(-0.5f * size,  0.0f, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size,  0.0f, -0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex3f(new Vector3f( 0.5f * size,  0.0f,  0.5f * size), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                0, 1, 3,
                3, 1, 2,
        }, m);
        return mesh;
    }

    // Creates a tiled plane
    public static Mesh3f TiledPlane(int size, Material m) {
        Vertex3f[] vertices = new Vertex3f[size * size * 4];
        int[] tris = new int[size * size * 6];
        for (int x = 0, i = 0, j = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                vertices[i] = new Vertex3f(new Vector3f((0.0f + x),  0.0f,  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f));
                vertices[i + 1] = new Vertex3f(new Vector3f((0.0f + x),  0.0f,  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f));
                vertices[i + 2] = new Vertex3f(new Vector3f((1.0f + x),  0.0f,  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f));
                vertices[i + 3] = new Vertex3f(new Vector3f((1.0f + x),  0.0f,  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f));
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

        return new Mesh3f(vertices, tris, m);
    }

    public static Mesh3f Terrain(Material m) {
        Vertex3f[] vertices = new Vertex3f[511 * 511 * 4];
        int[] tris = new int[511 * 511 * 6];

        for (int x = 0, i = 0, j = 0; x < 511; x++) {
            for (int z = 0; z < 511; z++) {


                vertices[i] = new Vertex3f(new Vector3f((0.0f + x),  World.getHeightMap()[x][z + 1],  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f));
                vertices[i + 1] = new Vertex3f(new Vector3f((0.0f + x),  World.getHeightMap()[x][z],  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f));
                vertices[i + 2] = new Vertex3f(new Vector3f((1.0f + x),  World.getHeightMap()[x + 1][z],  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f));
                vertices[i + 3] = new Vertex3f(new Vector3f((1.0f + x),  World.getHeightMap()[x + 1][z + 1],  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f));
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

        return new Mesh3f(vertices, tris, m);
    }

}
