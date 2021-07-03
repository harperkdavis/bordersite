package engine.io;

import engine.util.Utils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class IconLoader {

    public static void setIcon(String path) throws Exception {
        IntBuffer w = MemoryUtil.memAllocInt(1);
        IntBuffer h = MemoryUtil.memAllocInt(1);
        IntBuffer comp = MemoryUtil.memAllocInt(1);

        // Icons
        {
            ByteBuffer icon16;
            ByteBuffer icon32;
            try {
                icon16 = Utils.ioResourceToByteBuffer(path, 2048);
                icon32 = Utils.ioResourceToByteBuffer(path, 4096);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try (GLFWImage.Buffer icons = GLFWImage.malloc(2) ) {
                ByteBuffer pixels16 = STBImage.stbi_load_from_memory(icon16, w, h, comp, 4);
                icons
                        .position(0)
                        .width(w.get(0))
                        .height(h.get(0))
                        .pixels(pixels16);

                ByteBuffer pixels32 = STBImage.stbi_load_from_memory(icon32, w, h, comp, 4);
                icons
                        .position(1)
                        .width(w.get(0))
                        .height(h.get(0))
                        .pixels(pixels32);

                icons.position(0);
                GLFW.glfwSetWindowIcon(Window.getWindow(), icons);

                STBImage.stbi_image_free(pixels32);
                STBImage.stbi_image_free(pixels16);
            }
        }

        MemoryUtil.memFree(comp);
        MemoryUtil.memFree(h);
        MemoryUtil.memFree(w);

    }

}
