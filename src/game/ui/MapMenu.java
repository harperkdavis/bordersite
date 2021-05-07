package game.ui;

import engine.graphics.Material;
import engine.graphics.mesh.UiBuilder;
import engine.io.Input;
import engine.math.*;
import engine.objects.Camera;
import engine.objects.GameObject;
import game.PlayerMovement;
import game.world.World;
import main.Main;

import static game.ui.UserInterface.p;
import static game.ui.UserInterface.screen;

public class MapMenu extends Menu {

    private UiObject map, grid;
    private Vector2f mapPosition, mapVelocity;
    private float zoom = 1;
    private double pMouseX, pMouseY;
    private UiObject playerMarker;

    @Override
    public void init() {
        addObjectWithoutLoading(new UiObject(screen(1, 1, 12), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(4, new Material("/textures/map/map-background.png"))));
        map = addObjectWithoutLoading(new UiObject(screen(1, 1, 11), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(4, new Material("/textures/map/map.png"))));
        grid = addObjectWithoutLoading(new UiObject(screen(1, 1, 10), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(4, new Material("/textures/map/map-grid.png"))));

        playerMarker = addObjectWithoutLoading(new UiObject(screen(1, 1, 10), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(p(16), new Material("/textures/map/marker-player.png"))));
        addObjectWithoutLoading(new UiObject(screen(1, 1, 4), Vector3f.zero(), Vector3f.one(), UiBuilder.UICenter(4, new Material("/textures/map/map-overlay.png"))));

        mapPosition = new Vector2f(0, 0);
        mapVelocity = new Vector2f(0, 0);

        pMouseX = Input.getMouseX();
        pMouseY = Input.getMouseY();
    }

    @Override
    public void load() {
        for (GameObject object : objects) {
            object.load();
        }
    }

    @Override
    public void update() {
        if (Input.getScrollY() != 0) {
            float delta = (Input.getScrollY() > 0 ? 1.05f : Input.getScrollY() < 0 ? 1.0f / 1.05f : 1.0f);
            zoom *= delta;
            zoom = Mathf.clamp(zoom, 0.1f, 2.0f);
            if (zoom != 2.0f) {
                mapPosition.multiply(delta);
            }
        }

        Vector2f input = new Vector2f((float) (Input.getMouseX() - pMouseX), (float) (Input.getMouseY() - pMouseY)).multiply(Main.getDeltaTime() * 0.1f);
        mapVelocity.divide(1 + 4.0f * Main.getDeltaTime());

        if (Input.isMouseButton(0)) {
            mapVelocity.set(input.getX(), input.getY());
        }

        mapPosition.add(mapVelocity);

        map.setPosition(new Vector3f(screen(mapPosition.getX() + 1, mapPosition.getY() + 1, 11)));
        map.setScale(new Vector3f(zoom, zoom, zoom));
        grid.setPosition(new Vector3f(screen(mapPosition.getX() + 1, mapPosition.getY() + 1, 10)));
        grid.setScale(new Vector3f(zoom, zoom, zoom));

        playerMarker.setPosition(new Vector3f(getIconPosition(PlayerMovement.getPosition()), 1));
        playerMarker.setRotation(new Vector3f(0, 0, -Camera.getMainCameraRotation().getY()));

        mapPosition = new Vector2f(Mathf.clamp(mapPosition.getX(), -1.0f * zoom, 1.0f * zoom), Mathf.clamp(mapPosition.getY(), -2.0f * zoom, 2.0f * zoom));

        pMouseX = Input.getMouseX();
        pMouseY = Input.getMouseY();
    }

    private Vector2f getNormMapCoords(Vector3f realPosition) {
        return new Vector2f(realPosition.getX() / (511 * World.getScaleX()), realPosition.getZ() / (511 * World.getScaleZ()));
    }

    private Vector2f getIconPosition(Vector3f realPosition) {
        Vector2f normCoords = getNormMapCoords(realPosition);
        Matrix4f modifier = Matrix4f.transform(new Vector3f(mapPosition.getX() * zoom, mapPosition.getY() * zoom, 0), Vector3f.zero(), Vector3f.one().multiply(zoom));
        Vector4f pos = new Vector4f(normCoords.getX() * 2 - 1, normCoords.getY() * (16.0f / 9.0f) * 2 - (16.0f / 9.0f), 1, 1);
        pos.multiply(modifier);
        return new Vector2f(screen(pos.getX() + mapPosition.getX() + 1, pos.getY() + mapPosition.getY() + 1));
    }

    @Override
    public void unload() {
        for (GameObject object : objects) {
            object.unload();
        }
    }

}
