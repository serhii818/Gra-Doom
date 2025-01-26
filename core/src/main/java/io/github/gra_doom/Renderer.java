package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Class for rendering scene on the screen allowing positioning the contents in 5 places
 * <li>
 *     Full window mode
 *     in one of 4 corners
 *     or none
 * </li>
 */
abstract public class   Renderer  implements Disposable {
    public enum DrawMode {
        FULL_WINDOW,
        CORNER_UL,
        CORNER_UR,
        CORNER_LL,
        CORNER_LR,
        NONE,
    }

    // Parameters for position and size of frame on the sceen
    int renderWidth;
    int renderHeight;
    int renderPosX;
    int renderPosY;

    // acctual size of frame
    int width;
    int height;

    DrawMode drawMode;

    FrameBuffer frameBuffer;
    Texture frame;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    OrthographicCamera winCamera;
    Viewport viewport;

    public Renderer(int width, int height) {
        this.height = height;
        this.width = width;

        winCamera = new OrthographicCamera();
        viewport = new FitViewport(width, height, winCamera);
        winCamera.position.set(width/2f, height/2f, 0);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        winCamera.update();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(winCamera.combined);
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        batch = new SpriteBatch();

        setMode(DrawMode.FULL_WINDOW);
    }

    abstract public void render(Map map);
    abstract public void renderFrame(Map map);
    public void dispose() {
        frameBuffer.dispose();
        frame.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }

    /**
     * Draws contents of frame on the screen according to DrawMode
     */
    public void drawFrame() {
        if (drawMode!=DrawMode.NONE) {
            frame = frameBuffer.getColorBufferTexture();
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

    public void clearScreen() {
        Gdx.gl.glClearColor(0f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void setMode(DrawMode drawMode_)  {
        drawMode = drawMode_;
        switch (drawMode) {
            case FULL_WINDOW:
                renderPosX = 0;
                renderPosY = 0;
                renderWidth = Gdx.graphics.getWidth();
                renderHeight = Gdx.graphics.getHeight();
                break;
            case CORNER_UL:
                renderPosX = 0;
                renderPosY = Gdx.graphics.getHeight()/2;
                renderWidth = Gdx.graphics.getWidth()/2;
                renderHeight = Gdx.graphics.getHeight()/2;
                break;
            case CORNER_UR:
                renderPosX = Gdx.graphics.getWidth()/2;
                renderPosY = Gdx.graphics.getHeight()/2;
                renderWidth = Gdx.graphics.getWidth()/2;
                renderHeight = Gdx.graphics.getHeight()/2;
                break;
            case CORNER_LL:
                renderPosX = 0;
                renderPosY = 0;
                renderWidth = Gdx.graphics.getWidth()/2;
                renderHeight = Gdx.graphics.getHeight()/2;
                break;
            case CORNER_LR:
                renderPosX = Gdx.graphics.getHeight()/2;
                renderPosY = 0;
                renderWidth = Gdx.graphics.getWidth()/2;
                renderHeight = Gdx.graphics.getHeight()/2;
                break;
            case NONE:
                break;
        }
    }

}
