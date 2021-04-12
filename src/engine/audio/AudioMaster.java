package engine.audio;

import engine.math.Vector3f;
import engine.objects.Camera;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AudioMaster {

    private static long device;
    private static long context;

    private static AudioListener listener;

    public static List<AudioBuffer> audioBufferList;

    public static ConcurrentMap<String, AudioSource> audioSourceMap;


    public static void load() {
        audioBufferList = new ArrayList<>();
        audioSourceMap = new ConcurrentHashMap<>();
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

        setAttenuationModel(AL10.AL_DISTANCE_MODEL);
    }

    public static void update() {
        for (String name : audioSourceMap.keySet()) {
            AudioSource source = audioSourceMap.get(name);
            if (source.isRelative()) {
                source.setPosition(getSoundCoordinates(source.getSoundPosition()));
            }
            if (!source.isPlaying()) {
                removeSoundSource(name);
            }
        }
    }

    public static void playSound(SoundEffect sound) {
        AudioSource as = new AudioSource(false, false);
        int buffer = AudioBuffer.getSoundEffectBufferId(sound);
        System.out.println(buffer);
        as.setBuffer(buffer);
        playSoundFromSource(as);
    }

    public static Vector3f getSoundCoordinates(Vector3f position) {
        return getSoundCoordinates(position, 1.2f);
    }

    public static Vector3f getSoundCoordinates(Vector3f position, float falloff) {
        Vector3f cPos = Camera.getMainCameraPosition();
        Vector3f cRot = Camera.getMainCameraRotation();
        double originAngle = Math.atan2(cPos.getX() - position.getX(), cPos.getZ() - position.getZ());
        double cameraAngle = Math.toRadians(cRot.getY());
        double angleToCamera = cameraAngle - originAngle;
        float distance = (float) Math.sqrt(Math.pow(cPos.getX() - position.getX(), 2) + Math.pow(cPos.getY() - position.getY(), 2) + Math.pow(cPos.getZ() - position.getZ(), 2));
        return new Vector3f((float) (Math.sin(angleToCamera) * Math.pow(distance, falloff)), 0.0f, (float) (Math.cos(angleToCamera) * Math.pow(distance, falloff)));
    }

    public static void playSound(SoundEffect sound, Vector3f position) {
        AudioSource as = new AudioSource(false, true);
        as.setBuffer(AudioBuffer.getSoundEffectBufferId(sound));
        as.setSoundPosition(position);
        as.setPosition(getSoundCoordinates(position));
        playSoundFromSource(as);
    }

    public static void playSoundFromSource(AudioSource audioSource) {
        String uuid = UUID.randomUUID().toString();
        addSoundSource(uuid, audioSource);
    }

    public static void addSoundSource(String name, AudioSource audioSource) {
        audioSource.play();
        audioSourceMap.put(name, audioSource);
    }

    public static AudioSource getSoundSource(String name) {
        return audioSourceMap.get(name);
    }

    public static void removeSoundSource(String name) {
        audioSourceMap.remove(name);
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
        for (AudioSource audioSource : audioSourceMap.values()) {
            audioSource.unload();
        }
        audioSourceMap.clear();
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
