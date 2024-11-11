package io.github.gra_doom;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class DebugRenderer extends Renderer {
    int cellSize;
    //size of frame buffer


    public DebugRenderer(int cellSize, int width, int height) {
        this.cellSize = cellSize;
        this.width = width;
        this.height = height;

        winCamera = new OrthographicCamera(width, height);
        winCamera.position.set(width/2f, height/2f, 0);
        winCamera.update();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(winCamera.combined);
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        batch = new SpriteBatch();
        setMode(DrawMode.FULL_WINDOW);
    }

    private void drawMap(Map map) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.CYAN);
        for (int y = 0; y < map.arr.length; y++) {
            for (int x = 0; x < map.arr[y].length; x++) {
                if (map.arr[y][x] != 0) {
                    shapeRenderer.rect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }
        shapeRenderer.end();
    }

    private void drawCam(GameCamera cam) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(cam.pos.x * cellSize, cam.pos.y * cellSize, cellSize/4.0f);
        shapeRenderer.line(cam.pos.x * cellSize, cam.pos.y * cellSize, (cam.pos.x + (cam.dir.x + cam.plane.x)*3)*cellSize,
            (cam.pos.y + (cam.dir.y + cam.plane.y)*3)*cellSize);
        shapeRenderer.line(cam.pos.x * cellSize, cam.pos.y * cellSize, (cam.pos.x + (cam.dir.x - cam.plane.x)*3)*cellSize,
            (cam.pos.y + (cam.dir.y - cam.plane.y)*3)*cellSize);
        shapeRenderer.end();
    }


    @Override
    public void renderFrame(Map map) {
        frameBuffer.begin();
        clearScreen();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(0, 0, width, height);
        shapeRenderer.end();

        drawMap(map);
        drawCam(map.cam);
        frameBuffer.end();

    }

    @Override
    public void render(Map map) {
        renderFrame(map);
        drawFrame();

    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        frameBuffer.dispose();
        frame.dispose();
        batch.dispose();
    }
}
