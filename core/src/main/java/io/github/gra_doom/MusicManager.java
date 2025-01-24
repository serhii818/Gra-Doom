package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicManager {

    private static MusicManager instance; // Singleton
    private Music backgroundMusic;       // Muzyka w tle
    private boolean isPlaying = false;   // Czy muzyka jest odtwarzana

    // Prywatny konstruktor
    private MusicManager() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sound_and_music/menu.mp3"));
        backgroundMusic.setLooping(true); // Ustaw odtwarzanie w pętli
        backgroundMusic.setVolume(0.5f);  // Głośność 50%
    }

    // Singleton - uzyskaj instancję klasy
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    // Rozpocznij odtwarzanie muzyki
    public void playMusic() {
        if (!isPlaying) {
            backgroundMusic.play();
            isPlaying = true;
        }
    }

    // Zatrzymaj muzykę
    public void stopMusic() {
        if (isPlaying) {
            backgroundMusic.stop();
            isPlaying = false;
        }
    }

    // Sprawdź, czy muzyka jest odtwarzana
    public boolean isMusicPlaying() {
        return isPlaying;
    }

    // Zwolnij zasoby muzyki
    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}
