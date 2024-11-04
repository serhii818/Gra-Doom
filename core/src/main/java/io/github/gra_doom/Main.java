package io.github.gra_doom;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Game;


public class Main extends Game {



    /**
     * Initialization method
     */
    @Override
    public void create() {
    setScreen(new MenuScreen());
    }

    /**
     * main loop
     */
    @Override
    public void render() {

    }

    /**
     * clean up and destroying resourses
     */
    @Override
    public void dispose() {

    }
}
