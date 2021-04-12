package game;

import engine.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public interface GamePlane {

    void load();
    void fixedUpdate();
    void update();
    void render();
    void unload();

}
