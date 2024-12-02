package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ControlsScreen implements Screen {

    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private BitmapFont font;

    @Override
    public void show() {
        // Tworzymy scenę i batcha
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        batch = new SpriteBatch();
        backgroundTexture = new Texture("doommenu.jpg"); // Zmień na odpowiednią grafikę

        // Załaduj font
        font = new BitmapFont();
        font.getData().setScale(2);

        // Styl przycisków
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;

        // Styl etykiety
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        // Etykieta na ekranie
        Label controlsLabel = new Label("Control", labelStyle);
        controlsLabel.setFontScale(2);

        // Tworzymy przyciski do sterowania
        TextButton backToMenuButton = new TextButton("Exit", style);

        // Dodanie listenera do przycisku
        backToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Powrót do opcji
                ((Game) Gdx.app.getApplicationListener()).setScreen(new OptionsScreen());
            }
        });

        // Tworzenie tabeli i dodanie przycisków
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // Dodanie przycisków do tabeli
        table.add(controlsLabel).padBottom(50).colspan(2);
        table.row().padBottom(20);
        table.add(backToMenuButton).fillX().uniformX();

        // Dodanie tabeli do sceny
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // Rysowanie tła i interfejsu
        // Rysowanie tła i interfejsu
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Oblicz proporcje tła i dopasuj je do ekranu
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float textureWidth = backgroundTexture.getWidth();
        float textureHeight = backgroundTexture.getHeight();

        // Zachowanie proporcji obrazu tła
        float scaleX = screenWidth / textureWidth;
        float scaleY = screenHeight / textureHeight;
        float scale = Math.max(scaleX, scaleY); // Użyj większego skalowania, aby pasować do ekranu

        // Oblicz nowe wymiary tła
        float newWidth = textureWidth * scale;
        float newHeight = textureHeight * scale;

        // Oblicz pozycję tła, aby wyśrodkować je na ekranie
        float x = (screenWidth - newWidth) / 2;
        float y = (screenHeight - newHeight) / 2;

        batch.begin();
        batch.draw(backgroundTexture, x, y, newWidth, newHeight);  // Rysuj tło
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
