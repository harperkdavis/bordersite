package engine.graphics.render;

import engine.graphics.Shader;
import engine.graphics.light.DirectionalLight;
import engine.graphics.light.Fog;
import engine.graphics.light.PointLight;
import engine.graphics.light.SpotLight;
import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Transform;
import engine.math.Vector3f;
import engine.math.Vector4f;
import engine.objects.Camera;
import engine.objects.GameObject;
import engine.objects.GameObjectMesh;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class MainRenderer extends Renderer {

    private Vector3f ambientLight;
    private DirectionalLight directionalLight;
    private Fog fog;
    private float specularPower = 10f;

    private static final int MAX_POINT_LIGHTS = 16;
    private static final int MAX_SPOT_LIGHTS = 16;

    private PointLight[] pointLights = new PointLight[MAX_POINT_LIGHTS];
    private SpotLight[] spotLights = new SpotLight[MAX_SPOT_LIGHTS];

    public MainRenderer(Shader shader) {
        super(shader);
        Renderer.setMain(this);

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0.5f, 0.7f, 0.2f), 1.0f);
        fog = new Fog(true, new Vector3f(0.6f, 0.6f, 0.6f),0.002f);

        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            pointLights[i] = PointLight.IDENTITY;
        }
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            spotLights[i] = SpotLight.IDENTITY;
        }

        pointLights[0] = new PointLight(new Vector3f(1.0f, 0.1f, 0.1f), new Vector3f(1, 1,1), 8.0f, new PointLight.Attenuation(0, 0,2));
        pointLights[1] = new PointLight(new Vector3f(0.1f, 0.1f, 1.0f), new Vector3f(2, 1,2), 8.0f, new PointLight.Attenuation(0, 0,2));
    }

    public void renderPrep() {
        shader.bind(); // SHADER BOUND

        Matrix4f view = Matrix4f.view(Camera.getMainCameraPosition(), Camera.getMainCameraRotation(), true);

        shader.setUniform("view", view);
        shader.setUniform("projection", Window.getProjectionMatrix());

        shader.setUniform("cameraPos", Camera.getMainCameraPosition());
        shader.setUniform("fog", fog);

        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("directionalLight", directionalLight);

        shader.setUniform("pointLights", pointLights);
        shader.setUniform("pointLights", spotLights);
    }

    public void renderCleanup() {
        shader.unbind(); // SHADER UNBOUND
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
        if (object instanceof GameObjectMesh) {
            GameObjectMesh objectMesh = (GameObjectMesh) object;

            GL30.glBindVertexArray(objectMesh.getMesh().getVAO());
            GL30.glEnableVertexAttribArray(0);
            GL30.glEnableVertexAttribArray(1);
            GL30.glEnableVertexAttribArray(2);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, objectMesh.getMesh().getIBO());

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL13.glBindTexture(GL11.GL_TEXTURE_2D, objectMesh.getMesh().getMaterial().getTextureID());

            shader.setUniform("model", Matrix4f.transform(objectMesh.getPosition(), objectMesh.getRotation(), objectMesh.getScale()));
            shader.setUniform("reflectance", ((GameObjectMesh) object).getMesh().getMaterial().getReflectance());
            shader.setUniform("meshColor", ((GameObjectMesh) object).getColor());
            shader.setUniform("specularPower", specularPower);

            GL11.glDrawElements(GL11.GL_TRIANGLES, objectMesh.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL30.glDisableVertexAttribArray(0);
            GL30.glDisableVertexAttribArray(1);
            GL30.glDisableVertexAttribArray(2);
            GL30.glBindVertexArray(0);
        }
    }

}
