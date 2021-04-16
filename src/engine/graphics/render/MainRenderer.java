package engine.graphics.render;

import engine.graphics.Shader;
import engine.graphics.light.DirectionalLight;
import engine.graphics.light.Fog;
import engine.graphics.light.PointLight;
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

public class MainRenderer extends Renderer {

    private Vector3f ambientLight;
    private DirectionalLight directionalLight;
    private PointLight pointLight;
    private Fog fog;
    private float specularPower = 10f;

    public MainRenderer(Shader shader) {
        super(shader);
        Renderer.setMain(this);

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        float lightIntensity = 1000.0f;
        pointLight = new PointLight(lightColor, new Vector3f(1, 1, 1), lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);

        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0.5f, 0.7f, 0.2f), 1.0f);
        fog = new Fog(true, new Vector3f(0.6f, 0.6f, 0.6f),0.01f);
    }

    @Override
    public void render(GameObject object) {
        if (!object.isVisible()) {
            return;
        }
        if (object instanceof GameObjectGroup) {
            for (GameObject go : ((GameObjectGroup) object).getChildren()) {
                render(go);
            }
        }
        if (object instanceof GameObjectMesh) {
            GameObjectMesh objectMesh = (GameObjectMesh) object;

            GL30.glBindVertexArray(objectMesh.getMesh().getVAO());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, objectMesh.getMesh().getIBO());

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL13.glBindTexture(GL11.GL_TEXTURE_2D, objectMesh.getMesh().getMaterial().getTextureID());

            shader.bind(); // SHADER BOUND

            shader.setUniform("model", Matrix4f.transform(objectMesh.getPosition(), objectMesh.getRotation(), objectMesh.getScale()));
            shader.setUniform("view", Matrix4f.view(Camera.getMainCameraPosition(), Camera.getMainCameraRotation(), true));
            shader.setUniform("projection", Window.getProjectionMatrix());

            shader.setUniform("cameraPos", Camera.getMainCameraPosition());
            shader.setUniform("fog", fog);

            shader.setUniform("ambientLight", ambientLight);
            shader.setUniform("pointLight", pointLight);
            shader.setUniform("directionalLight", directionalLight);

            shader.setUniform("reflectance", ((GameObjectMesh) object).getMesh().getMaterial().getReflectance());
            shader.setUniform("meshColor", ((GameObjectMesh) object).getColor());
            shader.setUniform("specularPower", specularPower);

            GL11.glDrawElements(GL11.GL_TRIANGLES, objectMesh.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

            shader.unbind(); // SHADER UNBOUND

            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
        }
    }

}
