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

    public Vertex[] vertices;
    public int[] indices;
    public Material material;
    private int vao, pbo, ibo, cbo, tbo, nbo;
    private boolean linear;

    public Mesh(Vertex[] vertices, int[] indices, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.material = material;
        linear = false;
    }

    public Mesh(Vertex[] vertices, int[] indices, Material material, boolean linear) {
        this.vertices = vertices;
        this.indices = indices;
        this.material = material;
        this.linear = linear;
    }

    // Create the buffers to store mesh data
    public void create() {
        material.create(linear);
        createMesh();
    }

    private void createMesh() {

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

        // NORMALS
        FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);

        float[] normalData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            normalData[i * 3] = vertices[i].getNormal().getX();
            normalData[i * 3 + 1] = vertices[i].getNormal().getY();
            normalData[i * 3 + 2] = vertices[i].getNormal().getZ();
        }

        normalBuffer.put(normalData).flip();
        nbo = storeData(normalBuffer, 3, 3);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();

        ibo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    // Store data in buffer
    private int storeData(FloatBuffer buffer, int index, int size) {
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return bufferID;
    }

    // Destroy buffers
    public void destroy() {
        GL15.glDeleteBuffers(pbo);
        GL15.glDeleteBuffers(cbo);
        GL15.glDeleteBuffers(ibo);
        GL15.glDeleteBuffers(tbo);
        GL15.glDeleteBuffers(nbo);

        GL30.glDeleteVertexArrays(vao);

        material.destroy();
    }

    public void refresh() {
        create();
        destroy();
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
