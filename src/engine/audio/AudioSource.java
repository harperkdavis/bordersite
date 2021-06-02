package engine.audio;

import engine.math.Vector3;
import org.lwjgl.openal.AL10;

public class AudioSource {

    private final int sourceId;
    private Vector3 soundPosition;
    private final boolean relative;

    public AudioSource(boolean loop, boolean relative) {
        this.sourceId = AL10.alGenSources();
        this.relative = relative;

        if (loop) {
            AL10.alSourcei(sourceId, AL10.AL_LOOPING, AL10.AL_TRUE);
        }
        if (relative) {
            AL10.alSourcei(sourceId, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
        }
    }

    public void setBuffer(int bufferId) {
        stop();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
    }

    public boolean isRelative() {
        return relative;
    }

    public Vector3 getSoundPosition() {
        return soundPosition;
    }

    public void setSoundPosition(Vector3 soundPosition) {
        this.soundPosition = soundPosition;
    }

    public void setPosition(Vector3 position) {
        AL10.alSource3f(sourceId, AL10.AL_POSITION, position.getX(), position.getY(), position.getZ());
    }

    public void setVelocity(Vector3 velocity) {
        AL10.alSource3f(sourceId, AL10.AL_VELOCITY, velocity.getX(), velocity.getY(), velocity.getZ());
    }

    public void setGain(float gain) {
        AL10.alSourcef(sourceId, AL10.AL_GAIN, gain);
    }

    public void setProperty(int param, float value) {
        AL10.alSourcef(sourceId, param, value);
    }

    public void play() {
        AL10.alSourcePlay(sourceId);
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public void pause() {
        AL10.alSourcePause(sourceId);
    }

    public void stop() {
        AL10.alSourceStop(sourceId);
    }

    public void unload() {
        stop();
        AL10.alDeleteSources(sourceId);
    }

}
