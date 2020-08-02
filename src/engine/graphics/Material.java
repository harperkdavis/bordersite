package engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.IOException;

public class Material {

    private String texturePath;
    private Texture texture;
    private float width, height;
    private int textureID;

    public Material(String path) {
        texturePath = path;
    }

    public void create() {
        try {
            texture = TextureLoader.getTexture(texturePath.split("[.]")[1], Material.class.getResourceAsStream(texturePath), GL11.GL_LINEAR);
        } catch (IOException e) {
            System.err.println("[ERROR] Invalid texture at " + texturePath);
        }
        width = texture.getWidth();
        height = texture.getHeight();
        textureID = texture.getTextureID();
    }

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

}
