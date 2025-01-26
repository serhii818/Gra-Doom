package io.github.gra_doom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import io.github.gra_doom.entity.Entity;

import java.util.Arrays;

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
    float[] zBuffer;
    Integer[] spriteOrder;
    Float[] spriteDist;
    static public int maxSprites = 100;

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
        zBuffer = new float[width];
        spriteDist = new Float[maxSprites];
        spriteOrder = new Integer[maxSprites];

        setDrawFloorEnabled(true);
    }

    private void rayCast(Map map) {
        Player cam = map.player;
        buffer.setColor(Color.BLACK);
        buffer.fill();

        // floor casting
        if (drawFloorEnabled) drawFloor(cam);

        // wall casting
        drawWalls(cam, map);

        drawSprites(map);
    }

    private void drawFloor(Player cam) {
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

    private void drawSprites(Map map) {
        int spriteNum = map.entities.size();

        //sort sprites
        for (int i = 0; i< spriteNum; i++) {
            spriteOrder[i] = i;
            spriteDist[i] = map.entities.get(i).getDistFromCam(map.getPlayer());
        }
        sortSprites(spriteNum);

        for (int i = 0; i< spriteNum; i++) {
            Player cam = map.getPlayer();
            Entity e = map.entities.get(spriteOrder[i]);

            int texWidth = e.sprite.getWidth();
            int texHeight = e.sprite.getHeight();

            double spriteX = e.pos.x - cam.pos.x;
            double spriteY = e.pos.y - cam.pos.y;
            double invDet = 1.0 / (cam.plane.x * cam.dir.y - cam.dir.x * cam.plane.y);
            double transformX = invDet * (cam.dir.y * spriteX - cam.dir.x * spriteY);
            double transformY = invDet * (-cam.plane.y * spriteX + cam.plane.x * spriteY);

            int spriteScreenX = (int) ((width / 2) * (1 + transformX / transformY));
            int vMoveScreen = (int)(e.vMove / transformY);
            // ************************************************
            int spriteHeight = (int) (Math.abs((int)(height / (transformY))) / e.vDiv);

            int drawStartY = -spriteHeight / 2 + height / 2 + vMoveScreen;
            if(drawStartY < 0) drawStartY = 0;

            int drawEndY = spriteHeight / 2 + height / 2 + vMoveScreen;
            if(drawEndY >= height) drawEndY = height - 1;
            // ************************************************
            int spriteWidth = (int)(Math.abs( (int) (height / (transformY))) / (e.uDiv*(float)(texHeight)/texWidth));

            int drawStartX = -spriteWidth / 2 + spriteScreenX;
            if(drawStartX < 0) drawStartX = 0;

            int drawEndX = spriteWidth / 2 + spriteScreenX;
            if(drawEndX >= width) drawEndX = width - 1;
            // ************************************************

            float health_p = 0;
            if (e instanceof io.github.gra_doom.entity.Character c) {
                health_p = c.getHealth() / c.getMaxHealth();
            }

            for(int stripe = drawStartX; stripe < drawEndX; stripe++) {
                int texX = (int)(256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * texWidth / spriteWidth) / 256;
                //the conditions in the if are:
                //1) it's in front of camera plane so you don't see things behind you
                //2) it's on the screen (left)
                //3) it's on the screen (right)
                //4) ZBuffer, with perpendicular distance
                if(transformY > 0 && stripe > 0 && stripe < width && transformY < zBuffer[stripe])
                    for(int y = drawStartY; y < drawEndY; y++) //for every pixel of the current stripe
                    {
                        int d = (y-vMoveScreen) * 256 - height * 128 + spriteHeight * 128; //256 and 128 factors to avoid floats
                        int texY = ((d * texHeight) / spriteHeight) / 256;
                        int color;
                        if (y > drawStartY+5 || health_p == 0) {
                            color = e.sprite.getPixel(texX, texY);
                        } else {
                            if (((float)texX / texWidth) < health_p) color = Color.rgba8888(0, 1, 0, 1);
                            else color = Color.rgba8888(1, 0, 0, 1);

                        }

                        if ((color & 0xFFFFFF00) != 0) buffer.drawPixel(stripe, y, color);
                    }
            }
        }

    }

    private void sortSprites(int spriteNum) {
        // sorts sprite order according to sprite distance
        Arrays.sort(spriteOrder, 0, spriteNum, (i1, i2) -> Float.compare(spriteDist[i1], spriteDist[i2]));

        // revers to get from furthest to the closest
        for (int i = 0; i < spriteNum/2; i++) {
            Integer temp = spriteOrder[i];
            spriteOrder[i] = spriteOrder[spriteNum-i-1];
            spriteOrder[spriteNum-i-1] = temp;
        }

        // sort distances using sorted spriteOrder
        Float[] sortedSpriteDist = new Float[spriteNum];
        for (int i = 0; i < sortedSpriteDist.length; i++) {
            sortedSpriteDist[i] = spriteDist[spriteOrder[i]];
        }
        System.arraycopy(sortedSpriteDist, 0, spriteDist, 0, sortedSpriteDist.length);
    }

    public void setDrawFloorEnabled(boolean drawFloorEnabled) {
        this.drawFloorEnabled = drawFloorEnabled;
    }

    private void drawWalls(Player cam, Map map) {

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
                zBuffer[x] = perpWallDist;
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
