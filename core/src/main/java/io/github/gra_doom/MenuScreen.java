package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen implements Screen {

    public static final int MENU_WIDTH = 780;  // Szerokość świata menu
    public static final int MENU_HEIGHT = 480; // Wysokość świata menu

    private Stage stage;
    private Texture backgroundTexture;

    @Override
    public void show() {
        try {
            initializeViewport();

            MusicManager manager = MusicManager.getInstance();
            manager.setMusic("sound_and_music/menu.mp3", true);
            manager.playMusic();

            createMenuButtons();

        } catch (Exception e) {
            System.err.println("Błąd inicjalizacji MenuScreen: " + e.getMessage());
        }
    }

    private void initializeViewport() {
        stage = new Stage(new FitViewport(MENU_WIDTH, MENU_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("Menu/doommenu.jpg"));
    }

    private void createMenuButtons() {
        // Dodaj tło jako Image
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        backgroundImage.setZIndex(0); // Upewnij się, że jest na dole
        stage.addActor(backgroundImage);

        // Tworzenie przycisków
        Texture startGameTexture = new Texture(Gdx.files.internal("texts/start-game.png"));
        Texture loadGameTexture = new Texture(Gdx.files.internal("texts/load-game.png"));
        Texture optionsTexture = new Texture(Gdx.files.internal("texts/options.png"));
        Texture exitGameTexture = new Texture(Gdx.files.internal("texts/exit-game.png"));

        ImageButton startButton = Buttons.create(startGameTexture);
        ImageButton loadGameButton = Buttons.create(loadGameTexture);
        ImageButton optionsButton = Buttons.create(optionsTexture);
        ImageButton exitButton = Buttons.create(exitGameTexture);

        // Listener dla Play
        startButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                animateButtonsOffScreen(() -> {

                    MusicManager.getInstance().stopMusic();

                    // Ustawiamy ścieżkę do game.mp3
                    MusicManager.getInstance().setMusic("sound_and_music/game.mp3", true);
                    MusicManager.getInstance().playMusic();

                    // Teraz przechodzimy do ekranu gry
                    Main game = ((Main) Gdx.app.getApplicationListener());
                    game.resetGame();
                    game.inMenu = false;
                    game.setScreen(game.gameScreen);
                    Gdx.input.setInputProcessor(game.keyboardController);
                });
                return true;
            }
        });

        // Listener dla Options
        optionsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                animateButtonsOffScreen(() -> {
                    // Przełącz na OptionsScreen po animacji
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new OptionsScreen());
                });
                return true;
            }
        });

        // Listener dla Exit
        exitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                animateButtonsOffScreen(() -> {
                    // Zatrzymaj muzykę i wyjdź z gry
                    MusicManager.getInstance().stopMusic();
                    Gdx.app.exit();
                });
                return true;
            }
        });

        // Tworzenie tabeli z przyciskami
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        table.add(startButton).padBottom(20).center();
        table.row();
        table.add(loadGameButton).padBottom(20).center();
        table.row();
        table.add(optionsButton).padBottom(20).center();
        table.row();
        table.add(exitButton).center();

        stage.addActor(table);
    }

    private void animateButtonsOffScreen(Runnable onComplete) {
        float screenWidth = stage.getViewport().getWorldWidth();

        for (Actor actor : stage.getActors()) {
            if (actor instanceof Table) {
                Table table = (Table) actor;
                for (Cell<?> cell : table.getCells()) {
                    Actor button = cell.getActor();
                    if (button != null) {
                        // Animacja przesunięcia przycisków w prawo poza ekran
                        button.addAction(Actions.moveTo(screenWidth + button.getWidth(), button.getY(), 0.5f));
                    }
                }
                // Dodanie akcji wywołującej callback po zakończeniu animacji
                table.addAction(Actions.sequence(
                    Actions.delay(0.5f), // Czekaj, aż przyciski znikną
                    Actions.run(onComplete) // Wykonaj callback
                ));
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (stage != null) { // Sprawdzenie istnienia stage
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (stage != null) {
            stage.dispose();
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
