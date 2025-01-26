package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Klasa odpowiedzialna wyłącznie za HUD (wyświetlanie punktów, zdrowia, liczby wrogów).
 */
public class PlayerInterface {

    private BitmapFont font;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture weaponIcon; // Ikona broni

    // Kamera i viewport przeznaczone tylko dla HUD
    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    // Wewnętrzne klasy do zarządzania punktami i liczbą wrogów
    private ScoreManager scoreManager;
    private EnemyInfo enemyInfo;

    /**
     * Konstruktor.
     * Inicjalizuje czcionkę, obiekty do rysowania (batch, shapeRenderer),
     * kamerę HUD oraz menedżery do punktów i liczby wrogów.
     */
    public PlayerInterface() {
        // Inicjalizacja czcionki
        font = new BitmapFont();
        font.getData().setScale(1.5f); // Rozmiar czcionki
        font.setColor(Color.WHITE);    // Kolor czcionki (biały)

        // Inicjalizacja SpriteBatch i ShapeRenderer
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Załaduj teksturę ikony broni
        weaponIcon = new Texture("bullet/b2.png");

        // Inicjalizacja menedżerów wewnętrznych
        scoreManager = new ScoreManager();
        enemyInfo = new EnemyInfo();

        // --- Ustawienia kamery i viewportu HUD ---
        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(1280, 720, uiCamera);
        uiViewport.apply();

        // Ustawiamy początkową pozycję kamery, aby patrzyła na środek wirtualnego ekranu
        uiCamera.position.set(uiCamera.viewportWidth / 2f, uiCamera.viewportHeight / 2f, 0);
        uiCamera.update();
    }

    /**
     * Wywoływane z metody resize(int width, int height) w głównej klasie gry.
     */
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    /**
     * Zwraca aktualnie przechowywany wynik (score).
     */
    public int getScore() {
        return scoreManager.getScore();
    }

    /**
     * Główna metoda rysująca HUD.
     * Wywołuj ją np. w metodzie render() klasy Main (po narysowaniu świata).
     *
     * @param map bieżąca mapa, z której np. bierzemy informacje o graczu (health).
     */
    public void render(Map map) {
        // Aktualizacja kamery HUD
        uiCamera.update();

        // Ustawiamy macierz projekcji dla shapeRenderer i batch na kamerę HUD
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        batch.setProjectionMatrix(uiCamera.combined);

        float screenWidth  = uiViewport.getWorldWidth();  // "wirtualna" szerokość HUD
        float screenHeight = uiViewport.getWorldHeight(); // "wirtualna" wysokość HUD

        // Wysokość paska HUD
        float hudHeight = 150f;
        float hudY = 0f; // Pozycja HUD (na dole)

        // Rysowanie tła HUD
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.5f, 0.8f, 1f, 1f);
        shapeRenderer.rect(0, hudY, screenWidth, hudHeight);
        shapeRenderer.end();

        batch.begin();

        // Szerokość "obszaru kolumny" do wyśrodkowania
        float colWidth = 80f;

        // Pozycje kolumn (procentowo)
        float healthPositionX   = screenWidth  * 0.125f;
        float weaponPositionX   = screenWidth  * 0.375f;
        float scorePositionX    = screenWidth  * 0.625f;
        float enemiesPositionX  = screenWidth  * 0.875f;

        // Pozycje w pionie
        float labelTopY  = hudY + hudHeight - 20;
        float valueTopY  = hudY + hudHeight - 80;

        // HEALTH
        font.draw(batch, "Health",
            healthPositionX - colWidth/2f,
            labelTopY,
            colWidth,
            Align.center,
            false);

        font.draw(batch, String.valueOf(map.getPlayer().getHealth()),
            healthPositionX - colWidth/2f,
            valueTopY,
            colWidth,
            Align.center,
            false);

        // WEAPON
        font.draw(batch, "Weapon",
            weaponPositionX - colWidth/2f,
            labelTopY,
            colWidth,
            Align.center,
            false);

        // Ikona broni: rysujemy, odejmując połowę szerokości (64/2=32)
        batch.draw(weaponIcon,
            weaponPositionX - 32,
            valueTopY - 45, // minimalnie w górę, by się ładnie mieściło
            64, 64);

        // SCORE
        font.draw(batch, "Score",
            scorePositionX - colWidth/2f,
            labelTopY,
            colWidth,
            Align.center,
            false);

        font.draw(batch, String.valueOf(scoreManager.getScore()),
            scorePositionX - colWidth/2f,
            valueTopY,
            colWidth,
            Align.center,
            false);

        // ENEMIES
        font.draw(batch, "Enemies",
            enemiesPositionX - colWidth/2f,
            labelTopY,
            colWidth,
            Align.center,
            false);

        font.draw(batch, String.valueOf(enemyInfo.getRemaining()),
            enemiesPositionX - colWidth/2f,
            valueTopY,
            colWidth,
            Align.center,
            false);

        batch.end();
    }

    /**
     * Aktualizacja wyniku (score) - delegacja do ScoreManager.
     */
    public void updateScore(int newScore) {
        scoreManager.setScore(newScore);
    }

    /**
     * Aktualizacja liczby wrogów (enemies) - delegacja do EnemyInfo.
     */
    public void updateEnemiesRemaining(int enemies) {
        enemyInfo.setRemaining(enemies);
    }

    /**
     * Zwalnianie zasobów (czcionka, batch, shapeRenderer, ikona).
     */
    public void dispose() {
        font.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        weaponIcon.dispose();
    }

    // ======================
    // KLASY WEWNĘTRZNE
    // ======================
    /**
     * Klasa wewnętrzna do zarządzania punktami (score).
     */
    private static class ScoreManager {
        private int score;

        public ScoreManager() {
            this.score = 0;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int newScore) {
            this.score = newScore;
        }
    }

    /**
     * Klasa wewnętrzna do przechowywania liczby wrogów (enemies).
     */
    private static class EnemyInfo {
        private int remaining;

        public EnemyInfo() {
            this.remaining = 0; // Domyślnie 0
        }

        public int getRemaining() {
            return remaining;
        }

        public void setRemaining(int enemies) {
            this.remaining = enemies;
        }
    }
}
