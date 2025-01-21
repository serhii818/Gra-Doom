package io.github.gra_doom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.gra_doom.entity.Entity;

/**
 * Debug renderer for viewing actual 2D scene with zoom support.
 */
public class DebugRenderer extends Renderer {
    private int cellSize;
    private float zoom = 1.0f; // Default zoom level

    public DebugRenderer(int cellSize, int width, int height) {
        super(width, height);
        this.cellSize = cellSize;
    }


    public void setZoom(float zoom) {
        this.zoom = Math.max(0.1f, zoom); // Prevent zooming out too much
    }

    
    public float getZoom() {
        return zoom;
    }
    

    private void drawMap(Map map) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.CYAN);
        for (int y = 0; y < map.arr.length; y++) {
            for (int x = 0; x < map.arr[y].length; x++) {
                if (map.arr[y][x] != 0) { // Only render non-zero tiles
                    float scaledCellSize = cellSize * zoom; // Adjust size based on zoom
                    float posx = (x * scaledCellSize);
                    float posy = (y * scaledCellSize);
                    shapeRenderer.rect(posx, posy, scaledCellSize, scaledCellSize);
                }
            }
        }
        shapeRenderer.end();
    }

    /**
     * Render the frame in the framebuffer before displaying on screen.
     */
    @Override
    public void renderFrame(Map map) {
        frameBuffer.begin();
        clearScreen();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        float scaledWidth = width * zoom;  // Adjust width for zoom
        float scaledHeight = height * zoom; // Adjust height for zoom
        shapeRenderer.rect(0, 0, scaledWidth, scaledHeight);
        shapeRenderer.end();

        drawMap(map);
        frameBuffer.end();
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
