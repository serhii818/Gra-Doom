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
import com.badlogic.gdx.Preferences;

public class SoundSettingsScreen implements Screen {

    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;

    private Music backgroundMusic;
    private boolean isMusicEnabled = true;  // Początkowo włączona muzyka
    private boolean isSoundEnabled = true;  // Początkowo włączony dźwięk

    private Preferences prefs;  // Preferences do przechowywania ustawień

    @Override
    public void show() {
        prefs = Gdx.app.getPreferences("SoundSettings");  // Inicjalizacja Preferences
        isMusicEnabled = prefs.getBoolean("musicEnabled", true);  // Odczytanie ustawienia muzyki
        isSoundEnabled = prefs.getBoolean("soundEnabled", true);  // Odczytanie ustawienia dźwięku

        // Tworzymy SpriteBatch
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("doommenu.jpg"));  // Załaduj obrazek tła

        // Używamy domyślnej czcionki LibGDX
        font = new BitmapFont(); // Domyślna czcionka
        font.getData().setScale(2); // Ustaw rozmiar czcionki

        // Załaduj muzykę
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("DoomMenu.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Tworzymy styl dla etykiet
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font; // Przypisz domyślną czcionkę do stylu

        // Tworzymy etykietę
        Label soundSettingsLabel = new Label("Sound Settings", labelStyle);
        soundSettingsLabel.setFontScale(2); // Ustaw rozmiar czcionki dla etykiety

        // Tworzymy style dla przycisków
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font; // Używamy domyślnej czcionki dla przycisków

        // Tworzymy przyciski w formie checklisty
        TextButton toggleMusicButton = new TextButton(getMusicButtonText(), buttonStyle);
        TextButton toggleSoundButton = new TextButton(getSoundButtonText(), buttonStyle);
        TextButton backToMenuButton = new TextButton("Exit to options", buttonStyle);

        // Listener do przycisku muzyki
        toggleMusicButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isMusicEnabled = !isMusicEnabled;  // Przełącz stan muzyki
                toggleMusicButton.setText(getMusicButtonText());  // Zaktualizuj tekst przycisku
                if (isMusicEnabled) {
                    backgroundMusic.play();
                } else {
                    backgroundMusic.pause();
                }

                // Zapisz zmieniony stan w Preferences
                prefs.putBoolean("musicEnabled", isMusicEnabled);
                prefs.flush();  // Zapisz zmiany
            }
        });

        // Listener do przycisku dźwięku
        toggleSoundButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isSoundEnabled = !isSoundEnabled;  // Przełącz stan dźwięku
                toggleSoundButton.setText(getSoundButtonText());  // Zaktualizuj tekst przycisku

                // Zapisz zmieniony stan w Preferences
                prefs.putBoolean("soundEnabled", isSoundEnabled);
                prefs.flush();  // Zapisz zmiany
            }
        });

        // Listener do powrotu do menu
        backToMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new OptionsScreen());
            }
        });

        // Tworzymy tabelę
        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        // Dodaj etykietę i przyciski do tabeli
        table.add(soundSettingsLabel).padBottom(50).row();
        table.add(toggleMusicButton).padBottom(20).row();
        table.add(toggleSoundButton).padBottom(20).row();
        table.add(backToMenuButton).padTop(50);

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
        stage.dispose();
        batch.dispose();
        backgroundMusic.dispose();
        backgroundTexture.dispose();
    }

    @Override
    public void dispose() {
        // Czyszczenie zasobów
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    // Metoda pomocnicza do generowania tekstu przycisku muzyki
    private String getMusicButtonText() {
        return isMusicEnabled ? "On Musics" : "Off musics";
    }

    // Metoda pomocnicza do generowania tekstu przycisku dźwięku
    private String getSoundButtonText() {
        return isSoundEnabled ? "On sounds" : "Off sounds";
    }
}
