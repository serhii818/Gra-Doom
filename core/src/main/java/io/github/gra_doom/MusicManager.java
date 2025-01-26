    package io.github.gra_doom;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.audio.Music;

    /**
     * Uniwersalny zarządca muzyki (singleton).
     * Może wczytywać różne pliki (menu.mp3, game.mp3, etc.)
     */
    public class MusicManager {

        private static MusicManager instance;
        private Music backgroundMusic;       // Aktualnie odtwarzana muzyka
        private boolean isPlaying = false;   // Czy muzyka jest w stanie "odtwarzania"
        private String currentTrack = null;  // Zapamiętanie ścieżki do aktualnego utworu

        private MusicManager() {
            // Konstruktor prywatny - użyj getInstance().
            // Nie ładuj tu muzyki — poczekaj, aż ktoś wywoła setMusic().
        }

        // Singleton
        public static MusicManager getInstance() {
            if (instance == null) {
                instance = new MusicManager();
            }
            return instance;
        }

        /**
         * Ładuje (lub przeładowuje) utwór z pliku i ustawia go do odtwarzania.
         * Jeśli ścieżka jest taka sama jak już załadowana,
         * to nie przeładowujemy ponownie (opcjonalne zachowanie).
         *
         * @param filePath ścieżka do pliku, np. "sound_and_music/menu.mp3"
         * @param looping czy ma być w pętli
         */
        public void setMusic(String filePath, boolean looping) {
            // Jeśli próbujemy ustawić ten sam plik, a już mamy go wczytanego, nie musimy nic robić
            if (filePath.equals(currentTrack) && backgroundMusic != null) {
                backgroundMusic.setLooping(looping);
                return;
            }

            // Inaczej - zatrzymaj i usuń poprzednią muzykę (o ile istnieje)
            if (backgroundMusic != null) {
                backgroundMusic.stop();
                backgroundMusic.dispose();
                isPlaying = false;
            }

            // Załaduj nowy plik
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(filePath));
            backgroundMusic.setLooping(looping);
            backgroundMusic.setVolume(0.5f);

            currentTrack = filePath;
        }

        /**
         * Rozpocznij (lub wznowienie) odtwarzania muzyki.
         * Jeśli wcześniej była wstrzymana (pauseMusic), to play() wznowi od ostatniej pozycji.
         * Jeśli nie mamy jeszcze załadowanej muzyki (backgroundMusic == null),
         * to ta metoda nic nie zrobi.
         */
        public void playMusic() {
            if (backgroundMusic != null && !isPlaying) {
                backgroundMusic.play();
                isPlaying = true;
            }
        }

        /**
         * Wstrzymaj odtwarzanie muzyki (zapamięta aktualną pozycję).
         */
        public void pauseMusic() {
            if (backgroundMusic != null && isPlaying) {
                backgroundMusic.pause();
                isPlaying = false;
            }
        }

        /**
         * Zatrzymaj muzykę całkowicie (wraca do początku).
         * Kolejne 'playMusic()' zacznie od 0:00.
         */
        public void stopMusic() {
            if (backgroundMusic != null && isPlaying) {
                backgroundMusic.stop();
                isPlaying = false;
            }
        }

        /**
         * Czy aktualnie jest odtwarzana jakaś muzyka?
         */
        public boolean isMusicPlaying() {
            return isPlaying;
        }

        /**
         * Zwolnij zasoby (np. przy zamykaniu gry).
         */
        public void dispose() {
            if (backgroundMusic != null) {
                backgroundMusic.dispose();
                backgroundMusic = null;
            }
            isPlaying = false;
            currentTrack = null;
        }
    }
