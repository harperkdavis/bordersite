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
    private boolean nSpec;

    protected static List<Material> materials = new ArrayList<>();
    protected static Map<String, Material> mapMaterials = new HashMap<>();

    // GLOBAL

    public static final Material DEFAULT = new Material("/textures/default.png");
    public static final Material TEST = new Material("/textures/test.png");

    // FONT

    public static final Material FONT_KREMLIN = new Material("/textures/font/kremlin_font.png");
    public static final Material FONT_SYNE_SMALL = new Material("/textures/font/syne_font_small.png");
    public static final Material FONT_SYNE_BIG = new Material("/textures/font/syne_font_big.png");

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

    public static final Material UI_BORDERSITE_LOGO = new Material("/textures/ui/bordersite_logo.png");
    public static final Material UI_LOADING_LOGO = new Material("/textures/ui/loading_logo.png");

    public static final Material UI_CROSSHAIR = new Material("/textures/ui/crosshair.png");

    // WEAPONS

    public static final Material WPN_KNIFE_DEFAULT = new Material("/textures/weapons/knife_default.png");
    public static final Material WPN_KNIFE_NEBULA = new Material("/textures/weapons/knife_nebula.png");

    public Material(String diffuse, String normal, String specular) {
        materials.add(this);

        diffusePath = diffuse;
        normalPath = normal;
        specularPath = specular;
        nSpec = true;
    }


    public Material(String diffuse, boolean nSpec) {
        materials.add(this);

        diffusePath = diffuse;
        this.nSpec = nSpec;
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
