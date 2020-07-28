package sound;

import engine.LogicBehaviour;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;

public class AudioSource extends LogicBehaviour {
    private static final List<AudioSource> sources = new ArrayList<>();
    private final int id;
    public AudioClip clip;
    public float startGain = -30;

    public AudioSource() {
        id = alGenSources();
        SetGain(startGain);
        sources.add(this);
    }

    public static void CleanUp() {
        for (int i = 0; i < sources.size(); i++) sources.get(0).Destroy();
    }

    public void Play() {
        SetGain(startGain);
        alSourcePlay(id);
    }

    public void SetClip(AudioClip c) {
        clip = c;
        alSourcei(id, AL_BUFFER, c.id);
    }

    public void Pause() {
        alSourcePause(id);
    }

    public void Stop() {
        alSourceStop(id);
    }

    public void SetGain(float v) {
        alSourcef(id, AL_GAIN, v);
    }

    public void Destroy() {
        Stop();
        alDeleteSources(id);
        sources.remove(this);
    }
}