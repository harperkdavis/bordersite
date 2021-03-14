package engine.audio;

import engine.math.Vector3f;
import org.lwjgl.openal.AL10;

public class AudioListener {

    public AudioListener() {
    }

    public AudioListener(Vector3f position) {
        AL10.alListener3f(AL10.AL_POSITION, position.getX(), position.getY(), position.getZ());
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }

    public void setSpeed(Vector3f speed) {
        AL10.alListener3f(AL10.AL_VELOCITY, speed.getX(), speed.getY(), speed.getZ());
    }

    public void setPosition(Vector3f position) {
        AL10.alListener3f(AL10.AL_POSITION, position.getX(), position.getY(), position.getZ());
    }

    public void setOrientation(Vector3f at, Vector3f up) {
        float[] data = new float[6];
        data[0] = at.getX();
        data[1] = at.getY();
        data[2] = at.getZ();
        data[3] = up.getX();
        data[4] = up.getY();
        data[5] = up.getZ();
        AL10.alListenerfv(AL10.AL_ORIENTATION, data);
    }
}
