package engine.graphics;

import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjglx.Sys;

import java.util.Arrays;

public class Renderer {
    private Shader shader;
    private Window window;
    private boolean ortho;

    public Renderer(Window window, Shader shader, boolean ortho) {
        this.shader = shader;
        this.window = window;
        this.ortho = ortho;
    }

    public void renderMesh(GameObject object, Camera camera) {
        GL30.glBindVertexArray(object.getMesh().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, object.getMesh().getMaterial().getTextureID());

        Matrix4f model = Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale());
        shader.bind();
        if (ortho) {
            shader.setUniform("view", Matrix4f.view(Vector3f.zero(), new Vector3f(0, 180, 180)));
            shader.setUniform("projection", window.getOrthoMatrix());
        } else {
            shader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
            shader.setUniform("projection", window.getProjectionMatrix());
        }
        shader.setUniform("model", model);
        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);
        shader.unbind();

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

    }

}
