package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen implements Screen {

    private static final int WORLD_WIDTH = 720;  // Szerokość świata gry
    private static final int WORLD_HEIGHT = 480; // Wysokość świata gry

    private Stage stage;
    private Texture backgroundTexture;
    private SpriteBatch batch;

    @Override
    public void show() {
        // Użyj FitViewport zamiast ScreenViewport
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Załaduj teksturę tła
        backgroundTexture = new Texture(Gdx.files.internal("doommenu.jpg"));
        batch = new SpriteBatch();

        // Załaduj tekstury dla przycisków
        Texture startGameTexture = new Texture(Gdx.files.internal("texts/start-game.png"));
        Texture loadGameTexture = new Texture(Gdx.files.internal("texts/load-game.png"));
        Texture optionsTexture = new Texture(Gdx.files.internal("texts/options.png"));
        Texture exitGameTexture = new Texture(Gdx.files.internal("texts/exit-game.png"));

        // Utwórz przyciski z tekstur
        ImageButton startButton = new ImageButton(new TextureRegionDrawable(startGameTexture));
        ImageButton loadGameButton = new ImageButton(new TextureRegionDrawable(loadGameTexture));
        ImageButton optionsButton = new ImageButton(new TextureRegionDrawable(optionsTexture));
        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(exitGameTexture));

        // Ustaw skalowanie i punkt odniesienia
        setupButtonScaling(startButton);
        setupButtonScaling(loadGameButton);
        setupButtonScaling(optionsButton);
        setupButtonScaling(exitButton);

        // Dodaj listener do animacji powiększania i zmniejszania
        addHoverAnimation(startButton);
        addHoverAnimation(loadGameButton);
        addHoverAnimation(optionsButton);
        addHoverAnimation(exitButton);

        // Ustawienia pozycji i układ na scenie
        Table table = new Table();
        table.center(); // Wyśrodkuj tabelę
        table.setFillParent(true); // Tabela wypełnia ekran

        // Dodanie przycisków do tabeli
        table.add(startButton).size(200, 60).padBottom(10).center();
        table.row();
        table.add(loadGameButton).size(200, 60).padBottom(10).center();
        table.row();
        table.add(optionsButton).size(200, 60).padBottom(10).center();
        table.row();
        table.add(exitButton).size(200, 60).center();

        // Dodanie tabeli do sceny
        stage.addActor(table);
    }

    private void setupButtonScaling(ImageButton button) {
        button.setTransform(true); // Umożliwia transformacje (skalowanie, rotacje)
        button.setOrigin(button.getWidth() / 2, button.getHeight() / 2); // Ustaw środek jako punkt odniesienia
    }

    private void addHoverAnimation(ImageButton button) {
        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.scaleTo(1.2f, 1.2f, 0.2f)); // Powiększenie przycisku
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f)); // Powrót do oryginalnego rozmiaru
            }
        });
    }

    @Override
    public void render(float delta) {
        // Czyszczenie ekranu
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Narysuj tło
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.end();

        // Rysowanie sceny
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Dostosowanie widoku do zmiany rozmiaru okna
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        // Czyszczenie zasobów sceny
        stage.dispose();
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}
