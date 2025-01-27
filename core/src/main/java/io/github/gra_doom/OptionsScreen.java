package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class OptionsScreen implements Screen {

    public static final int MENU_WIDTH = 780;
    public static final int MENU_HEIGHT = 480;
    private static final float ANIMATION_DURATION = 0.5f;

    private Stage stage;
    private Texture backgroundTexture;

    @Override
    public void show() {
        initializeViewport();

        MusicManager.getInstance().playMusic();

        createButtons();
    }

    private void initializeViewport() {
        stage = new Stage(new FitViewport(MENU_WIDTH, MENU_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("Menu/doommenu.jpg"));
    }

    private void createButtons() {
        // Dodaj tło jako Image
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        backgroundImage.setZIndex(0); // Upewnij się, że jest na dole
        stage.addActor(backgroundImage);

        // Tworzenie przycisków
        Texture volumeTexture = new Texture(Gdx.files.internal("texts/volume-settings.png"));
        Texture controlTexture = new Texture(Gdx.files.internal("texts/control-settings.png"));
        Texture menuTexture = new Texture(Gdx.files.internal("texts/exit.png"));

        ImageButton volumeButton = Buttons.create(volumeTexture);
        ImageButton controlButton = Buttons.create(controlTexture);
        ImageButton menuButton = Buttons.create(menuTexture);


        controlButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                animateButtonsOffScreen(() -> {
                    // Wykonaj tutaj akcję po animacji, np. przejdź do ekranu ControlSettings
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new ControlsScreen());
                });
                return true;
            }
        });

        // Listener dla Menu (powrót do MenuScreen)
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

        // Tworzenie tabeli z przyciskami
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        table.add(volumeButton).padBottom(10).center();
        table.row();
        table.add(controlButton).padBottom(10).center();
        table.row();
        table.add(menuButton).center();

        stage.addActor(table);

        // Animacja pojawienia się tabeli
        table.setPosition(-MENU_WIDTH, table.getY());
        table.addAction(Actions.moveTo(0, table.getY(), ANIMATION_DURATION));
    }

    private void animateButtonsOffScreen(Runnable onComplete) {
        float screenWidth = stage.getViewport().getWorldWidth();

        for (Actor actor : stage.getActors()) {
            if (actor instanceof Table) {
                Table table = (Table) actor;
                for (Cell<?> cell : table.getCells()) {
                    Actor button = cell.getActor();
                    if (button != null) {
                        // Animacja przesunięcia przycisków w prawo poza ekran
                        button.addAction(Actions.moveTo(screenWidth + button.getWidth(), button.getY(), ANIMATION_DURATION));
                    }
                }
                // Dodanie akcji wywołującej callback po zakończeniu animacji
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

        if (stage != null) {
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
            stage.dispose();
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
