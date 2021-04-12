package engine.graphics.render;

import engine.graphics.Shader;
import engine.graphics.light.DirectionalLight;
import engine.graphics.light.PointLight;
import engine.io.Window;
import engine.math.Matrix4f;
import engine.math.Vector3f;
import engine.objects.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public abstract class Renderer {

    protected Shader shader;

    private static Renderer main;
    private static Renderer ui;
    private static Renderer viewmodel;

    public Renderer(Shader shader) {
        this.shader = shader;
    }

    public abstract void render(GameObject gameObject);

    public static Renderer getMain() {
        return main;
    }

    public static void setMain(MainRenderer main) {
        Renderer.main = main;
    }

    public static Renderer getUi() {
        return ui;
    }

    public static void setUi(UiRenderer ui) {
        Renderer.ui = ui;
    }

    public static Renderer getViewmodel() {
        return viewmodel;
    }

    public static void setViewmodel(ViewmodelRenderer viewmodel) {
        Renderer.viewmodel = viewmodel;
    }
}
