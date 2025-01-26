package io.github.gra_doom;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;         // <-- Dodatkowy import
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import io.github.gra_doom.entity.*;
import java.util.Date;                           // <-- Dodatkowy import

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

    // Metoda create
    @Override
    public void create() {
        Texture[] textures = new Texture[8];
        String[] texture_path = {
            "eagle","bluestone","colorstone","greystone",
            "mossy","purplestone","redbrick","wood"
        };
        for (int i = 0; i < texture_path.length; i++) {
            textures[i] = new Texture(Gdx.files.internal("pics/" + texture_path[i] + ".png"));
        }

        dr = new DebugRenderer(20, 800, 400);
        rc = new RayCaster(800, 600, textures);
        dov = new DebugOverlay(1600, 800);

        dr.setMode(Renderer.DrawMode.CORNER_UL);
        rc.setMode(Renderer.DrawMode.FULL_WINDOW);
        dov.setMode(Renderer.DrawMode.FULL_WINDOW);

        ((RayCaster)rc).setDrawFloorEnabled(false);
        Gdx.graphics.setWindowedMode(800, 600);

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
        pauseFont.getData().setScale(2f);
        pauseFont.setColor(Color.RED);

        // Ustawiamy ekrany
        menuScreen = new MenuScreen();
        gameScreen = getScreen();
        setScreen(menuScreen);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
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
     * Rysowanie menu pauzy
     */
    private void drawPauseMenu() {
        // Obsługa klawiszy w pauzie
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
            inMenu = true;
            paused = false;
            setScreen(menuScreen);
        }

        // Rysowanie menu pauzy
        pauseBatch.begin();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float xCenter = w / 2f;
        float yCenter = h / 2f;

        pauseFont.draw(pauseBatch, "GAME PAUSED", xCenter - 100, yCenter + 80);
        pauseFont.draw(pauseBatch, "S - Save game", xCenter - 100, yCenter + 40);
        pauseFont.draw(pauseBatch, "R - Reset level", xCenter - 100, yCenter);
        pauseFont.draw(pauseBatch, "M - Back to menu", xCenter - 100, yCenter - 40);

        pauseBatch.end();
    }

    /**
     * Rysowanie ekranu GameOver (HP=0)
     */
    private void drawGameOver() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
            gameOver = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            inMenu = true;
            gameOver = false;
            setScreen(menuScreen);
        }

        pauseBatch.begin();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float xCenter = w / 2f;
        float yCenter = h / 2f;

        pauseFont.draw(pauseBatch, "GAME OVER", xCenter - 100, yCenter + 80);
        pauseFont.draw(pauseBatch, "Your Score: " + selectedMap.getTotalScore(), xCenter - 100, yCenter + 40);
        pauseFont.draw(pauseBatch, "R - Reset level", xCenter - 100, yCenter);
        pauseFont.draw(pauseBatch, "M - Back to menu", xCenter - 100, yCenter - 40);

        pauseBatch.end();
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
        selectedMap = new Map(arr, player);

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
    }

    // GET/SET
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
