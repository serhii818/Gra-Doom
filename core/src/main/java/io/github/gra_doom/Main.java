package io.github.gra_doom;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.entity.*;

public class Main extends ApplicationAdapter {
    Map selectedMap;

    // test data section
    Renderer dr;
    Renderer rc;
    Renderer dov;
    // -----------------

    Player cam;
    KeyboardController keyboardController;
    MapEditorController MapEditorController;

    /**
     * Initialization method
     */
    @Override
    public void create() {

        // preparing window renderers
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
        int mapWidth = 24;
        int mapHeight = 24;
        
        cam = Player.makePlayer();
        selectedMap = MapEditor.loadMap("map.json", mapWidth, mapHeight, "file.ser");
        //selectedMap = MapEditor.loadMap("map.json", mapWidth, mapHeight, "file.ser");
        
        
        Gdx.graphics.setWindowedMode(840, 840);
        dr = new DebugRenderer(35, selectedMap.arr[0].length * 35, selectedMap.arr.length * 35, textures);
        //dr = new DebugRenderer(20, 800, 400, textures);
        rc = new RayCaster(800, 600, textures);

        dr.setMode(Renderer.DrawMode.FULL_WINDOW);
        //dov = new DebugOverlay(1600, 800);
        //dr.setMode(Renderer.DrawMode.CORNER_UL);
        rc.setMode(Renderer.DrawMode.FULL_WINDOW);
        //dov.setMode(Renderer.DrawMode.FULL_WINDOW);
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
        // preparing player
        cam = Player.makePlayer();
        cam.applyDamage(40);
        Projectile p = new Projectile(new Vector2(0, 0), 0.5f, "bullet/b2.png", 5,25, true);
        p.setTransforms(3, 3, 0);
        Weapon pw = new ProjectileWeapon(10, AmmoType.PISTOL, p);
        //Weapon pw = new HitScanWeapon(30, AmmoType.PISTOL, 25);
        cam.setWeapon(pw);


        //keyboardController = new KeyboardController(selectedMap.getPlayer(), selectedMap);
        //Gdx.input.setInputProcessor(keyboardController);



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
        //dov.viewport.update(width, height, true);
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


        dr.render(selectedMap);

        //rc.render(selectedMap);
        //dr.render(selectedMap);
        //dov.render(selectedMap);

    }

    /**
     * clean up and destroying resourses
     */
    @Override
    public void dispose() {
    }
}
