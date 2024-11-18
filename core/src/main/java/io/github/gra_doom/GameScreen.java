package io.github.gra_doom;  // Upewnij się, że pakiet jest zgodny z folderem

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;  // Importowanie klasy Gdx
import com.badlogic.gdx.graphics.GL20;  // Importowanie klasy GL20, która pozwala na czyszczenie ekranu
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {

    private SpriteBatch batch;

    @Override
    public void show() {
        batch = new SpriteBatch();  // Inicjalizacja zasobów
    }

    @Override
    public void render(float delta) {
        // Czyszczenie ekranu i rysowanie
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        // Tutaj można dodać kod rysujący obiekty w grze
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Dostosowanie ekranu
    }

    @Override
    public void hide() {
        batch.dispose();  // Zwalnianie zasobów
    }

    @Override
    public void dispose() {
        // Zwalnianie zasobów
    }

    @Override
    public void pause() {
        // Pauza w grze
    }

    @Override
    public void resume() {
        // Wznowienie gry
    }
}
