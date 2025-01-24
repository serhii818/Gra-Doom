package io.github.gra_doom;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Buttons {

    private static final float DEFAULT_WIDTH = 150;  // Domyślny szerokość przycisku
    private static final float DEFAULT_HEIGHT = 50; // Domyślna wysokość przycisku

    /**
     * Tworzy przycisk z podaną teksturą i domyślnymi animacjami.
     *
     * @param texture Tekstura dla przycisku
     * @return Sformatowany przycisk
     */
    public static ImageButton create(Texture texture) {
        // Stwórz przycisk
        ImageButton button = new ImageButton(new TextureRegionDrawable(texture));

        // Ustaw domyślny rozmiar i punkt odniesienia
        button.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        button.setTransform(true); // Umożliwia skalowanie
        button.setOrigin(button.getWidth() / 2, button.getHeight() / 2); // Punkt odniesienia w środku

        // Dodaj animacje hover
        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.scaleTo(1.2f, 1.2f, 0.2f)); // Powiększenie
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.addAction(Actions.scaleTo(1.0f, 1.0f, 0.2f)); // Powrót do pierwotnego rozmiaru
            }
        });

        return button;
    }
}
