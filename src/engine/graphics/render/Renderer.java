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

    private static MainRenderer main;
    private static UiRenderer ui;
    private static ViewmodelRenderer viewmodel;

    public Renderer(Shader shader) {
        this.shader = shader;
    }

    public abstract void render(GameObject gameObject);

    public static MainRenderer getMain() {
        return main;
    }

    public static void setMain(MainRenderer main) {
        Renderer.main = main;
    }

    public static UiRenderer getUi() {
        return ui;
    }

    public static void setUi(UiRenderer ui) {
        Renderer.ui = ui;
    }

    public static ViewmodelRenderer getViewmodel() {
        return viewmodel;
    }

    public static void setViewmodel(ViewmodelRenderer viewmodel) {
        Renderer.viewmodel = viewmodel;
    }
}
