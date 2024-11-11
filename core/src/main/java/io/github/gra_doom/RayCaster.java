package io.github.gra_doom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

/**
 * renderer map of the screen
 */
public class RayCaster extends Renderer implements Disposable {

    public RayCaster(int width, int height) {
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

    private void  rayCast(Map map) {
        GameCamera cam = map.cam;
        map.cam.dir.rotateDeg(0.5f);
        map.cam.plane.rotateDeg(0.5f);

        for (int x = 0; x < width; x++) {
            float cameraX = 2*x / (float)width -1;
            float rayDirX = cam.dir.x + cam.plane.x* cameraX;
            float rayDirY = cam.dir.y + cam.plane.y* cameraX;

            float deltaDistX = (rayDirX == 0) ? 1e30f : Math.abs(1/ rayDirX);
            float deltaDistY = (rayDirY == 0) ? 1e30f : Math.abs(1/ rayDirY);

            int mapX = (int)(cam.pos.x);
            int mapY = (int)(cam.pos.y);

            float sideDistX;
            float sideDistY;

            float perpWallDist;
            int lineHeight;

            int stepX;
            int stepY;

            int hit = 0;
            int side = 0;

            if (rayDirX < 0)
            {
                stepX = -1;
                sideDistX = (cam.pos.x - mapX) * deltaDistX;
            }
            else
            {
                stepX = 1;
                sideDistX = (mapX + 1.0f - cam.pos.x) * deltaDistX;
            }
            if (rayDirY < 0)
            {
                stepY = -1;
                sideDistY = (cam.pos.y - mapY) * deltaDistY;
            }
            else
            {
                stepY = 1;
                sideDistY = (mapY + 1.0f - cam.pos.y) * deltaDistY;
            }


            while (hit == 0) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }

                if (map.arr[mapY][mapX] > 0) hit = 1;
            }

            if (side == 0)  perpWallDist = (sideDistX - deltaDistX);
            else            perpWallDist = (sideDistY - deltaDistY);

            lineHeight = (int)(height/perpWallDist);

            int drawStart = -lineHeight/2 + height/2;
            int drawEnd = lineHeight/2 + height/2;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            if (side == 0) shapeRenderer.setColor(Color.RED);
            else shapeRenderer.setColor(Color.ORANGE);
            shapeRenderer.line(x, drawStart, x, drawEnd);
            shapeRenderer.end();


        }

    }

    @Override
    public void renderFrame(Map map) {
        frameBuffer.begin();
        clearScreen();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(0, 0, frameBuffer.getWidth()*2, frameBuffer.getHeight());
        shapeRenderer.end();


        rayCast(map);
        frameBuffer.end();
    }

    @Override
    public void render(Map map) {
        renderFrame(map);
        drawFrame();
    }

    @Override
    public void dispose() {

    }
}
