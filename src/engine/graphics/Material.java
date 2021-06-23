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
    protected static Map<String, Material> mapMaterials = new HashMap<>();

    // GLOBAL

    public static final Material DEFAULT = new Material("/textures/default.png");
    public static final Material TEST = new Material("/textures/test.png");

    // FONT

    public static final Material FONT_KREMLIN = new Material("/textures/font/kremlin_font.png");
    public static final Material FONT_SYNE = new Material("/textures/font/syne_font.png");

    // ENVIRONMENT

    public static final Material ENV_GRASS = new Material("/textures/environment/grass.png");
    public static final Material ENV_STONE = new Material("/textures/environment/stone.png");
    public static final Material ENV_FLOOR = new Material("/textures/environment/floor.png", true);
    public static final Material ENV_BRICK = new Material("/textures/environment/brick.png", true);
    public static final Material ENV_TREE = new Material("/textures/environment/tree.png");
    public static final Material ENV_GRID = new Material("/textures/environment/grid.png");

    public static final Material ENV_HEIGHTMAP = new Material("/textures/environment/heightmap.png");
    public static final Material ENV_TREEMAP = new Material("/textures/environment/treemap.png");
    public static final Material ENV_TREEMAP_NOISE = new Material("/textures/environment/treemap_noise.png");

    // UI

    public static final Material UI_BACK_ICON = new Material("/textures/ui/back_icon.png");
    public static final Material UI_BORDERSITE_LOGO = new Material("/textures/ui/bordersite_logo.png");
    public static final Material UI_LOADING_LOGO = new Material("/textures/ui/loading_logo.png");

    public static final Material UI_CROSSHAIR_CENTER = new Material("/textures/ui/crosshair_center.png");
    public static final Material UI_CROSSHAIR_OUT = new Material("/textures/ui/crosshair_out.png");

    public static final Material UI_EFFECT_BLEEDING = new Material("/textures/ui/effect_bleeding.png");
    public static final Material UI_EFFECT_FATIGUED = new Material("/textures/ui/effect_fatigued.png");

    public static final Material UI_HEALTH_ICON = new Material("/textures/ui/health_icon.png");
    public static final Material UI_HEALTH_REGEN_ICON = new Material("/textures/ui/health_regen_icon.png");
    public static final Material UI_SPEED_ICON = new Material("/textures/ui/speed_icon.png");
    public static final Material UI_STAMINA_ICON = new Material("/textures/ui/stamina_icon.png");

    public static final Material UI_HIT_INDICATOR = new Material("/textures/ui/hit_indicator.png");
    public static final Material UI_HIT_MARKER = new Material("/textures/ui/hit_marker.png");

    public static final Material UI_STATUSBAR = new Material("/textures/ui/statusbar.png");
    public static final Material UI_STATUSBAR_OUTLINE_80 = new Material("/textures/ui/statusbar_outline_80.png");
    public static final Material UI_STATUSBAR_OUTLINE_100 = new Material("/textures/ui/statusbar_outline_100.png");
    public static final Material UI_STATUSBAR_OUTLINE_200 = new Material("/textures/ui/statusbar_outline_200.png");

    // WEAPONS

    public static final Material WPN_KNIFE_DEFAULT = new Material("/textures/weapons/knife_default.png");
    public static final Material WPN_KNIFE_NEBULA = new Material("/textures/weapons/knife_nebula.png");

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
            normalPath = "/textures/default_normal.png";
            specularPath = "/textures/default_specular.png";
        }
    }

    public Material(String diffuse) {
        this(diffuse, false);
    }

    // Creates the material and loads it
    public void create() {
        // Try to load the texture
        try {
            diffuseTexture = TextureLoader.getTexture(diffusePath.split("[.]")[1], Material.class.getResourceAsStream(diffusePath), GL11.GL_NEAREST);
            normalTexture = TextureLoader.getTexture(normalPath.split("[.]")[1], Material.class.getResourceAsStream(normalPath), GL11.GL_NEAREST);
            specularTexture = TextureLoader.getTexture(specularPath.split("[.]")[1], Material.class.getResourceAsStream(specularPath), GL11.GL_NEAREST);
        } catch (Exception e) {
            System.err.println("[ERROR] Invalid texture at " + diffusePath + " | " + normalPath + " | " + specularPath);
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

    public static Material getMapMaterial(String name) {
        return mapMaterials.get(name);
    }

}
