package engine.graphics;

import engine.math.Vector2f;
import engine.math.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
    private Vertex[] vertices;
    private int[] indices;
    private Material material;
    private int vao, pbo, ibo, cbo, tbo;

    public Mesh(Vertex[] vertices, int[] indices, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.material = material;
    }

    public void create() {
        material.create();

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        // POSITION
        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);

        float[] positionData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            positionData[i * 3] = vertices[i].getPosition().getX();
            positionData[i * 3 + 1] = vertices[i].getPosition().getY();
            positionData[i * 3 + 2] = vertices[i].getPosition().getZ();
        }

        positionBuffer.put(positionData).flip();
        pbo = storeData(positionBuffer, 0, 3);

        // COLOR
        FloatBuffer colorBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);

        float[] colorData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            colorData[i * 3] = vertices[i].getColor().getX();
            colorData[i * 3 + 1] = vertices[i].getColor().getY();
            colorData[i * 3 + 2] = vertices[i].getColor().getZ();
        }

        colorBuffer.put(colorData).flip();
        cbo = storeData(colorBuffer, 1, 3);

        // UV
        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);

        float[] textureData = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            textureData[i * 2] = vertices[i].getUV().x;
            textureData[i * 2 + 1] = vertices[i].getUV().y;
        }

        textureBuffer.put(textureData).flip();
        tbo = storeData(textureBuffer, 2, 2);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();

        ibo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    private int storeData(FloatBuffer buffer, int index, int size) {
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return bufferID;
    }

    public void destroy() {
        GL15.glDeleteBuffers(pbo);
        GL15.glDeleteBuffers(cbo);
        GL15.glDeleteBuffers(ibo);
        GL15.glDeleteBuffers(tbo);

        GL30.glDeleteVertexArrays(vao);

        material.destroy();
    }

    public static Mesh Cube(float size, Material m) {
        Mesh mesh = new Mesh(new Vertex[] {
                //Back face
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size, -0.5f * size), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size, -0.5f * size), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size, -0.5f * size), new Vector2f(1.0f, 0.0f)),

                //Front face
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size,  0.5f * size), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size,  0.5f * size), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size,  0.5f * size), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size,  0.5f * size), new Vector2f(1.0f, 0.0f)),

                //Right face
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size, -0.5f * size), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size, -0.5f * size), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size,  0.5f * size), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size,  0.5f * size), new Vector2f(1.0f, 0.0f)),

                //Left face
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size, -0.5f * size), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size,  0.5f * size), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size,  0.5f * size), new Vector2f(1.0f, 0.0f)),

                //Top face
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size,  0.5f * size), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size,  0.5f * size, -0.5f * size), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size, -0.5f * size), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size,  0.5f * size,  0.5f * size), new Vector2f(1.0f, 0.0f)),

                //Bottom face
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size,  0.5f * size), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size, -0.5f * size), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f( 0.5f * size, -0.5f * size,  0.5f * size), new Vector2f(1.0f, 0.0f)),
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

    public Vertex[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getVAO() {
        return vao;
    }

    public int getPBO() {
        return pbo;
    }

    public int getCBO() {
        return cbo;
    }

    public int getTBO() {
        return cbo;
    }

    public int getIBO() {
        return ibo;
    }

    public Material getMaterial() {
        return material;
    }

}
