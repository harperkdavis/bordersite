package engine.graphics.render;

import engine.graphics.Shader;
import engine.graphics.light.DirectionalLight;
import engine.graphics.light.Fog;
import engine.graphics.light.PointLight;
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
import main.Global;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

public class MainRenderer extends Renderer {

    private final Vector3f ambientLight;
    private final Fog fog;

    private static float exposure = 1.8f;

    private final Shader gShader, depthShader, ssaoShader, ssaoBlurShader, blurShader, postShader, unlitShader;
    protected int depthMapFramebuffer, depthMapTexture;

    private int gBuffer, rboDepth;
    private int gPosition, gViewPosition, gNormal, gAlbedoSpec;

    private int ssaoColorBuffer, ssaoBlurBuffer, ssaoNoiseTexture;
    private int ssaoFBO, ssaoBlurFBO;

    private int hdrFBO, brightBlurFBO;
    private int hdrBuffer, hdrBrightBuffer, brightBlurBuffer;

    private final int SSAO_KERNEL_SIZE = 4;
    private final int MSAA_SAMPLES = 4;

    private Vector3f[] ssaoKernel = new Vector3f[SSAO_KERNEL_SIZE];
    private float[] ssaoNoise = new float[48];

    private Mesh renderQuad;

    private static final int SHADOW_MAP_WIDTH = 8192, SHADOW_MAP_HEIGHT = 8192;


    public MainRenderer(Shader gShader, Shader ssaoShader, Shader ssaoBlurShader, Shader shader, Shader depthShader, Shader blurShader, Shader postShader, Shader unlitShader) {
        super(shader);
        Renderer.setMain(this);

        this.gShader = gShader;
        this.depthShader = depthShader;
        this.ssaoShader = ssaoShader;
        this.ssaoBlurShader = ssaoBlurShader;
        this.blurShader = blurShader;
        this.postShader = postShader;
        this.unlitShader = unlitShader;

        ambientLight = new Vector3f(0.2f, 0.2f, 0.205f);
        fog = new Fog(true, new Vector3f(0.6f, 0.6f, 0.6f),0.002f);

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

        gViewPosition = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, gViewPosition);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, gViewPosition, 0);

        gNormal = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, gNormal);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL11.GL_TEXTURE_2D, gNormal, 0);

        gAlbedoSpec = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, gAlbedoSpec);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT3, GL11.GL_TEXTURE_2D, gAlbedoSpec, 0);

        int[] gbuffers = new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_COLOR_ATTACHMENT2, GL30.GL_COLOR_ATTACHMENT3};
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

        for (int i = 0; i < SSAO_KERNEL_SIZE; i++) {
            Vector3f sample = new Vector3f(randomSample.nextFloat() * 2.0f - 1.0f, randomSample.nextFloat() * 2.0f - 1.0f, randomSample.nextFloat()).normalize();
            sample.multiply(randomSample.nextFloat());

            float scale = Mathf.lerp(0.1f, 1.0f, (i / (float) SSAO_KERNEL_SIZE) * (i / (float) SSAO_KERNEL_SIZE));
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

        brightBlurFBO = GL30.glGenFramebuffers();
        brightBlurBuffer = GL11.glGenTextures();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, brightBlurFBO);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, brightBlurBuffer);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, Window.getWidth(), Window.getHeight(), 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE); // we clamp to the edge as the blur filter would otherwise sample repeated texture values!
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, brightBlurBuffer, 0);

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("[ERROR] Ping pong buffer not complete!");
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void renderScene() {

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        if (Input.isKey(GLFW.GLFW_KEY_P)) {
            exposure += 0.01f;
        } else if (Input.isKey(GLFW.GLFW_KEY_L)) {
            exposure -= 0.01f;
        }

        // 1: GEOMETRY

        if (Global.RENDER_WIREFRAME) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        Matrix4f view = Matrix4f.view(Camera.getMainCameraPosition(), Camera.getMainCameraRotation(), true);
        Matrix4f projection = Window.getProjectionMatrix();

        gShader.bind();

        gShader.setUniform("view", view);
        gShader.setUniform("projection", projection);

        for (GameObject o : Scene.objects) {

            gShader.setUniform("texture_diffuse", 0);
            gShader.setUniform("texture_specular", 1);
            gShader.setUniform("texture_normal", 2);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL13.glBindTexture(GL11.GL_TEXTURE_2D, o.getMesh().getMaterial().getDiffuseID());
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL13.glBindTexture(GL11.GL_TEXTURE_2D, o.getMesh().getMaterial().getSpecularID());
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL13.glBindTexture(GL11.GL_TEXTURE_2D, o.getMesh().getMaterial().getNormalID());

            gRender(o);
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        gShader.unbind();

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        renderWorldDepthMap();

        GL11.glViewport(0, 0, Window.getWidth(), Window.getHeight());

        // 2: SSAO TEXTURE

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, ssaoFBO);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        ssaoShader.bind();
        for (int i = 0; i < SSAO_KERNEL_SIZE; i++) {
            ssaoShader.setUniform("samples[" + i + "]", ssaoKernel[i]);
        }

        ssaoShader.setUniform("gPosition", 0);
        ssaoShader.setUniform("gNormal", 1);
        ssaoShader.setUniform("ssaoNoise", 2);

        ssaoShader.setUniform("noiseScale", new Vector2f(Window.getWidth() / 4.0f, Window.getHeight() / 4.0f));
        ssaoShader.setUniform("view", view);
        ssaoShader.setUniform("projection", projection);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, gViewPosition);
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

        shader.setUniform("viewPos", Camera.getMainCameraPosition());
        shader.setUniform("fog", fog);
        shader.setUniform("view", view);

        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("directionalLight", Scene.directionalLight);

        shader.setUniform("pointLights", Scene.pointLights);

        renderQuad();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        shader.unbind();

        // 3.5: BLOOM BLUR

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        blurShader.bind();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, brightBlurFBO);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, hdrBrightBuffer);
        blurShader.setUniform("image", 0);

        renderQuad();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        blurShader.unbind();

        // 4: HDR

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        postShader.bind();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, hdrBuffer);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, brightBlurBuffer);

        postShader.setUniform("hdrBuffer", 0);
        postShader.setUniform("brightBuffer", 1);
        postShader.setUniform("exposure", exposure);

        renderQuad();
        postShader.unbind();

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, gBuffer);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL30.glBlitFramebuffer(0, 0, Window.getWidth(), Window.getHeight(), 0, 0, Window.getWidth(), Window.getHeight(), GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        render(Scene.getSkybox());

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

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

    public void forwardsRender(Shader shader, GameObject object) {
        shader.bind();

        Matrix4f view = Matrix4f.view(Camera.getMainCameraPosition(), Camera.getMainCameraRotation(), true);
        Matrix4f projection = Window.getProjectionMatrix();

        if (!object.isVisible()) {
            return;
        }
        if (object.hasChildren()) {
            for (GameObject go : object.getChildren()) {
                render(go);
            }
        }

        GL30.glBindVertexArray(object.getMesh().getVAO());
        enableVertexArrays(5);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, object.getMesh().getMaterial().getDiffuseID());

        shader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale()));
        shader.setUniform("view", view);
        shader.setUniform("projection", projection);

        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        disableVertexArrays(5);
        GL30.glBindVertexArray(0);

        shader.unbind();
    }

    private Matrix4f getLightSpaceMatrix() {
        Matrix4f lightOrtho = Matrix4f.ortho(-100, 100, -100, 100, 1.0f, 320.0f);
        Vector3f lightDir = new Vector3f(Scene.directionalLight.getDirection());
        Matrix4f lightView = Matrix4f.lookAt(new Vector3f(-lightDir.getX() * 20, lightDir.getY() * 20, -lightDir.getZ() * 20), Vector3f.zero(), Vector3f.oneY());
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

    private void enableVertexArrays(int n) {
        for (int i = 0; i < n; i++) {
            GL30.glEnableVertexAttribArray(i);
        }
    }

    private void disableVertexArrays(int n) {
        for (int i = 0; i < n; i++) {
            GL30.glDisableVertexAttribArray(i);
        }
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
        enableVertexArrays(5);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());

        gShader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale()));

        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        disableVertexArrays(5);
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
        enableVertexArrays(5);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());

        depthShader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale()));

        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        disableVertexArrays(5);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void render(GameObject gameObject) {
        forwardsRender(unlitShader, gameObject);
    }
}
