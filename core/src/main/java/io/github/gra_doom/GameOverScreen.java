package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Ekran Game Over. Wyświetla zdobyty wynik i dwa przyciski: Reset oraz Back to Menu.
 */
public class GameOverScreen implements Screen {

    private Stage stage;
    private SpriteBatch batch;

    private int finalScore; // wynik, jaki gracz osiągnął w momencie śmierci

    public GameOverScreen(int score) {
        this.finalScore = score;
    }

    @Override
    public void show() {
        // Inicjalizujemy stage i batch
        stage = new Stage(new FitViewport(800, 600));
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        // Główna tabela
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Styl dla napisów (prosta czcionka)
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont(); // defaultowa czcionka

        // Napisy
        final Label gameOverLabel = new Label("GAME OVER", style);
        final Label scoreLabel = new Label("Your Score: " + finalScore, style);

        // Przyciski
        Texture resetTexture = new Texture(Gdx.files.internal("texts/reset.png"));
        ImageButton resetButton = new ImageButton(new TextureRegionDrawable(resetTexture));

        Texture menuTexture = new Texture(Gdx.files.internal("texts/back-menu.png"));
        ImageButton menuButton = new ImageButton(new TextureRegionDrawable(menuTexture));

        // Listener do Reset
        resetButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main game = (Main) Gdx.app.getApplicationListener();
                // 1. resetujemy stan gry
                game.resetGame();
                // 2. wracamy do ekranu gry
                game.setScreen(game.gameScreen);
                return true;
            }
        });

        // Listener do Back to menu
        menuButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main game = (Main) Gdx.app.getApplicationListener();
                // Powrót do menu
                game.inMenu = true;
                game.setScreen(game.menuScreen);
                return true;
            }
        });

        // Układ w tabeli
        root.add(gameOverLabel).pad(15);
        root.row();
        root.add(scoreLabel).pad(15);
        root.row();
        root.add(resetButton).pad(15);
        root.row();
        root.add(menuButton).pad(15);
    }

    @Override
    public void render(float delta) {
        // czyszczenie ekranu
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Rysowanie sceny
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
        stage.dispose();
        batch.dispose();
    }
}
