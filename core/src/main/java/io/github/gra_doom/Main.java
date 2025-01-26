package io.github.gra_doom;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    MapEditorController MapEditorController;

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
        //Wybieramy wielkosc mapy ( wpisanie wymiaru o wartosci 0 powoduje wczytanie mapy z pliku)
        int mapWidth = 0;
        int mapHeight = 24;
        
        cam = Player.makePlayer();
        int[][] arr = MapEditor.loadMap("map.json", mapWidth, mapHeight);
        selectedMap = new Map(arr, cam);
        
        
        Gdx.graphics.setWindowedMode(840, 840);
        dr = new DebugRenderer(35, selectedMap.arr[0].length * 35, selectedMap.arr.length * 35, textures, cam);
        //dr = new DebugRenderer(20, 800, 400, textures, cam);
        rc = new RayCaster(800, 600, textures);
        dr.setMode(Renderer.DrawMode.FULL_WINDOW);
        rc.setMode(Renderer.DrawMode.FULL_WINDOW);
        ((RayCaster)rc).setDrawFloorEnabled(false);

        
        //Edytor mapy - wybieramy MapEditorController, tryb gry - wybieramy keyboardController 
        //keyboardController = new KeyboardController(cam);
        int tileWidth = Gdx.graphics.getHeight() / mapHeight;
        int tileHeight = Gdx.graphics.getWidth() / mapHeight;
        MapEditorController = new MapEditorController(dr, tileWidth, tileHeight, selectedMap);

        
        //Edytor mapy - wybieramy MapEditorController, tryb gry - wybieramy keyboardController
        //Gdx.input.setInputProcessor(keyboardController);
        Gdx.input.setInputProcessor(MapEditorController);
        
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

        //rc.render(selectedMap);
        dr.render(selectedMap);
    }

    /**
     * clean up and destroying resourses
     */
    @Override
    public void dispose() {
    }
}
