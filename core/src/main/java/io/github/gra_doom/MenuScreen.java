package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen{private Stage stage;
    private Skin skin;

    @Override
    public void show() {
        // Tworzenie sceny z widokiem ekranu
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Ładowanie skórki - sprawdź, czy ścieżka do pliku JSON jest poprawna
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Tworzenie tabeli do układania elementów interfejsu
        Table table = new Table();
        table.setFillParent(true);  // Wypełnia cały ekran
        stage.addActor(table);

        // Tworzenie przycisków
        TextButton startButton = new TextButton("Start Game", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Dodanie przycisków do tabeli z wypełnieniem
        table.add(startButton).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);  // Odstęp między przyciskami
        table.add(exitButton).fillX().uniformX();

        // Dodanie nasłuchiwaczy do przycisków
        startButton.addListener(event -> {
            if (startButton.isPressed()) {
                // Tutaj można przełączyć się na ekran gry
                // Przykład: yourGame.setScreen(new GameScreen());
                System.out.println("Start Game clicked!");
            }
            return true;
        });

        exitButton.addListener(event -> {
            if (exitButton.isPressed()) {
                Gdx.app.exit();  // Zamknięcie aplikacji
            }
            return true;
        });
    }

    @Override
    public void render(float delta) {
        // Czyszczenie ekranu
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Aktualizacja i rysowanie sceny
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Aktualizacja widoku
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // Zwalnianie zasobów
        stage.dispose();
        skin.dispose();
    }
}
