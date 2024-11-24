package io.github.gra_doom;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.io.File;

public class Main extends ApplicationAdapter {
    Map selectedMap;

    // test data section
    Renderer dr;
    Renderer rc;
    // -----------------

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

        Gdx.graphics.setWindowedMode(1800, 1000);
        dr = new DebugRenderer(20, 800, 400);
        rc = new RayCaster(800, 600, textures);
        dr.setMode(Renderer.DrawMode.CORNER_UL);
        rc.setMode(Renderer.DrawMode.FULL_WINDOW);

        GameCamera cam = new GameCamera(new Vector2(14.4f, 10.8f), new Vector2(-1, 0), new Vector2(0, 0.66f));
        cam.dir.rotateDeg(-90);
        cam.plane.rotateDeg(-90);

        int[][] arr = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,2,2,2,2,2,0,0,0,0,3,0,3,0,3,0,0,0,1},
            {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,3,0,0,0,3,0,0,0,1},
            {1,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,2,2,0,2,2,0,0,0,0,4,0,3,0,3,0,0,0,1},
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
