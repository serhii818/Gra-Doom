package io.github.gra_doom;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import io.github.gra_doom.entity.Entity;


/**
 * Debug renderer for viewing actual 2D scene with zoom support.
 */
public class DebugRenderer extends Renderer {
    private int cellSize;
    private float zoom = 1.0f; // Default zoom level
    Texture[] textures_;
    Texture entitytext;

    

    public DebugRenderer(int cellSize, int width, int height, Texture[] textures_) {
        super(width, height);
        this.cellSize = cellSize;
        this.textures_ = textures_;
    }


    public void setZoom(float zoom) {
        this.zoom = Math.max(0.1f, zoom); // Prevent zooming out too much
    }

    
    public float getZoom() {
        return zoom;
    }
    

    private void drawMap(Map map, Player cam) {
    	batch.begin();
        for (int y = 0; y < map.arr.length; y++) {
            for (int x = 0; x < map.arr[y].length; x++) {
                if (map.arr[y][x] != 0) {
                	TextureRegion tileRegion = new TextureRegion(textures_[map.arr[y][x]-1], 0, 0, 64, 64);
                    float scaledCellSize = cellSize * zoom;
                    float posx = (x * scaledCellSize);
                    float posy = (y * scaledCellSize);
                    batch.draw(tileRegion, posx, posy, scaledCellSize, scaledCellSize);
                }
            }
        }
        
        
        TextureRegion playerRegion = new TextureRegion(textures_[0], 0, 0, 64, 64);
        batch.draw(playerRegion, cam.pos.x * cellSize * zoom, cam.pos.y * cellSize * zoom, cellSize * zoom, cellSize * zoom);
        
        for(Entity entity : map.entities) {
        	entitytext = new Texture(Gdx.files.internal(entity.spritePath));
        	int textureX;
        	int textureY;
        	int regionWidth;
        	int regionHeight;
        	
        	switch (entity.spritePath) {
        		case "sprites/Imp.png":
        			textureX = 17;
        			textureY = 9;
                	regionWidth = 15;
                	regionHeight = 15;
        			break;
        		case "sprites/Demon.png":
        			textureX = 8;
        			textureY = 16;
                	regionWidth = 30;
                	regionHeight = 30;
        			break;
        		case "sprites/Zombie.png":
        			textureX = 8;
        			textureY = 0;
                	regionWidth = 20;
                	regionHeight = 20;
        			break;
        		default:
        			textureX = 0;
        			textureY = 0;
                	regionWidth = 28;
                	regionHeight = 41;
        			break;
        	}
        	
        	TextureRegion entityrRegion = new TextureRegion(entitytext, textureX, textureY, regionWidth, regionHeight);
        	batch.draw(entityrRegion, entity.pos.x * cellSize * zoom, entity.pos.y * cellSize * zoom, cellSize * zoom, cellSize * zoom);
        }
        batch.end();
    }

    /**
     * Render the frame in the framebuffer before displaying on screen.
     */
    @Override
    public void renderFrame(Map map) {
        //frameBuffer.begin();
        //clearScreen();


    	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        float scaledWidth = width * zoom;  // Adjust width for zoom
        float scaledHeight = height * zoom; // Adjust height for zoom
        shapeRenderer.rect(0, 0, scaledWidth, scaledHeight);
        shapeRenderer.end();

        drawMap(map, map.player);
        //frameBuffer.end();

    }


    @Override
    public void render(Map map) {
        renderFrame(map);
        drawFrame();
    }


    @Override
    public void dispose() {
        super.dispose();
    }
}
