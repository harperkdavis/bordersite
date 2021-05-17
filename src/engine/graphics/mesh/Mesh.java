package engine.graphics.mesh;

import engine.graphics.Material;
import engine.graphics.vertex.Vertex;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    private int vao, pbo, ibo, cbo, tbo, nbo;

    public Vertex[] vertices;
    protected int[] indices;
    protected Material material;

    private FloatBuffer positionBuffer, textureBuffer, normalBuffer;
    private IntBuffer indicesBuffer;

    public Mesh(Vertex[] vertices, int[] indices, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.material = material;
    }

    public void create() {

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        // POSITION
        positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);

        float[] positionData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            positionData[i * 3] = vertices[i].getPosition().getX();
            positionData[i * 3 + 1] = vertices[i].getPosition().getY();
            positionData[i * 3 + 2] = vertices[i].getPosition().getZ();
        }

        positionBuffer.put(positionData).flip();
        pbo = storeData(positionBuffer, 0, 3);

        // UV
        textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);

        float[] textureData = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            textureData[i * 2] = vertices[i].getUV().x;
            textureData[i * 2 + 1] = vertices[i].getUV().y;
        }

        textureBuffer.put(textureData).flip();
        tbo = storeData(textureBuffer, 1, 2);

        // NORMALS
        normalBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);

        float[] normalData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            normalData[i * 3] = ((Vertex) vertices[i]).getNormal().getX();
            normalData[i * 3 + 1] = ((Vertex) vertices[i]).getNormal().getY();
            normalData[i * 3 + 2] = ((Vertex) vertices[i]).getNormal().getZ();
        }

        normalBuffer.put(normalData).flip();
        nbo = storeData(normalBuffer, 2, 3);

        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();

        ibo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    public void updateMesh(Mesh mesh) {
        destroy();

        this.vertices = mesh.vertices;
        this.indices = mesh.indices;

        create();
    }

    public void destroy() {
        this.vertices = null;
        this.indices = null;

        GL15.glDeleteBuffers(pbo);
        GL15.glDeleteBuffers(cbo);
        GL15.glDeleteBuffers(ibo);
        GL15.glDeleteBuffers(tbo);
        GL15.glDeleteBuffers(nbo);

        GL30.glDeleteVertexArrays(vao);

        MemoryUtil.memFree(positionBuffer);
        MemoryUtil.memFree(textureBuffer);
        MemoryUtil.memFree(normalBuffer);
        MemoryUtil.memFree(indicesBuffer);

    }

    // Store data in buffer
    protected int storeData(FloatBuffer buffer, int index, int size) {
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return bufferID;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
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

}
