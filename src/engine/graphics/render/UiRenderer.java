package engine.graphics.render;

import engine.graphics.Shader;
import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.objects.*;
import game.ui.UiObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class UiRenderer extends Renderer {

    public UiRenderer(Shader shader) {
        super(shader);
        setUi(this);
    }

    @Override
    public void render(GameObject object) {
        if (!object.isVisible()) {
            return;
        }
        if (object.hasChildren()) {
            for (GameObject go : object.getChildren()) {
                render(go);
            }
            render(object);
        }
        if (object instanceof UiObject) {
            UiObject objectMesh = (UiObject) object;
            render(objectMesh);
        }
    }

    private void render(UiObject objectMesh) {

        GL30.glBindVertexArray(objectMesh.getMesh().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, objectMesh.getMesh().getIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, objectMesh.getMesh().getMaterial().getTextureID());

        shader.bind(); // SHADER BOUND

        shader.setUniform("model", Matrix4f.transform(objectMesh.getPosition(), objectMesh.getRotation(), objectMesh.getScale()));
        shader.setUniform("view", Matrix4f.view(Vector3f.zero(), new Vector3f(0, 180, 180)));
        shader.setUniform("projection", Window.getOrthographicMatrix());

        shader.setUniform("meshColor", objectMesh.getColor());

        GL11.glDrawElements(GL11.GL_TRIANGLES, objectMesh.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.unbind(); // SHADER UNBOUND

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

}
