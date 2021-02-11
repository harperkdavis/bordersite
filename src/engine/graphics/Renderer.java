package engine.graphics;

import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import engine.objects.GameObjectGroup;
import engine.objects.GameObjectMesh;
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

    private static Renderer renderer;
    private static Renderer uiRenderer;

    public Renderer(Window window, Shader shader, boolean ortho) {
        this.shader = shader;
        this.window = window;
        this.ortho = ortho;
    }

    // Group renderer
    public void renderMesh(GameObjectGroup object, Camera camera) {
        for (GameObject go : object.getChildren()) {
            if (go instanceof GameObjectMesh || go instanceof GameObjectGroup) {
                renderMesh(go, camera);
            }
        }
    }

    // Single renderer
    public void renderMesh(GameObject object, Camera camera) {
        if (object instanceof GameObjectMesh) {
            GameObjectMesh objectMesh = (GameObjectMesh) object;
            GL30.glBindVertexArray(objectMesh.getMesh().getVAO());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL30.glEnableVertexAttribArray(3);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, objectMesh.getMesh().getIBO());

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL13.glBindTexture(GL11.GL_TEXTURE_2D, objectMesh.getMesh().getMaterial().getTextureID());

            Matrix4f model = Matrix4f.transform(objectMesh.getPosition(), objectMesh.getRotation(), objectMesh.getScale());
            shader.bind();
            if (ortho) {
                shader.setUniform("view", Matrix4f.view(Vector3f.zero(), new Vector3f(0, 180, 180)));
                shader.setUniform("projection", window.getOrthoMatrix());
            } else {
                shader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
                shader.setUniform("projection", window.getProjectionMatrix());
            }
            shader.setUniform("model", model);
            GL11.glDrawElements(GL11.GL_TRIANGLES, objectMesh.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);
            shader.unbind();

            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glDisableVertexAttribArray(2);
            GL30.glDisableVertexAttribArray(3);
            GL30.glBindVertexArray(0);
        }
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    public static void setRenderer(Renderer renderer) {
        Renderer.renderer = renderer;
    }

    public static Renderer getUiRenderer() {
        return uiRenderer;
    }

    public static void setUiRenderer(Renderer uiRenderer) {
        Renderer.uiRenderer = uiRenderer;
    }
}
