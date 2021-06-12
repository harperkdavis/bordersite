package engine.graphics.mesh;

import engine.graphics.Material;
import engine.graphics.vertex.Vertex;
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

    private int vao, pbo, ibo, tbo, bbo, nbo;

    public Vertex[] vertices;
    protected int[] indices;
    protected Material material;

    private FloatBuffer positionBuffer, textureBuffer, normalBuffer, tangentBuffer, bitangentBuffer;
    private IntBuffer indicesBuffer;

    public Mesh(Vertex[] vertices, int[] indices, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.material = material;
        setTangents();
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
            normalData[i * 3] = vertices[i].getNormal().getX();
            normalData[i * 3 + 1] = vertices[i].getNormal().getY();
            normalData[i * 3 + 2] = vertices[i].getNormal().getZ();
        }

        normalBuffer.put(normalData).flip();
        nbo = storeData(normalBuffer, 2, 3);

        // NORMAL TANGENTS
        tangentBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
        bitangentBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);

        float[] tangentData = new float[vertices.length * 3];
        float[] bitangentData = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            if (!vertices[i].getNormal().equals(Vector3f.zero()) && vertices[i].getTangent().equals(Vector3f.zero())) {
                System.err.println("[ERROR] Tangent data not found in vertex!");
                setTangents();
            }

            tangentData[i * 3] = vertices[i].getTangent().getX();
            tangentData[i * 3 + 1] = vertices[i].getTangent().getY();
            tangentData[i * 3 + 2] = vertices[i].getTangent().getZ();

            bitangentData[i * 3] = vertices[i].getBitangent().getX();
            bitangentData[i * 3 + 1] = vertices[i].getBitangent().getY();
            bitangentData[i * 3 + 2] = vertices[i].getBitangent().getZ();
        }
        tangentBuffer.put(tangentData).flip();
        tbo = storeData(tangentBuffer, 3, 3);
        bitangentBuffer.put(bitangentData).flip();
        bbo = storeData(bitangentBuffer, 4, 3);

        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();

        ibo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    public void setTangents() {
        for (int i = 0; i < indices.length; i += 3) {

            if (vertices[indices[i]].getNormal().equals(Vector3f.zero())) {
                vertices[indices[i]].setTangent(Vector3f.zero());
                vertices[indices[i + 1]].setTangent(Vector3f.zero());
                vertices[indices[i + 2]].setTangent(Vector3f.zero());
                continue;
            }

            Vector3f v0 = vertices[indices[i]].getPosition();
            Vector3f v1 = vertices[indices[i + 1]].getPosition();
            Vector3f v2 = vertices[indices[i + 2]].getPosition();

            Vector2f uv0 = vertices[indices[i]].getUV();
            Vector2f uv1 = vertices[indices[i + 1]].getUV();
            Vector2f uv2 = vertices[indices[i + 2]].getUV();

            Vector3f e1 = Vector3f.subtract(v1, v0);
            Vector3f e2 = Vector3f.subtract(v2, v0);

            Vector2f deltaUV1 = Vector2f.subtract(uv1, uv0);
            Vector2f deltaUV2 = Vector2f.subtract(uv2, uv0);

            float r = 1.0f / (deltaUV1.getX() * deltaUV2.getY() - deltaUV2.getX() * deltaUV1.getY());

            Vector3f tangent = new Vector3f(
                    r * (deltaUV2.getY() * e1.getX() - deltaUV1.getY() * e2.getX()),
                    r * (deltaUV2.getY() * e1.getY() - deltaUV1.getY() * e2.getY()),
                    r * (deltaUV2.getY() * e1.getZ() - deltaUV1.getY() * e2.getZ())
            );

            Vector3f bitangent = new Vector3f(
                    r * (-deltaUV2.getX() * e1.getX() + deltaUV1.getX() * e2.getX()),
                    r * (-deltaUV2.getX() * e1.getY() + deltaUV1.getX() * e2.getY()),
                    r * (-deltaUV2.getX() * e1.getZ() + deltaUV1.getX() * e2.getZ())
            );

            vertices[indices[i]].setTangent(tangent);
            vertices[indices[i + 1]].setTangent(tangent);
            vertices[indices[i + 2]].setTangent(tangent);

            vertices[indices[i]].setBitangent(bitangent);
            vertices[indices[i + 1]].setBitangent(bitangent);
            vertices[indices[i + 2]].setBitangent(bitangent);
        }
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
        GL15.glDeleteBuffers(ibo);
        GL15.glDeleteBuffers(tbo);
        GL15.glDeleteBuffers(nbo);
        GL15.glDeleteBuffers(tbo);
        GL15.glDeleteBuffers(bbo);

        GL30.glDeleteVertexArrays(vao);

        MemoryUtil.memFree(positionBuffer);
        MemoryUtil.memFree(textureBuffer);
        MemoryUtil.memFree(normalBuffer);
        MemoryUtil.memFree(indicesBuffer);
        MemoryUtil.memFree(tangentBuffer);
        MemoryUtil.memFree(bitangentBuffer);
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

    public int getTBO() {
        return tbo;
    }

    public int getIBO() {
        return ibo;
    }

}
