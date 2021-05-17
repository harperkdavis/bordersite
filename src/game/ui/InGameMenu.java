package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.graphics.text.TextMeshBuilder;
import engine.graphics.text.TextMode;
import engine.io.Input;
import engine.math.Mathf;
import engine.math.Vector3f;
import engine.math.Vector4f;
import engine.objects.Camera;
import engine.objects.GameObject;
import game.PlayerMovement;
import net.Client;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class InGameMenu extends Menu {

    private GameObject leftCrosshair;
    private GameObject rightCrosshair;
    private GameObject topCrosshair;
    private GameObject bottomCrosshair;
    private UiButton buy_backButton;
    private GameObject buy_backIcon;
    private UiPanel background, buy_moneyPanel, buy_moneyTimerPanel, buy_buyListPanel, buy_buyButton, buy_clearButton;

    private GameObject healthStatus, healthRegenStatus, staminaStatus, speedDial;

    private List<GameObject> compassDegrees, compassMarks;
    private GameObject headingDegrees;
    private final int COMPASS_MARKS = 36;

    private UiButton[] buy_itemTabs;
    private UiButton[] buy_itemSlots;

    protected boolean buyMenuOpen = false;
    protected boolean pauseMenuOpen = false;
    private float backTransparency = 0.0f;

    @Override
    public void init() {
        System.out.println("innit bruvving currently");

        compassDegrees = new ArrayList<>();
        compassMarks = new ArrayList<>();

        // Compass
        for (int i = 0; i < COMPASS_MARKS; i++) {
            compassDegrees.add(addObjectWithoutLoading(new GameObject(Vector3f.zero(), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("" + (i * 10), p(16), TextMode.CENTER))));
        }
        String[] cardinalDirections = new String[]{"N", "NNE", "NE", "NEE", "E", "SEE", "SE", "SSE", "S", "SSW", "SW", "SWW", "W", "NWW", "NW", "NNW"};
        for (int i = 0; i < 16; i++) {
            compassMarks.add(addObjectWithoutLoading(new GameObject(Vector3f.zero(), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh(cardinalDirections[i], p(16), TextMode.CENTER))));
        }
        headingDegrees = addObjectWithoutLoading(new GameObject(screen(1, p(50),  16), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("0", p(20), TextMode.CENTER)));


        // Status

        addObjectWithoutLoading(new UiPanel(0 + p(5), 2 - p(76), 0 + p(125), 2 - p(5), 19, 0.3f));

        addObjectWithoutLoading(new GameObject(screen(0 + p(20), 2 - p(22), 17), Vector3f.zero(), Vector3f.one(), UiBuilder.UIRect(p(256), Material.UI_STATUSBAR_OUTLINE_200)));
        addObjectWithoutLoading(new GameObject(screen(0 + p(14), 2 - p(18), 17), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(16), Material.UI_HEALTH_ICON)));

        addObjectWithoutLoading(new GameObject(screen(0 + p(20), 2 - p(42), 17), Vector3f.zero(), Vector3f.one(), UiBuilder.UIRect(p(256), Material.UI_STATUSBAR_OUTLINE_200)));
        addObjectWithoutLoading(new GameObject(screen(0 + p(14), 2 - p(38), 17), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(16), Material.UI_STAMINA_ICON)));

        addObjectWithoutLoading(new GameObject(screen(0 + p(20), 2 - p(62), 17), Vector3f.zero(), Vector3f.one(), UiBuilder.UIRect(p(128), Material.UI_STATUSBAR_OUTLINE_80)));
        addObjectWithoutLoading(new GameObject(screen(0 + p(14), 2 - p(58), 17), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(16), Material.UI_HEALTH_REGEN_ICON)));

        addObjectWithoutLoading(new GameObject(screen(0 + p(68), 2 - p(58), 17), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(16), Material.UI_SPEED_ICON)));

        healthStatus = addObjectWithoutLoading(new GameObject(screen(0 + p(20), 2 - p(22), 18), Vector3f.zero(), Vector3f.one(), UiBuilder.UIRect(p(16), Material.UI_STATUSBAR)));
        healthStatus.setColor(new Vector4f(0.25f, 0.6f, 0.3f, 1.0f));

        staminaStatus = addObjectWithoutLoading(new GameObject(screen(0 + p(20), 2 - p(42), 18), Vector3f.zero(), Vector3f.one(), UiBuilder.UIRect(p(16), Material.UI_STATUSBAR)));
        staminaStatus.setColor(new Vector4f(0.7f, 0.4f, 0.2f, 1.0f));

        healthRegenStatus = addObjectWithoutLoading(new GameObject(screen(0 + p(20), 2 - p(62), 18), Vector3f.zero(), Vector3f.one(), UiBuilder.UIRect(p(16), Material.UI_STATUSBAR)));
        healthRegenStatus.setColor(new Vector4f(0.5f, 0.3f, 0.55f, 1.0f));

        speedDial = addObjectWithoutLoading(new GameObject(screen(0 + p(72), 2 - p(51), 17), Vector3f.zero(), Vector3f.one(), TextMeshBuilder.TextMesh("0.0 m/s", p(14.0f), TextMode.LEFT)));

        // Buy Menu
        float boxesBorder = p(1);

        buy_itemTabs = new UiButton[10];
        buy_itemSlots = new UiButton[24];

        GameObject centerCrosshair = addObjectWithoutLoading(new GameObject(screen(1, 1, 11), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(8), Material.UI_CROSSHAIR_CENTER)));

        background = new UiPanel(0, 0, 2, 2, 10, 0.4f);
        addObjectWithoutLoading(background);

        buy_backButton = new UiButton(0.0f + boxesBorder, 0.0f + boxesBorder, 0.114583336f - boxesBorder, 0.2037037f - boxesBorder, 8, 0.4f) {
            @Override
            public void onClick() {
            }
        };
        addObjectWithoutLoading(buy_backButton);
        buy_backIcon = new GameObject(screen(0.057291668f, 0.10185185f, 7), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(64), Material.UI_BACK_ICON));

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


        System.out.println("innit bruvving ceased");
    }

    @Override
    public void load() {
        System.out.println("loading bruvving");
        int i = 0;
        for (GameObject object : objects) {
            object.load();
            i++;
        }
        System.out.println("loading bruvving ceasded " + i);
    }

    @Override
    public void update() {
        if (Client.isConnected()) {

            float rotRad = (float) Math.toRadians(Camera.getMainCameraRotation().getY());
            for (int i = 0; i < COMPASS_MARKS; i++) {
                float xVec = (float) (rotRad + (2.0f * i / COMPASS_MARKS) * Math.PI);
                compassDegrees.get(i).setPosition(screen(1 + p((float) Math.sin(xVec) * 300), p(16), 16));
                compassDegrees.get(i).setScale(Vector3f.one().multiply(Mathf.clamp((float) Math.cos(xVec * 2) * 1.2f, 1, 1.2f)));
                compassDegrees.get(i).setColor(new Vector4f(1, 1, 1, Mathf.clamp((float) Math.pow(Math.cos(xVec), 3), 0, 1)));
            }
            for (int i = 0; i < 16; i++) {
                float xVec = (float) (rotRad + (i / 8.0f) * Math.PI);
                compassMarks.get(i).setPosition(screen(1 + p((float) Math.sin(xVec) * 300), p(32), 16));
                compassMarks.get(i).setScale(Vector3f.one().multiply(Mathf.clamp((float) Math.cos(xVec * 2) * 1.2f, 1, 1.2f)));
                compassMarks.get(i).setColor(new Vector4f(1, 1, 1, Mathf.clamp((float) Math.pow(Math.cos(xVec), 3), 0, 1)));
            }
            int degrees = (int) -Camera.getMainCameraRotation().getY();
            headingDegrees.setMesh(TextMeshBuilder.TextMesh("" + (((degrees % 360) + 360) % 360), p(24), TextMode.CENTER));


            healthStatus.setScale(new Vector3f(PlayerMovement.getHealth() / 10.0f, 1, 1));
            float healthBarFactor = (1 - Math.min(PlayerMovement.getHealth() / 100.0f, 1));
            healthStatus.setColor(new Vector4f(0.25f + healthBarFactor * 0.5f, 0.6f - healthBarFactor * 0.5f, 0.3f - healthBarFactor * 0.2f, 1.0f));

            staminaStatus.setScale(new Vector3f(PlayerMovement.getStamina() / 10.0f, 1, 1));
            float staminaBarFactor = (1 - (PlayerMovement.getStamina() / 200.0f));
            staminaStatus.setColor(new Vector4f(Mathf.lerp(0.7f, 0.5f, staminaBarFactor), Mathf.lerp(0.4f, 0.5f, staminaBarFactor), Mathf.lerp(0.2f, 0.5f, staminaBarFactor), 1.0f));

            healthRegenStatus.setScale(new Vector3f(PlayerMovement.getHealthChange() * 4 + 4,1, 1));
            healthRegenStatus.setColor(new Vector4f(0.5f + PlayerMovement.getHealthChange() * 0.5f, 0.3f, 0.5f - PlayerMovement.getHealthChange() * 0.5f, 1.0f));

            Vector3f pos = PlayerMovement.getPosition();
            int intX = ((int) pos.getX());
            int intY = ((int) pos.getY());
            int intZ = ((int) pos.getZ());
            speedDial.setMesh(TextMeshBuilder.TextMesh(intX + ", " + intY + ", " + intZ, p(14.0f), TextMode.LEFT));

            float recoil = PlayerMovement.getPlayerMovement().getRecoil();
            float recoilOffset = recoil > 1 ? recoil * recoil : recoil;

            background.setColor(new Vector4f(0, 0, 0, backTransparency));

            if (Input.isKeyDown(GLFW.GLFW_KEY_B) && !pauseMenuOpen) {
                buyMenuOpen = !buyMenuOpen;
            }
            if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
                if (buyMenuOpen) {
                    buyMenuOpen = false;
                } else pauseMenuOpen = !pauseMenuOpen;
            }

            float BUY_MENU_SMOOTHING = 0.1f;
            if (buyMenuOpen || pauseMenuOpen) {
                backTransparency = Mathf.lerpdt(backTransparency, 0.4f, BUY_MENU_SMOOTHING);
            } else {
                backTransparency = Mathf.lerpdt(backTransparency, 0.0f, BUY_MENU_SMOOTHING);
            }

            if (buyMenuOpen) {
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
