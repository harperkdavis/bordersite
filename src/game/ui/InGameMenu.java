package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.io.Input;
import engine.math.Mathf;
import engine.math.Vector3f;
import engine.math.Vector4f;
import engine.objects.GameObject;
import game.PlayerMovement;
import net.Client;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class InGameMenu extends Menu {

    private GameObject centerCrosshair, leftCrosshair, rightCrosshair, topCrosshair, bottomCrosshair;
    private UiButton buy_backButton;
    private UiObject buy_backIcon;
    private UiPanel background, buy_moneyPanel, buy_moneyTimerPanel, buy_buyListPanel, buy_buyButton, buy_clearButton;

    private UiButton[] buy_itemTabs;
    private UiButton[] buy_itemSlots;

    private boolean buyMenuOpen = false;
    private boolean pauseMenuOpen = false;
    private final float BUY_MENU_SMOOTHING = 0.1f;
    private float backTransparency = 0.0f;

    @Override
    public void init() {
        
        float boxesBorder = p(1);

        buy_itemTabs = new UiButton[10];
        buy_itemSlots = new UiButton[24];

        centerCrosshair = addObjectWithoutLoading(new UiObject(screen(1, 1, 11), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(8), new Material("/textures/crosshair-center.png"))));

        background = new UiPanel(0, 0, 2, 2, 10, 0.4f);
        addObjectWithoutLoading(background);

        buy_backButton = new UiButton(0.0f + boxesBorder, 0.0f + boxesBorder, 0.114583336f - boxesBorder, 0.2037037f - boxesBorder, 8, 0.4f) {
            @Override
            public void onClick() {

            }
        };
        addObjectWithoutLoading(buy_backButton);
        buy_backIcon = new UiObject(screen(0.057291668f, 0.10185185f, 7), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(64), new Material("/textures/ui/back-icon.png")));

        // Top Bar

        buy_moneyPanel = new UiPanel(0.114583336f + boxesBorder, 0.0f + boxesBorder, 1.4479166f - boxesBorder, 0.2037037f - boxesBorder, 8, 0.4f);
        addObjectWithoutLoading(buy_moneyPanel);

        buy_moneyTimerPanel = new UiPanel(1.4479166f + boxesBorder, 0.0f + boxesBorder, 1.5625f - boxesBorder, 0.2037037f - boxesBorder, 8, 0.4f);
        addObjectWithoutLoading(buy_moneyTimerPanel);

        buy_backButton.setPosition(screen(1, -4, 8));
        buy_moneyPanel.setPosition(screen(1, -4, 8));
        buy_moneyTimerPanel.setPosition(screen(1, -4, 8));

        // Buy List

        buy_buyListPanel = new UiPanel(1.5625f + boxesBorder, 0.0f, 2.0f, 2.0f, 8, 0.4f);
        addObjectWithoutLoading(buy_buyListPanel);

        buy_buyButton = new UiPanel(1.5822917f, 1.6074075f, 1.9822917f, 1.8444444f, 7, new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
        addObjectWithoutLoading(buy_buyButton);

        buy_clearButton = new UiPanel(1.5822917f, 1.8777778f, 1.9822917f, 1.9518518f, 7, new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        addObjectWithoutLoading(buy_clearButton);

        buy_buyListPanel.setPosition(screen(4, 1, 8));
        buy_buyButton.setPosition(screen(4, 1, 7));
        buy_clearButton.setPosition(screen(4, 1, 7));

        // Tabs

        for (int i = 0; i < 10; i++) {
            UiButton button = new UiButton(i * 0.15625f + boxesBorder, 0.2037037f + boxesBorder, (i + 1) * 0.15625f - boxesBorder, 0.4074074f - boxesBorder, 8, 0.4f) {
                @Override
                public void onClick() {

                }
            };
            buy_itemTabs[i] = button;
            addObjectWithoutLoading(button);

            button.setPosition(screen(1, -3 - i * 2, 6));
        }

        // Buy Squares

        for (int i = 0; i < 24; i++) {
            int xPos = i % 6;
            int yPos = i / 6;
            float offset = 0;
            if (i / 6 == 3) {
                offset = p(1);
            }

            UiButton button = new UiButton(0.26041666f * xPos + boxesBorder, 0.4074074f + yPos * 0.3981482f + boxesBorder + offset, 0.26041666f * (xPos + 1) - boxesBorder, 0.4074074f + (yPos + 1) * 0.3981482f - boxesBorder + offset, 8, 0.3f) {
                @Override
                public void onClick() {

                }
            };
            buy_itemSlots[i] = button;
            addObjectWithoutLoading(button);

            button.setPosition(screen(1, 3 + i * 2, 5));
        }

        // Pause Menu



    }

    @Override
    public void load() {
        for (GameObject object : objects) {
            object.load();
        }
    }

    @Override
    public void update() {
        if (Client.isConnected()) {
            float recoil = PlayerMovement.getPlayerMovement().getRecoil();
            float recoilOffset = recoil > 1 ? recoil * recoil : recoil;

            background.setColor(new Vector4f(0, 0, 0, backTransparency));

            if (Input.isKeyDown(GLFW.GLFW_KEY_B)) {
                buyMenuOpen = !buyMenuOpen;
            }
            if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
                if (buyMenuOpen) {
                    buyMenuOpen = false;
                } else if (pauseMenuOpen) {
                    pauseMenuOpen = false;
                } else {
                    pauseMenuOpen = true;
                }
            }

            if (buyMenuOpen || pauseMenuOpen) {
                backTransparency = Mathf.lerpdt(backTransparency, 0.4f, BUY_MENU_SMOOTHING);
            } else {
                backTransparency = Mathf.lerpdt(backTransparency, 0.0f, BUY_MENU_SMOOTHING);
            }

            if (buyMenuOpen) {
                System.out.println(buy_backButton.getPosition().toString());
                buy_backButton.setPosition(Vector3f.lerpdt(buy_backButton.getPosition(), screen(1, 1, 8), BUY_MENU_SMOOTHING));
                buy_backIcon.setPosition(Vector3f.lerpdt(buy_backIcon.getPosition(), screen(0.057291668f, 0.10185185f, 7), BUY_MENU_SMOOTHING));
                buy_moneyPanel.setPosition(Vector3f.lerpdt(buy_moneyPanel.getPosition(), screen(1, 1, 8), BUY_MENU_SMOOTHING));
                buy_moneyTimerPanel.setPosition(Vector3f.lerpdt(buy_moneyTimerPanel.getPosition(), screen(1, 1, 8), BUY_MENU_SMOOTHING));
                buy_buyListPanel.setPosition(Vector3f.lerpdt(buy_buyListPanel.getPosition(), screen(1, 1, 8), BUY_MENU_SMOOTHING));
                buy_buyButton.setPosition(Vector3f.lerpdt(buy_buyButton.getPosition(), screen(1, 1, 7), BUY_MENU_SMOOTHING));
                buy_clearButton.setPosition(Vector3f.lerpdt(buy_clearButton.getPosition(), screen(1, 1, 7), BUY_MENU_SMOOTHING));

                for (int i = 0; i < 10; i++) {
                    buy_itemTabs[i].setPosition(Vector3f.lerpdt(buy_itemTabs[i].getPosition(), screen(1, 1, 6), BUY_MENU_SMOOTHING));
                }

                for (int i = 0; i < 24; i++) {
                    buy_itemSlots[i].setPosition(Vector3f.lerpdt(buy_itemSlots[i].getPosition(), screen(1, 1, 5), BUY_MENU_SMOOTHING));
                }

            } else {
                buy_backButton.setPosition(Vector3f.lerpdt(buy_backButton.getPosition(), screen(1, -4, 8), BUY_MENU_SMOOTHING));
                buy_backIcon.setPosition(Vector3f.lerpdt(buy_backIcon.getPosition(), screen(0.057291668f, -3.89814815f, 7), BUY_MENU_SMOOTHING));

                buy_moneyPanel.setPosition(Vector3f.lerpdt(buy_moneyPanel.getPosition(), screen(1, -4, 8), BUY_MENU_SMOOTHING));
                buy_moneyTimerPanel.setPosition(Vector3f.lerpdt(buy_moneyTimerPanel.getPosition(), screen(1, -4, 8), BUY_MENU_SMOOTHING));
                buy_buyListPanel.setPosition(Vector3f.lerpdt(buy_buyListPanel.getPosition(), screen(4, 1, 8), BUY_MENU_SMOOTHING));
                buy_buyButton.setPosition(Vector3f.lerpdt(buy_buyButton.getPosition(), screen(4, 1, 7), BUY_MENU_SMOOTHING));
                buy_clearButton.setPosition(Vector3f.lerpdt(buy_clearButton.getPosition(), screen(4, 1, 7), BUY_MENU_SMOOTHING));

                for (int i = 0; i < 10; i++) {
                    buy_itemTabs[i].setPosition(Vector3f.lerpdt(buy_itemTabs[i].getPosition(), screen(1, -3 - i, 6), BUY_MENU_SMOOTHING));
                }

                for (int i = 0; i < 24; i++) {
                    buy_itemSlots[i].setPosition(Vector3f.lerpdt(buy_itemSlots[i].getPosition(), screen(1, 3 + i, 5), BUY_MENU_SMOOTHING));
                }
            }

            buy_backButton.update();
            for (UiButton button : buy_itemTabs) {
                button.update();
            }
            for (UiButton button : buy_itemSlots) {
                button.update();
            }
        }
    }

    @Override
    public void unload() {
        for (GameObject object : objects) {
            object.unload();
        }
    }

}
