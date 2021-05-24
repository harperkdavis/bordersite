package engine.graphics.render;

import engine.graphics.Material;
import engine.graphics.Shader;
import engine.graphics.light.DirectionalLight;
import engine.graphics.light.Fog;
import engine.graphics.light.PointLight;
import engine.graphics.light.SpotLight;
import engine.graphics.mesh.Mesh;
import engine.graphics.vertex.Vertex;
import engine.io.Input;
import engine.io.Window;
import engine.math.Mathf;
import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;
import game.scene.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;

import java.util.List;

public class MainRenderer extends Renderer {

    private final Vector3f ambientLight;
    private final DirectionalLight directionalLight;
    private final Fog fog;

    private static final int MAX_POINT_LIGHTS = 16;
    private static float exposure = 0.2f;

    private final PointLight[] pointLights = new PointLight[MAX_POINT_LIGHTS];

    private Shader gShader, depthShader, ssaoShader, ssaoBlurShader, postShader;
    protected int depthMapFramebuffer, depthMapTexture;

    private int gBuffer, rboDepth;
    private int gPosition, gNormal, gAlbedoSpec;

    private int ssaoColorBuffer, ssaoBlurBuffer, ssaoNoiseTexture;
    private int ssaoFBO, ssaoBlurFBO;

    private int hdrFBO;
    private int hdrBuffer, hdrBrightBuffer;

    private Vector3f[] ssaoKernel = new Vector3f[64];
    private float[] ssaoNoise = new float[48];

    private Mesh renderQuad;

    private static final int SHADOW_MAP_WIDTH = 32768, SHADOW_MAP_HEIGHT = 32768;


    public MainRenderer(Shader gShader, Shader ssaoShader, Shader ssaoBlurShader, Shader shader, Shader depthShader, Shader postShader) {
        super(shader);
        Renderer.setMain(this);

        this.gShader = gShader;
        this.depthShader = depthShader;
        this.ssaoShader = ssaoShader;
        this.ssaoBlurShader = ssaoBlurShader;
        this.postShader = postShader;

        ambientLight = new Vector3f(0.5f, 0.5f, 0.5f);
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0.8f, 1.0f, 0.4f).normalize(), 1.2f);
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

        int[] gbuffers = new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_COLOR_ATTACHMENT2};
        IntBuffer drawBuffers = BufferUtils.createIntBuffer(gbuffers.length);
        for(int elem : gbuffers) {
            drawBuffers.put(elem);
        }
        drawBuffers.flip();

        GL20.glDrawBuffers(drawBuffers);

        rboDepth = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboDepth);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, Window.getWidth(), Window.getHeight());
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, rboDepth);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("[ERROR] Deffered framebuffer not complete!");
        }


        // SSAO


        ssaoFBO = GL30.glGenFramebuffers();
        ssaoBlurFBO = GL30.glGenFramebuffers();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoFBO);
        ssaoColorBuffer = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ssaoColorBuffer);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, ssaoColorBuffer, 0);
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("[ERROR] SSAO Framebuffer not complete!");
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoBlurFBO);
        ssaoBlurBuffer = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ssaoBlurBuffer);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, ssaoBlurBuffer, 0);
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("[ERROR] SSAO Blur Framebuffer not complete!");
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        Random randomSample = new Random();

        for (int i = 0; i < 64; i++) {
            Vector3f sample = new Vector3f(randomSample.nextFloat() * 2.0f - 1.0f, randomSample.nextFloat() * 2.0f - 1.0f, randomSample.nextFloat()).normalize();
            sample.multiply(randomSample.nextFloat());

            float scale = Mathf.lerp(0.1f, 1.0f, (i / 64.0f) * (i / 64.0f));
            sample.multiply(scale);
            ssaoKernel[i] = sample;
        }

        for (int i = 0; i < 16; i++) {
            ssaoNoise[i * 3] = randomSample.nextFloat() * 2.0f - 1.0f;
            ssaoNoise[i * 3 + 1] = randomSample.nextFloat() * 2.0f - 1.0f;
            ssaoNoise[i * 3 + 2] = 0;
        }

        ssaoNoiseTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ssaoNoiseTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB32F, 4, 4, 0, GL11.GL_RGB, GL11.GL_FLOAT, ssaoNoise);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

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

        // HDR AND POST PROCESSING

        hdrFBO = GL30.glGenFramebuffers();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, hdrFBO);

        hdrBuffer = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, hdrBuffer);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL15.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL15.GL_CLAMP_TO_EDGE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, hdrBuffer, 0);

        hdrBrightBuffer = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, hdrBrightBuffer);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL15.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL15.GL_CLAMP_TO_EDGE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, hdrBrightBuffer, 0);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("[ERROR] HDR Framebuffer not complete!");
        }

        int[] hdrbuffers = new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1};
        IntBuffer hdrDrawBuffers = BufferUtils.createIntBuffer(hdrbuffers.length);
        for(int elem : hdrbuffers) {
            hdrDrawBuffers.put(elem);
        }
        hdrDrawBuffers.flip();

        GL20.glDrawBuffers(hdrDrawBuffers);

    }

    public void renderScene() {

        if (Input.isKey(GLFW.GLFW_KEY_P)) {
            exposure += 0.01f;
        } else if (Input.isKey(GLFW.GLFW_KEY_L)) {
            exposure -= 0.01f;
        }

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


        // 2: SSAO TEXTURE


        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoFBO);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        ssaoShader.bind();
        for (int i = 0; i < 64; i++) {
            ssaoShader.setUniform("samples[" + i + "]", ssaoKernel[i]);
        }

        ssaoShader.setUniform("gPosition", 0);
        ssaoShader.setUniform("gNormal", 1);
        ssaoShader.setUniform("ssaoNoise", 2);

        ssaoShader.setUniform("noiseScale", new Vector2f(Window.getWidth() / 4.0f, Window.getHeight() / 4.0f));
        ssaoShader.setUniform("view", view);
        ssaoShader.setUniform("projection", projection);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, gPosition);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, gNormal);
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, ssaoNoiseTexture);
        renderQuad();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        ssaoShader.unbind();

        // 2.5: SSAO BLUR

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoBlurFBO);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        ssaoBlurShader.bind();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, ssaoColorBuffer);
        renderQuad();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        ssaoBlurShader.unbind();


        // 3: LIGHTING


        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, hdrFBO);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        shader.bind();

        shader.setUniform("gPosition", 0);
        shader.setUniform("gNormal", 1);
        shader.setUniform("gAlbedoSpec", 2);
        shader.setUniform("ssao", 3);
        shader.setUniform("shadowMap", 4);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, gPosition);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, gNormal);
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, gAlbedoSpec);
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, ssaoBlurBuffer);
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, depthMapTexture);

        shader.setUniform("lightSpaceMatrix", getLightSpaceMatrix());

        shader.setUniform("cameraPos", Camera.getMainCameraPosition());
        shader.setUniform("fog", fog);

        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("directionalLight", directionalLight);

        shader.setUniform("pointLights", pointLights);

        renderQuad();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        shader.unbind();

        // 4: HDR

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        postShader.bind();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, hdrBuffer);

        postShader.setUniform("hdrBuffer", 0);
        postShader.setUniform("exposure", exposure);

        renderQuad();
        postShader.unbind();
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
