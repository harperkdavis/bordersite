package game.ui;

import engine.io.Input;
import engine.math.Mathf;
import engine.math.Vector4f;
import engine.objects.GameObject;
import game.ui.text.UiText;

import static game.ui.UserInterface.p;

public abstract class UiButton extends UiPanel {

    private final float x1;
    private final float y1;
    private final float x2;
    private final float y2;
    private final float transparency;
    private float t;

    public UiButton(float x1, float y1, float x2, float y2, float l, float transparency) {
        super(x1, y1, x2, y2, l, transparency);
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.transparency = transparency;
        this.t = this.transparency;
    }

    public void update() {
        if (isHovered() && isVisible()) {
            t = Mathf.lerpdt(t, 1.0f, 0.1f);
            if (Input.isMouseButtonDown(0)) {
                onClick();
            }
        } else {
            t = Mathf.lerpdt(t, 0.0f, 0.1f);
        }
        setMesh(UiPanel.createPanel(x1 + p(t), y1 + p(2 * t), x2 - p(t), y2 - p(2 * t), getMesh().getMaterial()));
        setColor(new Vector4f(0, 0, 0, transparency + t * 0.05f));
    }

    private boolean isHovered() {
        float mX = UserInterface.getNormMouseX();
        float mY = UserInterface.getNormMouseY();

        return mX >= x1 && mX <= x2 && mY >= y1 && mY <= y2;
    }

    public abstract void onClick();

}
