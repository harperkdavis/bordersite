package engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.IOException;

// Stores Materials that create the appearance of an Object
public class Material {


    private final float reflectance;

    private final String texturePath;
    private Texture texture;
    private float width, height;
    private int textureID;

    public Material(String path) {
        texturePath = path;
        reflectance = 1.0f;
    }

    public Material(String path, float reflectance) {
        texturePath = path;
        this.reflectance = reflectance;
    }

    // Creates the material and loads it
    public void create() {
        // Try to load the texture
        try {
            texture = TextureLoader.getTexture(texturePath.split("[.]")[1], Material.class.getResourceAsStream(texturePath), GL11.GL_NEAREST);
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
}
