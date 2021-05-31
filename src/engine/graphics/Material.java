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


    private final String diffusePath, specularPath, normalPath;
    private Texture diffuseTexture, specularTexture, normalTexture;
    private int diffuseID, specularID, normalID;
    private float width, height;

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
    public static final Material ENV_FLOOR = new Material("environment/floor.png", true);
    public static final Material ENV_BRICK = new Material("environment/brick.png", true);
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

    public Material(String diffuse, String normal, String specular) {
        materials.add(this);

        diffusePath = diffuse;
        normalPath = normal;
        specularPath = specular;
    }


    public Material(String diffuse, boolean nSpec) {
        materials.add(this);

        diffusePath = diffuse;
        if (nSpec) {
            normalPath = diffuse.split("[.]")[0] + "_normal." + diffuse.split("[.]")[1];
            specularPath = diffuse.split("[.]")[0] + "_specular." + diffuse.split("[.]")[1];
        } else {
            normalPath = "default_normal.png";
            specularPath = "default_specular.png";
        }
    }

    public Material(String diffuse) {
        this(diffuse, false);
    }

    // Creates the material and loads it
    public void create() {
        // Try to load the texture
        try {
            diffuseTexture = TextureLoader.getTexture("/textures/" + diffusePath.split("[.]")[1], Material.class.getResourceAsStream("/textures/" + diffusePath), GL11.GL_NEAREST);
            normalTexture = TextureLoader.getTexture("/textures/" + normalPath.split("[.]")[1], Material.class.getResourceAsStream("/textures/" + normalPath), GL11.GL_NEAREST);
            specularTexture = TextureLoader.getTexture("/textures/" + specularPath.split("[.]")[1], Material.class.getResourceAsStream("/textures/" + specularPath), GL11.GL_NEAREST);
        } catch (Exception e) {
            System.err.println("[ERROR] Invalid texture at " + diffusePath);
            e.printStackTrace();
        }
        // Gets the width and height
        width = diffuseTexture.getWidth();
        height = diffuseTexture.getHeight();

        diffuseID = diffuseTexture.getTextureID();
        normalID = normalTexture.getTextureID();
        specularID = specularTexture.getTextureID();

    }

    // Unloads the texture
    public void destroy() {
        GL13.glDeleteTextures(diffuseID);
        GL13.glDeleteTextures(normalID);
        GL13.glDeleteTextures(specularID);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getDiffuseID() {
        return diffuseID;
    }

    public Texture getDiffuseTexture() {
        return diffuseTexture;
    }

    public String getDiffusePath() {
        return diffusePath;
    }


    public int getNormalID() {
        return normalID;
    }

    public Texture getNormalTexture() {
        return normalTexture;
    }

    public String getNormalPath() {
        return normalPath;
    }


    public int getSpecularID() {
        return specularID;
    }

    public Texture getSpecularTexture() {
        return specularTexture;
    }

    public String getSpecularPath() {
        return specularPath;
    }

}
