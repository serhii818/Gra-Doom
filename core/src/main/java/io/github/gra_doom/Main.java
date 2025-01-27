package io.github.gra_doom;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.gra_doom.entity.*;
import java.util.Date;

public class Main extends Game {

    Renderer dr, rc, dov;
    Map selectedMap;
    Player cam;

    KeyboardController keyboardController;
    public Screen gameScreen;
    public Screen menuScreen;
    boolean inMenu = true; // startujemy w menu

    // HUD
    private PlayerInterface playerInterface;

    // Flagi stanu
    private boolean paused = false;
    private boolean gameOver = false;

    // Rysowanie menu pauzy / game over
    private SpriteBatch pauseBatch;
    private BitmapFont pauseFont;
    private BitmapFont scoreFont; // Nowy font do wyświetlania wyniku

    // Kamera i Viewport
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 800f;
    private final float VIRTUAL_HEIGHT = 600f;

    // Tekstury dla UI
    private Texture gamePausedTexture;
    private Texture saveGameTexture;
    private Texture resetTexture;
    private Texture backMenuTexture;
    private Texture gameOverTexture;
    private Texture gameOverResetTexture;
    private Texture gameOverBackMenuTexture;

    // Przechowywanie pozycji i rozmiarów przycisków
    private RectangleButton saveButtonPause;
    private RectangleButton resetButtonPause;
    private RectangleButton backMenuButtonPause;

    private RectangleButton resetButtonGameOver;
    private RectangleButton backMenuButtonGameOver;

    @Override
    public void create() {
        // Inicjalizacja kamery i viewportu
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        viewport.apply();

        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();

        // Wczytywanie tekstur UI
        gamePausedTexture = new Texture(Gdx.files.internal("texts/game-paused.png"));
        saveGameTexture = new Texture(Gdx.files.internal("texts/save-game.png"));
        resetTexture = new Texture(Gdx.files.internal("texts/reset.png"));
        backMenuTexture = new Texture(Gdx.files.internal("texts/back-menu.png"));
        gameOverTexture = new Texture(Gdx.files.internal("texts/game-over.png"));
        gameOverResetTexture = new Texture(Gdx.files.internal("texts/reset.png")); // Używamy tej samej tekstury co reset w pauzie
        gameOverBackMenuTexture = new Texture(Gdx.files.internal("texts/back-menu.png")); // Używamy tej samej tekstury co back menu w pauzie

        // Wczytywanie tekstur mapy
        Texture[] textures = new Texture[8];
        String[] texture_path = {
            "eagle","bluestone","colorstone","greystone",
            "mossy","purplestone","redbrick","wood"
        };
        for (int i = 0; i < texture_path.length; i++) {
            textures[i] = new Texture(Gdx.files.internal("pics/" + texture_path[i] + ".png"));
        }

        // Inicjalizacja rendererów
        dr = new DebugRenderer(20, 800, 400);
        rc = new RayCaster(800, 600, textures);
        dov = new DebugOverlay(1600, 800);

        dr.setMode(Renderer.DrawMode.CORNER_UL);
        rc.setMode(Renderer.DrawMode.FULL_WINDOW);
        dov.setMode(Renderer.DrawMode.FULL_WINDOW);

        ((RayCaster)rc).setDrawFloorEnabled(false);
        Gdx.graphics.setWindowedMode((int)VIRTUAL_WIDTH, (int)VIRTUAL_HEIGHT);

        // Gracz
        cam = Player.makePlayer();

        // Przykładowy pocisk i broń
        Projectile p = new Projectile(new Vector2(0, 0), 0.5f, "bullet/b2.png", 5,25, true);
        p.setTransforms(3, 3, 0);
        Weapon pw = new ProjectileWeapon(10, AmmoType.PISTOL, p);
        cam.setWeapon(pw);

        // Interfejs
        playerInterface = new PlayerInterface();

        // Tworzymy mapę z wrogami
        initMapWithEnemiesAndItems(cam);

        // Klawiatura
        keyboardController = new KeyboardController(cam, selectedMap, this);
        Gdx.input.setInputProcessor(keyboardController);

        // Rysowanie pauzy
        pauseBatch = new SpriteBatch();
        pauseFont = new BitmapFont();
        pauseFont.setColor(Color.RED); // Czcionka w kolorze czerwonym

        // Tworzenie fontu dla wyniku w Game Over
        scoreFont = new BitmapFont();
        scoreFont.setColor(Color.RED);
        scoreFont.getData().setScale(2f); // Powiększenie fontu

        // Ustawiamy ekrany
        menuScreen = new MenuScreen();
        gameScreen = getScreen();
        setScreen(menuScreen);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
        playerInterface.resize(width, height);
    }

    /**
     * Główna pętla
     */
    @Override
    public void render() {
        if (inMenu) {
            super.render();
            return;
        }

        // Muzyka
        if (paused || gameOver) {
            MusicManager.getInstance().pauseMusic();
        } else {
            MusicManager.getInstance().playMusic();
        }

        // Czyszczenie ekranu
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Obsługa kliknięć myszy
        if (Gdx.input.justTouched()) {
            Vector2 touchPos = getMouseWorldCoordinates();
            handleClick(touchPos.x, touchPos.y);
        }

        // GameOver
        if (gameOver) {
            drawGameOver();
            return;
        }

        // Normalna gra (jeśli nie pauza)
        if (!paused) {
            selectedMap.update();

            int currentEnemyCount = selectedMap.getEnemyCount();
            playerInterface.updateEnemiesRemaining(currentEnemyCount);

            int newScore = selectedMap.getTotalScore();
            playerInterface.updateScore(newScore);

            rc.render(selectedMap);
            dov.render(selectedMap);
            playerInterface.render(selectedMap);

            if (selectedMap.getPlayer().getHealth() <= 0) {
                gameOver = true;
                return;
            }
        } else {
            drawPauseMenu();
        }
    }

    /**
     * Rysowanie menu pauzy z użyciem tekstur i obsługą kliknięć
     */
    private void drawPauseMenu() {
        // Obsługa klawiszy w pauzie (opcjonalne, jeśli chcesz zachować możliwość używania klawiatury)
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            // Wyświetlenie informacji o rozpoczęciu zapisu gry
            System.out.println("Enter save name:");

            // Wywołanie metody saveGame z nazwą zapisu
            String saveName = "playerSave_" + System.currentTimeMillis(); // Dynamiczna nazwa zapisu
            saveGame(saveName);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
            paused = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            backToMenu();
        }

        // Ustawienie batcha na kamerę
        pauseBatch.setProjectionMatrix(camera.combined);
        pauseBatch.begin();

        // Obliczenie dynamicznej skali tekstur
        float scaleFactor = Math.min(viewport.getWorldWidth() / VIRTUAL_WIDTH, viewport.getWorldHeight() / VIRTUAL_HEIGHT);

        // Obliczenie dynamicznych pozycji
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        float xCenter = w / 2f;
        float yCenter = h / 2f;

        // Definicja wysokości poszczególnych elementów UI (proporcjonalnie do skali)
        float headerHeight = gamePausedTexture.getHeight() * scaleFactor;
        float buttonHeight = saveGameTexture.getHeight() * scaleFactor;
        float buttonSpacing = 20f * scaleFactor; // Odstęp między przyciskami

        // Obliczenie szerokości tekstur (skalowanie proporcjonalne)
        float headerWidth = gamePausedTexture.getWidth() * scaleFactor;
        float buttonWidth = saveGameTexture.getWidth() * scaleFactor; // Zakładamy, że wszystkie przyciski mają tę samą szerokość

        // Rysowanie nagłówka "GAME PAUSED"
        pauseBatch.draw(gamePausedTexture, xCenter - (headerWidth / 2), yCenter + (buttonHeight + buttonSpacing) * 1.5f, headerWidth, headerHeight);

        // Rysowanie przycisku "Save Game"
        float saveX = xCenter - (buttonWidth / 2);
        float saveY = yCenter + (buttonHeight + buttonSpacing) * 0.5f;
        pauseBatch.draw(saveGameTexture, saveX, saveY, buttonWidth, buttonHeight);
        saveButtonPause = new RectangleButton(saveX, saveY, buttonWidth, buttonHeight);

        // Rysowanie przycisku "Reset Level"
        float resetX = xCenter - (buttonWidth / 2);
        float resetY = yCenter - buttonSpacing;
        pauseBatch.draw(resetTexture, resetX, resetY, buttonWidth, buttonHeight);
        resetButtonPause = new RectangleButton(resetX, resetY, buttonWidth, buttonHeight);

        // Rysowanie przycisku "Back to Menu"
        float backX = xCenter - (buttonWidth / 2);
        float backY = yCenter - (buttonHeight + 1.5f * buttonSpacing);
        pauseBatch.draw(backMenuTexture, backX, backY, buttonWidth, buttonHeight);
        backMenuButtonPause = new RectangleButton(backX, backY, buttonWidth, buttonHeight);

        pauseBatch.end();
    }

    /**
     * Rysowanie ekranu GameOver z użyciem tekstur i obsługą kliknięć oraz wyświetlaniem wyniku
     */
    private void drawGameOver() {
        // Obsługa klawiszy w GameOver (opcjonalne)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
            gameOver = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            backToMenu();
        }

        // Ustawienie batcha na kamerę
        pauseBatch.setProjectionMatrix(camera.combined);
        pauseBatch.begin();

        // Obliczenie dynamicznej skali tekstur
        float scaleFactor = Math.min(viewport.getWorldWidth() / VIRTUAL_WIDTH, viewport.getWorldHeight() / VIRTUAL_HEIGHT);

        // Obliczenie dynamicznych pozycji
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        float xCenter = w / 2f;
        float yCenter = h / 2f;

        // Definicja wysokości poszczególnych elementów UI (proporcjonalnie do skali)
        float headerHeight = gameOverTexture.getHeight() * scaleFactor;
        float buttonHeight = gameOverResetTexture.getHeight() * scaleFactor;
        float buttonSpacing = 20f * scaleFactor; // Odstęp między przyciskami

        // Obliczenie szerokości tekstur (skalowanie proporcjonalne)
        float headerWidth = gameOverTexture.getWidth() * scaleFactor;
        float buttonWidth = gameOverResetTexture.getWidth() * scaleFactor; // Zakładamy, że wszystkie przyciski mają tę samą szerokość

        // Rysowanie nagłówka "GAME OVER"
        pauseBatch.draw(gameOverTexture, xCenter - (headerWidth / 2), yCenter + (buttonHeight + buttonSpacing) * 1.5f, headerWidth, headerHeight);

        // Dodanie wyświetlania wyniku gracza
        String scoreText = "Your Score: " + playerInterface.getScore();
        GlyphLayout layout = new GlyphLayout(scoreFont, scoreText);

        // Stała definiująca przesunięcie w górę (20 pikseli) w skali świata gry
        final float SCORE_Y_OFFSET = 50f * scaleFactor;

        // Obliczanie pozycji tekstu wyniku
        float scoreX = xCenter - (layout.width / 2);
        float scoreY = yCenter + (buttonHeight + buttonSpacing) * 0.5f + SCORE_Y_OFFSET; // Przesunięcie w górę o 20px

        // Renderowanie tekstu wyniku
        scoreFont.draw(pauseBatch, layout, scoreX, scoreY);

        // Rysowanie przycisku "Reset Level"
        float resetX = xCenter - (buttonWidth / 2);
        float resetY = yCenter - buttonSpacing;
        pauseBatch.draw(gameOverResetTexture, resetX, resetY, buttonWidth, buttonHeight);
        resetButtonGameOver = new RectangleButton(resetX, resetY, buttonWidth, buttonHeight);

        // Rysowanie przycisku "Back to Menu"
        float backX = xCenter - (buttonWidth / 2);
        float backY = yCenter - (buttonHeight + 1.5f * buttonSpacing);
        pauseBatch.draw(gameOverBackMenuTexture, backX, backY, buttonWidth, buttonHeight);
        backMenuButtonGameOver = new RectangleButton(backX, backY, buttonWidth, buttonHeight);

        pauseBatch.end();
    }

    /**
     * Obsługa kliknięć myszy na przyciski
     * @param x - współrzędna x kliknięcia w świecie gry
     * @param y - współrzędna y kliknięcia w świecie gry
     */
    private void handleClick(float x, float y) {
        if (paused) {
            if (saveButtonPause != null && saveButtonPause.contains(x, y)) {
                String saveName = "playerSave_" + System.currentTimeMillis();
                saveGame(saveName);
                System.out.println("Gra została zapisana.");
            }
            if (resetButtonPause != null && resetButtonPause.contains(x, y)) {
                resetGame();
                paused = false;
            }
            if (backMenuButtonPause != null && backMenuButtonPause.contains(x, y)) {
                backToMenu();
            }
        } else if (gameOver) {
            if (resetButtonGameOver != null && resetButtonGameOver.contains(x, y)) {
                resetGame();
                gameOver = false;
            }
            if (backMenuButtonGameOver != null && backMenuButtonGameOver.contains(x, y)) {
                backToMenu();
            }
        }
    }

    /**
     * Pobiera współrzędne myszy przekształcone do świata gry
     * @return Vector2 z przekształconymi współrzędnymi myszy
     */
    private Vector2 getMouseWorldCoordinates() {
        Vector2 screenCoords = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(screenCoords);
        return screenCoords;
    }

    /**
     * Reset gry
     */
    public void resetGame() {
        inMenu = false;
        paused = false;
        gameOver = false;

        cam = Player.makePlayer();

        Projectile p = new Projectile(new Vector2(0, 0), 0.5f, "bullet/b2.png", 5,25, true);
        p.setTransforms(3, 3, 0);
        Weapon pw = new ProjectileWeapon(10, AmmoType.PISTOL, p);
        cam.setWeapon(pw);

        initMapWithEnemiesAndItems(cam);

        keyboardController = new KeyboardController(cam, selectedMap, this);
        Gdx.input.setInputProcessor(keyboardController);

        MusicManager.getInstance().stopMusic();
        MusicManager.getInstance().playMusic();
    }

    /**
     * Powrót do menu głównego
     */
    public void backToMenu() {
        inMenu = true;
        paused = false;
        gameOver = false;
        setScreen(menuScreen);
    }

    private void initMapWithEnemiesAndItems(Player player) {
        int[][] arr = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,2,2,2,2,2,0,0,0,0,3,0,3,0,3,0,0,0,1},
            {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,3,0,0,0,3,0,0,0,1},
            {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,2,2,0,2,2,0,0,0,0,3,0,3,0,3,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,4,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,0,0,0,5,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,4,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };
        selectedMap = new Map(arr, cam);

        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.IMP, new Vector2(2, 2)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.IMP, new Vector2(20, 20)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.ZOMBIE, new Vector2(10, 10)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.ZOMBIE, new Vector2(11, 11)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.ZOMBIE, new Vector2(12, 10)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.ZOMBIE, new Vector2(10, 20)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.DEMON, new Vector2(18, 21)));

        selectedMap.addEntity(PickUpItem.makeItem(new Vector2(2, 3),  PickUpItem.Item.HEALTH25));
        selectedMap.addEntity(PickUpItem.makeItem(new Vector2(20, 21), PickUpItem.Item.HEALTH25));
        selectedMap.addEntity(PickUpItem.makeItem(new Vector2(10, 21), PickUpItem.Item.HEALTH50));
        selectedMap.addEntity(PickUpItem.makeItem(new Vector2(20, 11), PickUpItem.Item.HEALTH100));
    }

    /**
     * Metoda zapisująca stan gry do pliku .txt
     * @param saveName - nazwa zapisu (bez rozszerzenia)
     */
    public void saveGame(String saveName) {
        // Upewnij się, że nazwa zapisu jest poprawna
        if (saveName == null || saveName.isEmpty()) {
            saveName = "defaultSave_" + System.currentTimeMillis();
        }

        try {
            // Sprawdzenie, czy folder "Saves/" istnieje, jeśli nie, twórz go
            FileHandle saveDirectory = Gdx.files.local("Saves/");
            if (!saveDirectory.exists()) {
                saveDirectory.mkdirs(); // Tworzenie folderu
                System.out.println("Folder 'Saves/' utworzony.");
            }

            // Ścieżka do pliku zapisu
            FileHandle file = Gdx.files.local("Saves/" + saveName + ".txt");

            // Pobranie danych do zapisu
            float health = cam.getHealth();
            Vector2 pos = cam.pos.cpy();
            int score = playerInterface.getScore();
            String time = new Date().toString();

            // Tworzenie zawartości pliku
            StringBuilder sb = new StringBuilder();
            sb.append("Save Name: ").append(saveName).append("\n");
            sb.append("Health: ").append(health).append("\n");
            sb.append("Position: (").append(pos.x).append(", ").append(pos.y).append(")\n");
            sb.append("Score: ").append(score).append("\n");
            sb.append("Saved at: ").append(time).append("\n");

            // Zapisywanie do pliku
            file.writeString(sb.toString(), false);

            System.out.println("Gra zapisana do pliku: " + file.path());
        } catch (Exception e) {
            System.out.println("Save Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveMap(int[][] map, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            filePath = "map.json";
        }
        FileHandle file = Gdx.files.local(filePath);
        Json json = new Json();
        String mapJson = json.toJson(map);
        file.writeString(mapJson, false);
    }

    @Override
    public void dispose() {
        super.dispose();
        pauseBatch.dispose();
        pauseFont.dispose();
        scoreFont.dispose(); // Zwalnianie nowego fontu

        // Zwalnianie tekstur UI
        gamePausedTexture.dispose();
        saveGameTexture.dispose();
        resetTexture.dispose();
        backMenuTexture.dispose();
        gameOverTexture.dispose();
        gameOverResetTexture.dispose();
        gameOverBackMenuTexture.dispose();
    }

    // GET/SET
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Klasa pomocnicza do przechowywania prostokątów reprezentujących przyciski
     */
    private static class RectangleButton {
        float x, y, width, height;

        public RectangleButton(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean contains(float pointX, float pointY) {
            return pointX >= x && pointX <= x + width &&
                pointY >= y && pointY <= y + height;
        }
    }
}
