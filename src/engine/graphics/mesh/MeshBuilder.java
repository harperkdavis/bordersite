package engine.graphics.mesh;

import engine.graphics.Material;
import engine.graphics.vertex.Vertex;
import engine.math.Vector2f;
import engine.math.Vector3f;
import game.scene.Scene;

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
        Vector3f uvLo = new Vector3f(Math.min(a.getX(), b.getX()) % 1, Math.min(a.getY(), b.getY()) % 1, Math.min(a.getZ(), b.getZ()) % 1);
        Vector3f uvHi = new Vector3f(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ()));

        float minX = Math.min(a.getX(), b.getX()), maxX = Math.max(a.getX(), b.getX());
        float minY = Math.min(a.getY(), b.getY()), maxY = Math.max(a.getY(), b.getY());
        float minZ = Math.min(a.getZ(), b.getZ()), maxZ = Math.max(a.getZ(), b.getZ());

        Mesh mesh = new Mesh(new Vertex[] {

                new Vertex(new Vector3f(minX, maxY, minZ), new Vector3f(0, 0, -1), new Vector2f(uvLo.getX(), uvHi.getY())),
                new Vertex(new Vector3f(minX, minY, minZ), new Vector3f(0, 0, -1), new Vector2f(uvLo.getX(), uvLo.getY())),
                new Vertex(new Vector3f(maxX, minY, minZ), new Vector3f(0, 0, -1), new Vector2f(uvHi.getX(), uvLo.getY())),
                new Vertex(new Vector3f(maxX, maxY, minZ), new Vector3f(0, 0, -1), new Vector2f(uvHi.getX(), uvHi.getY())),

                new Vertex(new Vector3f(maxX, maxY, maxZ), new Vector3f(0, 0, 1), new Vector2f(uvLo.getX(), uvHi.getY())),
                new Vertex(new Vector3f(maxX, minY, maxZ), new Vector3f(0, 0, 1), new Vector2f(uvLo.getX(), uvLo.getY())),
                new Vertex(new Vector3f(minX, minY, maxZ), new Vector3f(0, 0, 1), new Vector2f(uvHi.getX(), uvLo.getY())),
                new Vertex(new Vector3f(minX, maxY, maxZ), new Vector3f(0, 0, 1), new Vector2f(uvHi.getX(), uvHi.getY())),

                new Vertex(new Vector3f(minX, maxY, maxZ), new Vector3f(-1, 0, 0), new Vector2f(uvLo.getZ(), uvHi.getY())),
                new Vertex(new Vector3f(minX, minY, maxZ), new Vector3f(-1, 0, 0), new Vector2f(uvLo.getZ(), uvLo.getY())),
                new Vertex(new Vector3f(minX, minY, minZ), new Vector3f(-1, 0, 0), new Vector2f(uvHi.getZ(), uvLo.getY())),
                new Vertex(new Vector3f(minX, maxY, minZ), new Vector3f(-1, 0, 0), new Vector2f(uvHi.getZ(), uvHi.getY())),

                new Vertex(new Vector3f(maxX, maxY, minZ), new Vector3f(1, 0, 0), new Vector2f(uvLo.getZ(), uvHi.getY())),
                new Vertex(new Vector3f(maxX, minY, minZ), new Vector3f(1, 0, 0), new Vector2f(uvLo.getZ(), uvLo.getY())),
                new Vertex(new Vector3f(maxX, minY, maxZ), new Vector3f(1, 0, 0), new Vector2f(uvHi.getZ(), uvLo.getY())),
                new Vertex(new Vector3f(maxX, maxY, maxZ), new Vector3f(1, 0, 0), new Vector2f(uvHi.getZ(), uvHi.getY())),

                new Vertex(new Vector3f(minX, minY, minZ), new Vector3f(0, -1, 0), new Vector2f(uvLo.getX(), uvHi.getZ())),
                new Vertex(new Vector3f(minX, minY, maxZ), new Vector3f(0, -1, 0), new Vector2f(uvLo.getX(), uvLo.getZ())),
                new Vertex(new Vector3f(maxX, minY, maxZ), new Vector3f(0, -1, 0), new Vector2f(uvHi.getX(), uvLo.getZ())),
                new Vertex(new Vector3f(maxX, minY, minZ), new Vector3f(0, -1, 0), new Vector2f(uvHi.getX(), uvHi.getZ())),

                new Vertex(new Vector3f(minX, maxY, maxZ), new Vector3f(0, 1, 0), new Vector2f(uvLo.getX(), uvHi.getZ())),
                new Vertex(new Vector3f(minX, maxY, minZ), new Vector3f(0, 1, 0), new Vector2f(uvLo.getX(), uvLo.getZ())),
                new Vertex(new Vector3f(maxX, maxY, minZ), new Vector3f(0, 1, 0), new Vector2f(uvHi.getX(), uvLo.getZ())),
                new Vertex(new Vector3f(maxX, maxY, maxZ), new Vector3f(0, 1, 0), new Vector2f(uvHi.getX(), uvHi.getZ()))
        }, new int[] {

                0, 2, 1,
                0, 3, 2,

                4, 6, 5,
                4, 7, 6,

                8, 10, 9,
                8, 11, 10,

                12, 14, 13,
                12, 15, 14,

                16, 18, 17,
                16, 19, 18,

                20, 22, 21,
                20, 23, 22

        }, m);
        return mesh;
    }


    // Creates a ramp
    public static Mesh Ramp(Vector3f a, Vector3f b, int direction, Material m) {
        Vector3f fL, fR, bL, bR, tL, tR;
        float minX = Math.min(a.getX(), b.getX());
        float maxX = Math.max(a.getX(), b.getX());
        float minY = Math.min(a.getY(), b.getY());
        float maxY = Math.max(a.getY(), b.getY());
        float minZ = Math.min(a.getZ(), b.getZ());
        float maxZ = Math.max(a.getZ(), b.getZ());
        switch (direction) {
            default -> {
                fL = new Vector3f(minX, minY, minZ);
                fR = new Vector3f(maxX, minY, minZ);
                bL = new Vector3f(minX, minY, maxZ);
                bR = new Vector3f(maxX, minY, maxZ);
            }
            case 1 -> {
                fL = new Vector3f(maxX, minY, minZ);
                fR = new Vector3f(maxX, minY, maxZ);
                bL = new Vector3f(minX, minY, minZ);
                bR = new Vector3f(minX, minY, maxZ);
            }
            case 2 -> {
                fL = new Vector3f(maxX, minY, maxZ);
                fR = new Vector3f(minX, minY, maxZ);
                bL = new Vector3f(maxX, minY, minZ);
                bR = new Vector3f(minX, minY, minZ);
            }
            case 3 -> {
                fL = new Vector3f(minX, minY, maxZ);
                fR = new Vector3f(minX, minY, minZ);
                bL = new Vector3f(maxX, minY, maxZ);
                bR = new Vector3f(maxX, minY, minZ);
            }
        }
        tL = new Vector3f(fL);
        tR = new Vector3f(fR);
        tL.setY(maxY);
        tR.setY(maxY);
        Mesh mesh = new Mesh(new Vertex[] {
                //Front face
                new Vertex(fL, new Vector3f(0, 0, 1), new Vector2f(0.0f, 0.0f)),
                new Vertex(tL, new Vector3f(0, 0, 1), new Vector2f(0.0f, 1.0f)),
                new Vertex(tR, new Vector3f(0, 0, 1), new Vector2f(1.0f, 1.0f)),
                new Vertex(fR, new Vector3f(0, 0, 1), new Vector2f(1.0f, 0.0f)),

                //Right face
                new Vertex(fR, new Vector3f(1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(tR, new Vector3f(1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(bR, new Vector3f(1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Left face
                new Vertex(fL, new Vector3f(-1, 0, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(tL, new Vector3f(-1, 0, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(bL, new Vector3f(-1, 0, 0), new Vector2f(1.0f, 0.0f)),

                //Top face
                new Vertex(bL, new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(tL, new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(tR, new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(bR, new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f)),

                //Bottom face
                new Vertex(bL, new Vector3f(0, -1, 0), new Vector2f(0.0f, 0.0f)),
                new Vertex(fL, new Vector3f(0, -1, 0), new Vector2f(0.0f, 1.0f)),
                new Vertex(fR, new Vector3f(0, -1, 0), new Vector2f(1.0f, 1.0f)),
                new Vertex(bR, new Vector3f(0, -1, 0), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
                //Front face
                0, 1, 3,
                3, 1, 2,

                //Right face
                4, 5, 6,

                //Left face
                7, 8, 9,

                //Top face
                10, 11, 13,
                13, 11, 12,

                //Bottom face
                14, 15, 17,
                17, 15, 16,
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


                vertices[i] = new Vertex(new Vector3f((0.0f + x),  Scene.getHeightMap()[x][z + 1],  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 0.0f));
                vertices[i + 1] = new Vertex(new Vector3f((0.0f + x),  Scene.getHeightMap()[x][z],  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(0.0f, 1.0f));
                vertices[i + 2] = new Vertex(new Vector3f((1.0f + x),  Scene.getHeightMap()[x + 1][z],  (0.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 1.0f));
                vertices[i + 3] = new Vertex(new Vector3f((1.0f + x),  Scene.getHeightMap()[x + 1][z + 1],  (1.0f + z)), new Vector3f(0, 1, 0), new Vector2f(1.0f, 0.0f));
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

}
