package io.github.gra_doom;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Color;

public class MenuScreen implements Screen {

    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;

    @Override
    public void show() {
        // Tworzenie sceny i batcha
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        batch = new SpriteBatch();
        backgroundTexture = new Texture("doommenu.jpg");

        // Dodanie przycisków i listenerów
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        TextButton startButton = new TextButton("Rozpocznij Gre", new TextButton.TextButtonStyle());
        TextButton exitButton = new TextButton("Wyjdź z Gry", new TextButton.TextButtonStyle());

        startButton.getStyle().fontColor = Color.WHITE;
        exitButton.getStyle().fontColor = Color.WHITE;

        // Dodanie listenerów
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Zmieniamy ekran na GameScreen
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Zamyka aplikację
            }
        });

        // Dodanie przycisków do tabeli
        table.add(startButton).fillX().uniformX().padBottom(20);
        table.row().pad(10, 0, 10, 0);
        table.add(exitButton).fillX().uniformX();

        // Dodanie tabeli do sceny
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
        batch.dispose();
        backgroundTexture.dispose();
    }


    @Override
    public void dispose() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
