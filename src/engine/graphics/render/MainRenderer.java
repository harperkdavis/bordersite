package engine.graphics.render;

import engine.graphics.Material;
import engine.graphics.Shader;
import engine.graphics.light.DirectionalLight;
import engine.graphics.light.Fog;
import engine.graphics.light.PointLight;
import engine.graphics.light.SpotLight;
import engine.graphics.mesh.Mesh;
import engine.graphics.vertex.Vertex;
import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import game.scene.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class MainRenderer extends Renderer {

    private final Vector3f ambientLight;
    private final DirectionalLight directionalLight;
    private final Fog fog;

    private static final int MAX_POINT_LIGHTS = 16;

    private final PointLight[] pointLights = new PointLight[MAX_POINT_LIGHTS];

    private Shader gShader, depthShader;
    protected int depthMapFramebuffer, depthMapTexture;

    private int gBuffer, rboDepth;
    private int gPosition, gNormal, gAlbedoSpec;

    private Mesh renderQuad;

    private static final int SHADOW_MAP_WIDTH = 32768, SHADOW_MAP_HEIGHT = 32768;


    public MainRenderer(Shader gShader, Shader shader, Shader depthShader) {
        super(shader);
        Renderer.setMain(this);

        this.gShader = gShader;
        this.depthShader = depthShader;

        ambientLight = new Vector3f(0.5f, 0.5f, 0.5f);
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0.8f, 1.0f, 0.4f).normalize(), 1.0f);
        fog = new Fog(true, new Vector3f(0.6f, 0.6f, 0.6f),0.002f);

        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            pointLights[i] = PointLight.IDENTITY;
        }

    }

    public void createBuffers() {
        // Deffered Framebuffer

        gBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer);

        gPosition = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, gPosition);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, gPosition, 0);

        gNormal = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, gNormal);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, gNormal, 0);

        gAlbedoSpec = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, gAlbedoSpec);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL11.GL_TEXTURE_2D, gAlbedoSpec, 0);

        int[] buffers = new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_COLOR_ATTACHMENT2};
        IntBuffer drawBuffers = BufferUtils.createIntBuffer(buffers.length);
        for(int elem : buffers) {
            drawBuffers.put(elem);
        }
        drawBuffers.flip();

        GL20.glDrawBuffers(drawBuffers);

        rboDepth = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboDepth);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, Window.getWidth(), Window.getHeight());
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, rboDepth);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("[ERROR] Deffered framebuffer error");
        }

        // Render Quad

        renderQuad = new Mesh(new Vertex[]{
                new Vertex(new Vector3f(-1.0f, -1.0f, 0.0f), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-1.0f, 1.0f, 0.0f), new Vector2f(0.0f, 1.0f)),
                new Vertex(new Vector3f(1.0f, 1.0f, 0.0f), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(1.0f, -1.0f, 0.0f), new Vector2f(1.0f, 0.0f)),
        }, new int[] {
            0, 1, 3,
            3, 1, 2,
        }, null);
        renderQuad.create();

        // Depth Framebuffer

        depthMapFramebuffer = GL30.glGenFramebuffers();
        depthMapTexture = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthMapTexture);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFramebuffer);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_CLAMP_TO_EDGE);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthMapTexture, 0);

        GL30.glDrawBuffer(GL11.GL_NONE);
        GL30.glReadBuffer(GL11.GL_NONE);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("[ERROR] Depth framebuffer error");
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

    }

    public void renderScene() {

        // 1: GEOMETRY

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        Matrix4f view = Matrix4f.view(Camera.getMainCameraPosition(), Camera.getMainCameraRotation(), true);
        Matrix4f projection = Window.getProjectionMatrix();

        gShader.bind();

        gShader.setUniform("view", view);
        gShader.setUniform("projection", projection);

        gShader.setUniform("diffuse_texture", 0);
        gShader.setUniform("specular_texture", 1);

        for (GameObject o : Scene.objects) {
            gRender(o);
        }

        gShader.unbind();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        renderWorldDepthMap();

        GL11.glViewport(0, 0, Window.getWidth(), Window.getHeight());

        // 2: LIGHTING

        shader.bind();

        shader.setUniform("gPosition", 0);
        shader.setUniform("gNormal", 1);
        shader.setUniform("gAlbedoSpec", 2);
        shader.setUniform("shadowMap", 3);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, gPosition);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, gNormal);
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, gAlbedoSpec);
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, depthMapTexture);

        shader.setUniform("lightSpaceMatrix", getLightSpaceMatrix());

        shader.setUniform("cameraPos", Camera.getMainCameraPosition());
        shader.setUniform("fog", fog);

        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("directionalLight", directionalLight);

        shader.setUniform("pointLights", pointLights);

        renderQuad();
        shader.unbind();

    }

    private void renderQuad() {
        GL30.glBindVertexArray(renderQuad.getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, renderQuad.getIBO());

        GL11.glDrawElements(GL11.GL_TRIANGLES, renderQuad.getIndices().length, GL11.GL_UNSIGNED_INT, 0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void render(GameObject object) {
    }

    private Matrix4f getLightSpaceMatrix() {
        Matrix4f lightOrtho = Matrix4f.ortho(-200, 200, -200, 200, 1.0f, 40.0f);
        Vector3f lightDir = new Vector3f(directionalLight.getDirection());
        Matrix4f lightView = Matrix4f.lookAt(new Vector3f(-lightDir.getX() * 4, lightDir.getY() * 4, -lightDir.getZ() * 4), Vector3f.zero(), Vector3f.oneY());
        return Matrix4f.multiply(lightView, lightOrtho);
    }

    private void renderWorldDepthMap() {

        depthShader.bind();
        GL11.glCullFace(GL11.GL_FRONT);

        depthShader.setUniform("lightSpaceMatrix", getLightSpaceMatrix());

        GL11.glViewport(0, 0, SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFramebuffer);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);


        for (GameObject object : Scene.objects) {
            renderMinimal(object);
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glCullFace(GL11.GL_BACK);
        depthShader.unbind();

    }

    private void gRender(GameObject object) {
        if (!object.isVisible()) {
            return;
        }
        if (object.hasChildren()) {
            for (GameObject go : object.getChildren()) {
                gRender(go);
            }
        }

        GL30.glBindVertexArray(object.getMesh().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, object.getMesh().getMaterial().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, Material.DEFAULT.getTextureID());

        gShader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale()));

        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void renderMinimal(GameObject object) {
        if (!object.isVisible()) {
            return;
        }
        if (object.hasChildren()) {
            for (GameObject go : object.getChildren()) {
                renderMinimal(go);
            }
        }

        GL30.glBindVertexArray(object.getMesh().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());

        depthShader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale()));

        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

}
