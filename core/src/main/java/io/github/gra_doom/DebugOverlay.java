package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DebugOverlay extends Renderer{
    private BitmapFont font;
    private float font_height;
    private int messageCounter = 0;

    public DebugOverlay(int width, int height) {
        super(width, height);
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font_height = font.getLineHeight();
        batch.setProjectionMatrix(winCamera.combined);

    }

    @Override
    public void render(Map map) {
        //renderFrame(map);
        batch.begin();

        print_next("Health:"+map.getPlayer().getHealth());
        print_next("Ammo:"+map.getPlayer().isShooting());
        print_next("Weapon:"+map.getPlayer().getWeapon().toString());
        print_next("Player:"+map.getPlayer().toString());
        print_next("MAP_enities:" + map.entities.size());

        messageCounter = 0;
        batch.end();
        drawFrame();

        

    }

    @Override
    public void renderFrame(Map map) {
//        frameBuffer.begin();
//        Gdx.gl.glClearColor(0, 0, 0, 0);
//        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
//
//        batch.begin();
//
//        print_next("Health:"+map.getPlayer().getHealth());
//        print_next("Ammo:"+map.getPlayer().isShooting());
//        print_next("Weapon:"+map.getPlayer().getWeapon().toString());
//        print_next("Player:"+map.getPlayer().toString());
//        print_next("MAP_enities:" + map.entities.size());
//
//        messageCounter = 0;
//        batch.end();
//        frameBuffer.end();
    }

    private void print_next(String message) {
        font.draw(batch, message, 2, renderHeight-font_height*messageCounter);
        messageCounter++;
    }
}
