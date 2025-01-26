package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import io.github.gra_doom.entity.Projectile;

public class KeyboardController implements InputProcessor {

    private Player cam;
    private Map map;

    // Referencja do Main, by móc ustawiać pauzę (esc)
    private Main main;

    public KeyboardController(Player cam, Map map, Main main) {
        this.cam = cam;
        this.map = map;
        this.main = main;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.RIGHT:
                cam.rotatingLeft = true;
                break;
            case Input.Keys.LEFT:
                cam.rotatingRight = true;
                break;
            case Input.Keys.W:
                cam.movingForward = true;
                break;
            case Input.Keys.S:
                cam.movingBackward = true;
                break;
            case Input.Keys.D:
                cam.movingRight = true;
                break;
            case Input.Keys.A:
                cam.movingLeft = true;
                break;
            case Input.Keys.SPACE:
                cam.setShooting(true);
                break;
            case Input.Keys.Q:
                Gdx.app.exit();
                break;

            case Input.Keys.ESCAPE:
                // Włącz / wyłącz pauzę
                main.setPaused(!main.isPaused());
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.RIGHT:
                cam.rotatingLeft = false;
                break;
            case Input.Keys.LEFT:
                cam.rotatingRight = false;
                break;
            case Input.Keys.W:
                cam.movingForward = false;
                break;
            case Input.Keys.S:
                cam.movingBackward = false;
                break;
            case Input.Keys.D:
                cam.movingRight = false;
                break;
            case Input.Keys.A:
                cam.movingLeft = false;
                break;
            case Input.Keys.SPACE:
                cam.setShooting(false);
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }


}
