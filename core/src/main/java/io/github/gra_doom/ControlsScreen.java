package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ControlsScreen implements Screen {

    private static final int MENU_WIDTH = 780;
    private static final int MENU_HEIGHT = 480;

    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;

    @Override
    public void show() {
        // Tworzenie widoku z zachowaniem proporcji
        stage = new Stage(new FitViewport(MENU_WIDTH, MENU_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        batch = new SpriteBatch();
        backgroundTexture = new Texture("Menu/doommenu.jpg"); // Tło ekranu

        // Ładowanie tekstur
        Texture controlSettingsTexture = new Texture("texts/Control-settings.png");
        Texture moveTexture = new Texture("texts/move.png");
        Texture cameraTexture = new Texture("texts/camera.png");
        Texture shootTexture = new Texture("texts/shoot.png");
        Texture debugTexture = new Texture("texts/debug.png");
        Texture exitTexture = new Texture("texts/exit.png");

        // Tworzenie elementów graficznych
        Image controlSettingsImage = new Image(new TextureRegionDrawable(controlSettingsTexture));
        Image moveImage = new Image(new TextureRegionDrawable(moveTexture));
        Image cameraImage = new Image(new TextureRegionDrawable(cameraTexture));
        Image shootImage = new Image(new TextureRegionDrawable(shootTexture));
        Image debugImage = new Image(new TextureRegionDrawable(debugTexture));

        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(exitTexture));
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Powrót do OptionsScreen
                ((Game) Gdx.app.getApplicationListener()).setScreen(new OptionsScreen());
            }
        });

        // Tworzenie tabeli
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // Dodawanie elementów do tabeli
        table.add(controlSettingsImage).padBottom(20).center();
        table.row();
        table.add(moveImage).padBottom(10).center();
        table.row();
        table.add(cameraImage).padBottom(10).center();
        table.row();
        table.add(shootImage).padBottom(10).center();
        table.row();
        table.add(debugImage).padBottom(10).center();
        table.row();
        table.add(exitButton).padBottom(10).center();

        stage.addActor(table);

        // Dodawanie tła
        Image backgroundImage = new Image(new TextureRegionDrawable(backgroundTexture));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        backgroundImage.toBack(); // Tło zawsze za elementami
    }

    @Override
    public void render(float delta) {
        // Czyszczenie ekranu
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Aktualizacja i rysowanie sceny
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Aktualizacja widoku z zachowaniem proporcji
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
