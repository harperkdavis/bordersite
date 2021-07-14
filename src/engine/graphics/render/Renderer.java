package engine.graphics.render;

import engine.graphics.Shader;
import engine.objects.*;

public abstract class Renderer {

    protected Shader shader;

    private static MainRenderer main;
    private static UiRenderer ui;

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

}
