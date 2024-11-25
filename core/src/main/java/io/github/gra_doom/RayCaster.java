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
    Pixmap floorTex;
    Pixmap ceilingTex;

    private boolean drawFloorEnabled;

    public RayCaster(int width, int height, Texture[] textures_) {
        super(width, height);

        // convert texture into PixMaps and setup buffer
        this.textures = new Pixmap[textures_.length];
        for (int i = 0; i < this.textures.length; i++) {
            TextureData t = textures_[i].getTextureData();
            t.prepare();
            this.textures[i] = t.consumePixmap();
        }
        this.texW = textures_[0].getWidth();
        this.texH = textures_[0].getHeight();

        this.floorTex = this.textures[2];
        this.ceilingTex = this.textures[7];

        buffer = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        setDrawFloorEnabled(true);
    }

    private void rayCast(Map map) {
        GameCamera cam = map.cam;
        buffer.setColor(Color.BLACK);
        buffer.fill();


        // rotation for testing, delete this for proper movement testing

        // floor casting
        if (drawFloorEnabled) drawFloor(cam);

        // wall casting
        drawWalls(cam, map);
    }

    private void drawFloor(GameCamera cam) {
        for (int y = 0; y < height; y++) {
            float rayDirX0 = cam.dir.x - cam.plane.x;
            float rayDirY0 = cam.dir.y - cam.plane.y;
            float rayDirX1 = cam.dir.x + cam.plane.x;
            float rayDirY1 = cam.dir.y + cam.plane.y;

            int p = y - height / 2;
            float posZ = 0.5f * height;
            float rowDistance = posZ / p;

            float floorStepX = rowDistance * (rayDirX1 - rayDirX0) / width;
            float floorStepY = rowDistance * (rayDirY1 - rayDirY0) / width;

            float floorX = cam.pos.x + rowDistance * rayDirX0;
            float floorY = cam.pos.y + rowDistance * rayDirY0;

            for(int x = 0; x < width; ++x)
            {
                // the cell coordinate is simply got from the integer parts of floorX and floorY
                int cellX = (int)(floorX);
                int cellY = (int)(floorY);

                // get the texture coordinate from the fractional part
                int tx = (int)(texW * (floorX - cellX)) & (texW - 1);
                int ty = (int)(texH * (floorY - cellY)) & (texH - 1);

                floorX += floorStepX;
                floorY += floorStepY;

                // choose texture and draw the pixel
                int color;

                // floor
                color = floorTex.getPixel(tx, ty);
                //color = (color >> 1) & 8355711; // make a bit darker
                //color = color | 0b00000000_00000000_00000000_11111111;
                buffer.drawPixel(x, y, color);

                //ceiling (symmetrical, at screenHeight - y - 1 instead of y)
                color = ceilingTex.getPixel(tx, ty);
                //color = (color >> 1) & 8355711; // make a bit darker
                color = color & 0xef_ef_ef_ff;
                buffer.drawPixel(x, height-y-1, color);
            }
        }
    }

    public void setDrawFloorEnabled(boolean drawFloorEnabled) {
        this.drawFloorEnabled = drawFloorEnabled;
    }

    private void drawWalls(GameCamera cam, Map map) {
        for (int x = 0; x < width; x++) {
            float cameraX = 2*x / (float)width -1;              // direction of ray relative to the center of screen (-1; 1)
            float rayDirX = cam.dir.x + cam.plane.x* cameraX;
            float rayDirY = cam.dir.y + cam.plane.y* cameraX;

            float deltaDistX = (rayDirX == 0) ? 1e30f : Math.abs(1/ rayDirX); // distance to travel along y for next integer x value
            float deltaDistY = (rayDirY == 0) ? 1e30f : Math.abs(1/ rayDirY);

            // current map position
            int mapX = (int)(cam.pos.x);
            int mapY = (int)(cam.pos.y);

            // accumulative distance travelled along x and y
            float sideDistX;
            float sideDistY;

            // distance to the wall perpendicular to the camera plane
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
            int c = 0;
            while (hit == 0 && c < 100) {
                c++;
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }

                if (map.arr.length > mapY && mapY >= 0 && map.arr[0].length > mapX && mapX >= 0) {
                    if (map.arr[mapY][mapX] > 0) hit = 1;
                } else {
                    break;
                }
            }

            if (hit == 1) {
                if (side == 0) perpWallDist = (sideDistX - deltaDistX);
                else perpWallDist = (sideDistY - deltaDistY);

                lineHeight = (int) (height / perpWallDist);

                int drawStart = -lineHeight / 2 + height / 2;
                int drawEnd = lineHeight / 2 + height / 2;

                int texNum = map.arr[mapY][mapX] - 1;
                float wallX;
                if (side == 0) wallX = cam.pos.y + perpWallDist * rayDirY;
                else wallX = cam.pos.x + perpWallDist * rayDirX;
                wallX -= (float) Math.floor(wallX);

                int texX = (int) (wallX * texW);
                if (side == 0 && rayDirX > 0) texX = texW - texX - 1;
                if (side == 1 && rayDirY < 0) texX = texW - texX - 1;

                float step = (1.0f * texH) / lineHeight;
                float texPos = (drawStart - height / 2.0f + lineHeight / 2.0f) * step;


                for (int y = drawStart; y < drawEnd; y++) {
                    int texY = (int) texPos & (texH - 1);
                    texPos += step;

                    int color = textures[texNum].getPixel(texX, texY);
                    if (side == 1) color = (color >> 1) & 8355711;
                    color = color | 0b00000000_00000000_00000000_11111111;
                    buffer.drawPixel(x, y, color);

                }
            }
        }
    }

    @Override
    public void renderFrame(Map map) {
        //frameBuffer.begin();
        rayCast(map);
        //frameBuffer.end();
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
            System.out.println(frameT.getTexture().getWidth());
            batch.begin();
            //frameT.flip(false, true);
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
        super.dispose();
        buffer.dispose();
        for (Pixmap texture : textures) {
            texture.dispose();
        }
    }
}
