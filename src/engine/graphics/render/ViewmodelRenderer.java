package engine.graphics.render;

import engine.graphics.Shader;
import engine.graphics.light.DirectionalLight;
import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.objects.GameObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class ViewmodelRenderer extends Renderer {

    private final Vector3f ambientLight;
    private final DirectionalLight directionalLight;

    public ViewmodelRenderer(Shader shader) {
        super(shader);

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0.5f, 0.7f, 0.2f), 1.0f);
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
        }

        GL30.glBindVertexArray(object.getMesh().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());

        shader.bind(); // SHADER BOUND

        shader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale()));
        shader.setUniform("view", Matrix4f.view(Vector3f.zero(), Vector3f.zero(), false));
        shader.setUniform("projection", Window.getProjectionMatrix());

        shader.setUniform("cameraPos", new Vector3f(0, 0,0));

        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("directionalLight", directionalLight);

        shader.setUniform("meshColor", object.getColor());
        float specularPower = 10f;
        shader.setUniform("specularPower", specularPower);

        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

        shader.unbind(); // SHADER UNBOUND


        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

    }
}
