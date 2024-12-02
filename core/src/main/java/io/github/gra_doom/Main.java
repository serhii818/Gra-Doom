package io.github.gra_doom;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Main extends ApplicationAdapter {
    Map selectedMap;

    // test data section
    Renderer dr;
    Renderer rc;
    // -----------------

    Player cam;
    KeyboardController keyboardController;

    /**
     * Initialization method
     */
    @Override
    public void create() {
        // test
        // preparing window renderers, Game camera and map
        Texture[] textures = new Texture[8];
        String[] texture_path = {
            "eagle",
            "bluestone",
            "colorstone",
            "greystone",
            "mossy",
            "purplestone",
            "redbrick",
            "wood"
        };
        for (int i = 0; i < texture_path.length; i++) {
            textures[i] = new Texture(Gdx.files.internal("pics/" + texture_path[i] + ".png"));

        }

        Gdx.graphics.setWindowedMode(1600, 800);
        dr = new DebugRenderer(20, 800, 400);
        rc = new RayCaster(800, 600, textures);
        dr.setMode(Renderer.DrawMode.CORNER_UL);
        rc.setMode(Renderer.DrawMode.FULL_WINDOW);
        ((RayCaster)rc).setDrawFloorEnabled(false);

        cam = Player.makePlayer();
        keyboardController = new KeyboardController(cam);

        Gdx.input.setInputProcessor(keyboardController);

        int[][] arr = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,2,2,2,2,2,0,0,0,0,3,0,3,0,3,0,0,0,1},
            {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,3,0,0,0,3,0,0,0,1},
            {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,2,2,0,2,2,0,0,0,0,3,0,3,0,3,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,4,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,0,0,0,5,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,4,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,4,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };
        selectedMap = new Map(arr, cam);

        // ----
    }

    /**
     * updates renderer parameter according to window size for proper rendering
     * @param width - width of the window
     * @param height - height of the window
    */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        rc.viewport.update(width, height, true);
        dr.viewport.update(width, height, true);
    }

    /**
     * main loop
     */
    @Override
    public void render() {

        // listen to inputs
        // update gamestate
        // render
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        selectedMap.update();

        rc.render(selectedMap);
        //dr.render(selectedMap);
    }

    /**
     * clean up and destroying resourses
     */
    @Override
    public void dispose() {
    }
}
