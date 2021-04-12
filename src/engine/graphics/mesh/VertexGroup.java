package engine.graphics.mesh;

import engine.graphics.Material;
import engine.graphics.vertex.Vertex;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public abstract class VertexGroup {

    public Vertex[] vertices;
    protected int[] indices;
    protected Material material;

    public VertexGroup(Vertex[] vertices, int[] indices, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.material = material;
    }

    // Create the buffers to store mesh data
    public void create() {
        material.create();
        createMesh();
    }

    protected abstract void createMesh();

    protected abstract void updateMesh(VertexGroup mesh);

    // Store data in buffer
    protected int storeData(FloatBuffer buffer, int index, int size) {
        int bufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return bufferID;
    }

    protected abstract void deleteBuffers();

    // Destroy buffers
    public void destroy() {
        deleteBuffers();

        material.destroy();
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public Material getMaterial() {
        return material;
    }

}
