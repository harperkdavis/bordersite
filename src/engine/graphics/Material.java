package engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

// Stores Materials that create the appearance of an Object
public class Material {

    private final float reflectance;

    private final String texturePath;
    private Texture texture;
    private float width, height;
    private int textureID;

    protected static List<Material> materials = new ArrayList<>();

    // GLOBAL

    public static final Material DEFAULT = new Material("default.png");
    public static final Material TEST = new Material("test.png");

    // FONT

    public static final Material FONT_KREMLIN = new Material("font/kremlin_font.png");
    public static final Material FONT_UBUNTU = new Material("font/ubuntu_font.png");
    public static final Material FONT_UBUNTU_OUTLINE = new Material("font/ubuntu_font_outline.png");

    // ENVIRONMENT

    public static final Material ENV_GRASS = new Material("environment/grass.png");
    public static final Material ENV_STONE = new Material("environment/stone.png");
    public static final Material ENV_TREE = new Material("environment/tree.png");
    public static final Material ENV_GRID = new Material("environment/grid.png");

    public static final Material ENV_HEIGHTMAP = new Material("environment/heightmap.png");
    public static final Material ENV_TREEMAP = new Material("environment/treemap.png");
    public static final Material ENV_TREEMAP_NOISE = new Material("environment/treemap_noise.png");

    // MAP

    public static final Material MAP_MAP = new Material("map/map.png");
    public static final Material MAP_BACKGROUND = new Material("map/map_background.png");
    public static final Material MAP_GRID = new Material("map/map_grid.png");
    public static final Material MAP_OVERLAY = new Material("map/map_overlay.png");

    public static final Material MAP_MARKER_PLAYER = new Material("map/marker_player.png");

    // UI

    public static final Material UI_BACK_ICON = new Material("ui/back_icon.png");
    public static final Material UI_BORDERSITE_LOGO = new Material("ui/bordersite_logo.png");
    public static final Material UI_LOADING_LOGO = new Material("ui/loading_logo.png");

    public static final Material UI_CROSSHAIR_CENTER = new Material("ui/crosshair_center.png");
    public static final Material UI_CROSSHAIR_OUT = new Material("ui/crosshair_out.png");

    public static final Material UI_EFFECT_BLEEDING = new Material("ui/effect_bleeding.png");
    public static final Material UI_EFFECT_FATIGUED = new Material("ui/effect_fatigued.png");

    public static final Material UI_HEALTH_ICON = new Material("ui/health_icon.png");
    public static final Material UI_HEALTH_REGEN_ICON = new Material("ui/health_regen_icon.png");
    public static final Material UI_SPEED_ICON = new Material("ui/speed_icon.png");
    public static final Material UI_STAMINA_ICON = new Material("ui/stamina_icon.png");

    public static final Material UI_HIT_INDICATOR = new Material("ui/hit_indicator.png");
    public static final Material UI_HIT_MARKER = new Material("ui/hit_marker.png");

    public static final Material UI_STATUSBAR = new Material("ui/statusbar.png");
    public static final Material UI_STATUSBAR_OUTLINE_80 = new Material("ui/statusbar_outline_80.png");
    public static final Material UI_STATUSBAR_OUTLINE_100 = new Material("ui/statusbar_outline_100.png");
    public static final Material UI_STATUSBAR_OUTLINE_200 = new Material("ui/statusbar_outline_200.png");

    // WEAPONS

    public static final Material WPN_KNIFE_DEFAULT = new Material("weapons/knife_default.png");
    public static final Material WPN_KNIFE_NEBULA = new Material("weapons/knife_nebula.png");


    private Material(String path) {
        materials.add(this);

        texturePath = path;
        reflectance = 1.0f;
    }

    public Material(int textureID) {
        texture = null;
        this.textureID = textureID;
        reflectance = 1.0f;
        texturePath = "";
    }

    // Creates the material and loads it
    public void create() {
        // Try to load the texture
        try {
            texture = TextureLoader.getTexture("/textures/" + texturePath.split("[.]")[1], Material.class.getResourceAsStream("/textures/" + texturePath), GL11.GL_NEAREST);
        } catch (IOException e) {
            System.err.println("[ERROR] Invalid texture at " + texturePath);
        }
        // Gets the width and height
        width = texture.getWidth();
        height = texture.getHeight();
        textureID = texture.getTextureID();
    }

    public void create(boolean linear) {
        // Try to load the texture
        try {
            int mode = linear ? GL11.GL_LINEAR : GL11.GL_NEAREST;
            texture = TextureLoader.getTexture(texturePath.split("[.]")[1], Material.class.getResourceAsStream(texturePath), mode);
        } catch (IOException e) {
            System.err.println("[ERROR] Invalid texture at " + texturePath);
        }
        // Gets the width and height
        width = texture.getWidth();
        height = texture.getHeight();
        textureID = texture.getTextureID();
    }

    // Unloads the texture
    public void destroy() {
        GL13.glDeleteTextures(textureID);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getTextureID() {
        return textureID;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getReflectance() {
        return reflectance;
    }

    public String getTexturePath() {
        return texturePath;
    }

}
