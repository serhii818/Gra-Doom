package io.github.gra_doom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

/**
 * renderer map of the screen
 */
public class RayCaster extends Renderer implements Disposable {
    Pixmap[] textures;

    int texW;
    int texH;
    Pixmap buffer;

    public RayCaster(int width, int height, Texture[] textures_) {
        this.width = width;
        this.height = height;
        this.textures = new Pixmap[textures_.length];
        for (int i = 0; i < this.textures.length; i++) {
            TextureData t = textures_[i].getTextureData();
            t.prepare();
            this.textures[i] = t.consumePixmap();
        }
        this.texW = textures_[0].getWidth();
        this.texH = textures_[0].getHeight();

        winCamera = new OrthographicCamera(width, height);
        winCamera.position.set(width/2f, height/2f, 0);
        winCamera.update();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(winCamera.combined);
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        buffer = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        batch = new SpriteBatch();
        setMode(DrawMode.FULL_WINDOW);
    }

    private void  rayCast(Map map) {
        GameCamera cam = map.cam;
        buffer.setColor(Color.BLACK);
        buffer.fill();


        // rotation for testing, delete this for proper movement testing
        //map.cam.dir.rotateDeg(0.5f);
        //map.cam.plane.rotateDeg(0.5f);

        for (int x = 0; x < width; x++) {
            float cameraX = 2*x / (float)width -1;              // direction of ray relative to the center of screen (-1; 1)
            float rayDirX = cam.dir.x + cam.plane.x* cameraX;
            float rayDirY = cam.dir.y + cam.plane.y* cameraX;

            float deltaDistX = (rayDirX == 0) ? 1e30f : Math.abs(1/ rayDirX); // distance to travell along y for next integer x value
            float deltaDistY = (rayDirY == 0) ? 1e30f : Math.abs(1/ rayDirY);

            // curent map position
            int mapX = (int)(cam.pos.x);
            int mapY = (int)(cam.pos.y);

            // accumulative distance travelled allond x and y
            float sideDistX;
            float sideDistY;

            // distance to the wall perpendicular to he camera plane
            float perpWallDist;
            int lineHeight;

            // map position increments
            int stepX;
            int stepY;

            // info about wall intercestion
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

            // trawell alond ray direction until wall hit
            // FIXME if the is no wall allond thw way it will go outside of map range and will crash the game
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

            // TODO draw textures
            int texNum = map.arr[mapY][mapX] -1;
            float wallX;
            if (side == 0) wallX = cam.pos.y + perpWallDist*rayDirY;
            else wallX = cam.pos.x + perpWallDist*rayDirX;
            wallX -= (float) Math.floor(wallX);

            int texX = (int)(wallX * texW);
            if(side == 0 && rayDirX > 0) texX = texW - texX - 1;
            if(side == 1 && rayDirY < 0) texX = texW - texX - 1;

            float step = (1.0f * texH) / lineHeight;
            float texPos = (drawStart - height / 2.0f + lineHeight / 2.0f) * step;

            for (int y = drawStart; y < drawEnd; y++) {
                int texY = (int)texPos & (texH - 1);
                texPos += step;

                int color = textures[texNum].getPixel(texX, texY);
                if(side == 1) color = (color >> 1) & 8355711;
                buffer.drawPixel(x, y, color);

            }


        }

    }

    @Override
    public void renderFrame(Map map) {
        frameBuffer.begin();
        //clearScreen();

        rayCast(map);
        frameBuffer.end();
    }

    @Override
    public void render(Map map) {
        renderFrame(map);
        drawFrame();
    }

    @Override
    public void drawFrame() {
        if (drawMode!=DrawMode.NONE) {
            frame = new Texture(buffer);
            TextureRegion frameT = new TextureRegion(frame);

            batch.begin();
            frameT.flip(false, true);
            batch.draw(
                frameT,
                renderPosX, renderPosY,
                renderWidth, renderHeight
            );
            batch.end();
        }
    }

    @Override
    public void dispose() {
        // TODO dispose of all objects this class
    }
}
