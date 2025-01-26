package io.github.gra_doom;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.entity.*;

import com.badlogic.gdx.Game;


public class Main extends Game {
    Map selectedMap;


    // test data section
    Renderer dr;
    Renderer rc;
    Renderer dov;
    // -----------------

    Player cam;
    KeyboardController keyboardController;
    public Screen gameScreen;
    public Screen menuScreen;
    boolean inMenu = true;


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

        Gdx.graphics.setWindowedMode(1600, 800);
        dr = new DebugRenderer(20, 800, 400);
        rc = new RayCaster(800, 600, textures);
        dov = new DebugOverlay(1600, 800);
        dr.setMode(Renderer.DrawMode.CORNER_UL);
        rc.setMode(Renderer.DrawMode.FULL_WINDOW);
        dov.setMode(Renderer.DrawMode.FULL_WINDOW);
        ((RayCaster)rc).setDrawFloorEnabled(false);

        // preparing player
        cam = Player.makePlayer();
        cam.applyDamage(40);
        Projectile p = new Projectile(new Vector2(0, 0), 0.5f, "bullet/b2.png", 5,25, true);
        p.setTransforms(3, 3, 0);
        Weapon pw = new ProjectileWeapon(10, AmmoType.PISTOL, p);
        //Weapon pw = new HitScanWeapon(30, AmmoType.PISTOL, 25);
        cam.setWeapon(pw);

        // prepare map
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

        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.IMP, new Vector2(2, 2)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.IMP, new Vector2(20, 20)));

        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.ZOMBIE, new Vector2(10, 10)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.ZOMBIE, new Vector2(11, 11)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.ZOMBIE, new Vector2(12, 10)));
        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.ZOMBIE, new Vector2(10, 20)));

        selectedMap.addEntity(Enemy.makeEnemy(Enemy.Type.DEMON, new Vector2(18, 21)));

        selectedMap.addEntity(PickUpItem.makeItem(new Vector2(2, 3), PickUpItem.Item.HEALTH25));
        selectedMap.addEntity(PickUpItem.makeItem(new Vector2(20, 21), PickUpItem.Item.HEALTH25));
        selectedMap.addEntity(PickUpItem.makeItem(new Vector2(10, 21), PickUpItem.Item.HEALTH50));
        selectedMap.addEntity(PickUpItem.makeItem(new Vector2(20, 11), PickUpItem.Item.HEALTH100));

        keyboardController = new KeyboardController(selectedMap.getPlayer(), selectedMap);
        //Gdx.input.setInputProcessor(keyboardController);

        gameScreen = getScreen();

        menuScreen = new MenuScreen();
        this.setScreen(menuScreen);
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
        dov.viewport.update(width, height, true);
    }

    /**
     * main loop
     */
    @Override
    public void render() {
        if (inMenu) super.render();
        else {


            // listen to inputs
            // update gamestate
            // render
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


            selectedMap.update();

            rc.render(selectedMap);
            //dr.render(selectedMap);
            dov.render(selectedMap);
        }
    }


    /**
     * clean up and destroying resourses
     */
    @Override
    public void dispose() {
        super.dispose();
    }
}
