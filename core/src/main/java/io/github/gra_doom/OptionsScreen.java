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
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class OptionsScreen implements Screen {

    private static final int SCREEN_WIDTH = 780;
    private static final int SCREEN_HEIGHT = 480;
    private static final float ANIMATION_DURATION = 0.5f;

    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;

    @Override
    public void show() {
        if (batch == null) {
            batch = new SpriteBatch();
        }
        stage = new Stage(new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("Menu/doommenu.jpg"));

        MusicManager.getInstance().playMusic();

        createButtons();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Dopasuj widok
    }

    private void createButtons() {
        Texture volumeTexture = new Texture(Gdx.files.internal("texts/volume-settings.png"));
        Texture controlTexture = new Texture(Gdx.files.internal("texts/control-settings.png"));
        Texture menuTexture = new Texture(Gdx.files.internal("texts/exit.png"));

        ImageButton volumeButton = Buttons.create(volumeTexture);
        ImageButton controlButton = Buttons.create(controlTexture);
        ImageButton menuButton = Buttons.create(menuTexture);

        menuButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                animateButtonsOffScreen(() -> {
                    // Przełącz na MenuScreen po zakończeniu animacji
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                });
                return true;
            }
        });

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        table.add(volumeButton).padBottom(10).center();
        table.row();
        table.add(controlButton).padBottom(10).center();
        table.row();
        table.add(menuButton).center();

        stage.addActor(table);

        table.setPosition(-SCREEN_WIDTH, table.getY());
        table.addAction(Actions.moveTo(0, table.getY(), ANIMATION_DURATION));
    }

    private void animateButtonsOffScreen(Runnable onComplete) {
        float screenWidth = Gdx.graphics.getWidth();

        for (Actor actor : stage.getActors()) {
            if (actor instanceof Table) {
                Table table = (Table) actor;
                for (Cell<?> cell : table.getCells()) {
                    Actor button = cell.getActor();
                    if (button != null) {
                        button.addAction(Actions.moveTo(screenWidth + button.getWidth(), button.getY(), ANIMATION_DURATION));
                    }
                }
                table.addAction(Actions.sequence(
                        Actions.delay(ANIMATION_DURATION),
                        Actions.run(onComplete)
                ));
            }
        }
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight); // Dynamiczne dopasowanie tła
        batch.end();

        if (stage != null) { // Dodajemy sprawdzenie istnienia stage
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
            stage.dispose(); // Usuwamy zasoby, ale nie ustawiamy stage na null
        }
    }
    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

}
