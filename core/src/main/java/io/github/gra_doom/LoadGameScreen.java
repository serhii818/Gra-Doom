package io.github.gra_doom;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

public class LoadGameScreen implements Screen {

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

        // Styl etykiety
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        // Etykieta na ekranie
        Label loadGameLabel = new Label("Wczytywanie gry...", labelStyle);
        loadGameLabel.setFontScale(2);

        // Dodanie etykiety do tabeli
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        table.add(loadGameLabel).expand().center();

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
