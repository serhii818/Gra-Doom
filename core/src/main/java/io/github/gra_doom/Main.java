package io.github.gra_doom;


import com.badlogic.gdx.Game;


public class Main extends Game {



    /**
     * Initialization method
     */
    @Override
    public void create() {
        this.setScreen(new MenuScreen());
    }

    /**
     * main loop
     */
    @Override
    public void render() {
        super.render();
    }
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
