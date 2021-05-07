package engine.audio;

import engine.util.Utils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.system.MemoryUtil.NULL;

// From LWJGLGameDev

public class AudioBuffer {

    private final int bufferId;

    private ShortBuffer pcm = null;

    public AudioBuffer(String file){
        this.bufferId = AL10.alGenBuffers();
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = null;
            try {
                pcm = readVorbis(file, 32 * 1024, info);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Copy to buffer
            AL10.alBufferData(bufferId, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate());
        }
    }

    public static int getSoundEffectBufferId(SoundEffect effect) {
        String path = "resources/audio/" + effect.name().toLowerCase() + ".ogg";
        AudioBuffer buffer = new AudioBuffer(path);
        AudioMaster.addSoundBuffer(buffer);
        return buffer.getBufferId();
    }

    public static int getSoundEffectBufferId(String buffer) {
        return new AudioBuffer(buffer).getBufferId();
    }

    public int getBufferId() {
        return this.bufferId;
    }

    public void unload() {
        AL10.alDeleteBuffers(this.bufferId);
        if (pcm != null) {
            MemoryUtil.memFree(pcm);
        }
    }

    private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer vorbis = Utils.ioResourceToByteBuffer(resource, bufferSize);
            IntBuffer error = stack.mallocInt(1);
            long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == NULL) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            STBVorbis.stb_vorbis_get_info(decoder, info);

            int channels = info.channels();

            int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

            pcm = MemoryUtil.memAllocShort(lengthSamples);

            pcm.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
            STBVorbis.stb_vorbis_close(decoder);

            return pcm;
        }
    }

}
