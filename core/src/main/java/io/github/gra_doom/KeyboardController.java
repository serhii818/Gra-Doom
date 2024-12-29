package io.github.gra_doom;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import io.github.gra_doom.entity.Projectile;

public class KeyboardController implements InputProcessor{

	private Player cam;
    private Map map;


	public KeyboardController(Player cam, Map map) {
		this.cam = cam;
        this.map = map;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
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
            Projectile p = new Projectile(cam.pos.cpy(), 0.5f, "pics/barrel.png", 1,10, true);
            p.setVel(cam.dir.cpy());
            map.addEntity(p);
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
