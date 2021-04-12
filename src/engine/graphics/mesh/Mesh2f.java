package engine.graphics.mesh;

import engine.graphics.Material;
import engine.graphics.vertex.Vertex2f;
import engine.graphics.vertex.Vertex3f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh2f extends VertexGroup {

    private int vao, pbo, ibo, cbo, tbo;

    public Mesh2f(Vertex2f[] vertices, int[] indices, Material material) {
        super(vertices, indices, material);
    }

    @Override
    protected void createMesh() {

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

        // UV
        FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);

        float[] textureData = new float[vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            textureData[i * 2] = vertices[i].getUV().x;
            textureData[i * 2 + 1] = vertices[i].getUV().y;
        }

        textureBuffer.put(textureData).flip();
        tbo = storeData(textureBuffer, 1, 2);

        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();

        ibo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    @Override
    public void updateMesh(VertexGroup mesh) {
        if (mesh instanceof Mesh2f) {
            deleteBuffers();
            this.vertices = mesh.vertices;
            this.indices = mesh.indices;

            createMesh();
        } else {
            throw new IllegalArgumentException("Wrong mesh type");
        }
    }

    @Override
    public void deleteBuffers() {
        GL15.glDeleteBuffers(pbo);
        GL15.glDeleteBuffers(cbo);
        GL15.glDeleteBuffers(ibo);
        GL15.glDeleteBuffers(tbo);

        GL30.glDeleteVertexArrays(vao);
    }

    @Override
    public Vertex3f[] getVertices() {
        return (Vertex3f[]) vertices;
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
