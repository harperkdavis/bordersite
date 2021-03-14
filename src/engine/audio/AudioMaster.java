package engine.audio;

import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioMaster {

    private static long device;
    private static long context;

    private static AudioListener listener;

    public static List<AudioBuffer> audioBufferList;

    public static Map<String, AudioSource> soundSourceMap;


    public static void load() {
        audioBufferList = new ArrayList<>();
        soundSourceMap = new HashMap<>();
        device = ALC10.alcOpenDevice((ByteBuffer) null);
        if (device == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = ALC10.alcCreateContext(device, (IntBuffer) null);
        if (context == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        ALC10.alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    public static void addSoundSource(String name, AudioSource audioSource) {
        soundSourceMap.put(name, audioSource);
    }

    public static AudioSource getSoundSource(String name) {
        return soundSourceMap.get(name);
    }

    public static void playSoundSource(String name) {
        AudioSource audioSource = soundSourceMap.get(name);
        if (audioSource != null && !audioSource.isPlaying()) {
            audioSource.play();
        }
    }

    public static void removeSoundSource(String name) {
        soundSourceMap.remove(name);
    }

    public static void addSoundBuffer(AudioBuffer audioBuffer) {
        audioBufferList.add(audioBuffer);
    }

    public static AudioListener getListener() {
        return listener;
    }

    public static void setListener(AudioListener listener) {
        AudioMaster.listener = listener;
    }

    public static void setAttenuationModel(int model) {
        AL10.alDistanceModel(model);
    }

    public static void unload() {
        for (AudioSource audioSource : soundSourceMap.values()) {
            audioSource.unload();
        }
        soundSourceMap.clear();
        for (AudioBuffer audioBuffer : audioBufferList) {
            audioBuffer.unload();
        }
        audioBufferList.clear();
        if (context != MemoryUtil.NULL) {
            ALC10.alcDestroyContext(context);
        }
        if (device != MemoryUtil.NULL) {
            ALC10.alcCloseDevice(device);
        }
    }
}
