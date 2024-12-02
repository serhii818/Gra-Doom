package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;





public class OptionsScreen implements Screen {

    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private BitmapFont font;
    private Music backgroundMusic;

    @Override
    public void show() {
        // Tworzymy SpriteBatch
        batch = new SpriteBatch();

        // Załaduj tło
        backgroundTexture = new Texture("doommenu.jpg");  // Załaduj obrazek tła

        // Załaduj czcionkę
        font = new BitmapFont();
        font.getData().setScale(2);

        // Załaduj muzykę
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("DoomMenu.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Tworzymy styl dla przycisków
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;

        // Tworzymy styl dla etykiet i przypisujemy font
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;  // Ważne - przypisanie czcionki do stylu

        // Tworzymy etykietę
        Label optionsLabel = new Label("Options", labelStyle);
        optionsLabel.setFontScale(2); // Ustaw rozmiar czcionki dla etykiety

        // Tworzymy przyciski
        TextButton soundSettingsButton = new TextButton("Sound Settings", textButtonStyle);
        TextButton controlsButton = new TextButton("Control Settings", textButtonStyle);
        TextButton backToMenuButton = new TextButton("Exit to menu", textButtonStyle);

        // Dodanie akcji do przycisków
        soundSettingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new SoundSettingsScreen());
            }
        });

        controlsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new ControlsScreen());
            }
        });

        backToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            }
        });

        // Utwórz tabelę do rozmieszczenia przycisków
        Table table = new Table(); // Tworzymy tabelę
        table.center();
        table.setFillParent(true);

        // Dodaj przyciski i etykietę do tabeli
        table.add(optionsLabel).padBottom(50).colspan(2);
        table.row().padBottom(20);
        table.add(soundSettingsButton).fillX().uniformX().padBottom(20);
        table.row().pad(10, 0, 10, 0);
        table.add(controlsButton).fillX().uniformX().padBottom(20);
        table.row().pad(10, 0, 10, 0);
        table.add(backToMenuButton).fillX().uniformX();

        // Dodaj tabelę do sceny
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
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
        // Zwalniamy zasoby
        stage.dispose();
        batch.dispose();  // Zwalniamy SpriteBatch
        backgroundTexture.dispose();
        backgroundMusic.dispose();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
