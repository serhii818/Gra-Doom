package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import io.github.gra_doom.entity.Entity;

public class MapEditorController implements InputProcessor {
	
	private Renderer dr;
	private OrthographicCamera camera;
	Vector3 dir =  new Vector3();
    private int tileWidth, tileHeight;
    MapEditor editor;
    int selectedBlock;
    Map map;

    public MapEditorController(Renderer dr, int tileWidth, int tileHeight, Map map) {
    	this.dr = dr;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.map = map;
        editor = new MapEditor(map);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
		case Input.Keys.NUM_0:
			selectedBlock = 0;
			break;
		case Input.Keys.NUM_1:
			selectedBlock = 1;
			break;
		case Input.Keys.NUM_2:
			selectedBlock = 2;
			break;
		case Input.Keys.NUM_3:
			selectedBlock = 3;
			break;
		case Input.Keys.NUM_4:
			selectedBlock = 4;
			break;
		case Input.Keys.NUM_5:
			selectedBlock = 5;
			break;
		case Input.Keys.NUM_6:
			selectedBlock = 6;
			break;	
		case Input.Keys.NUM_7:
			selectedBlock = 7;
			break;
		case Input.Keys.S:
			MapEditor.saveMap(map.arr, "map.json");
			break;
		}
		return true;
	}

    
	@Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {

        	Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        	
            float cameraX = camera.position.x;
            float cameraY = camera.position.y;
            
            float scaledTileWidth = tileWidth * camera.zoom;
            float scaledTileHeight = tileHeight * camera.zoom;

            int tileX = (int) ((worldCoords.x + cameraX - (camera.viewportWidth / 2f)) / scaledTileWidth);
            int tileY = (int) ((worldCoords.y + cameraY - (camera.viewportHeight / 2f)) / scaledTileHeight);

        	editor.setTile( tileY , tileX, selectedBlock);
        	
            return true;
        }
        return false;
    }
	
	
	@Override
	public boolean scrolled(float amountX, float amountY) {
	    float zoomSpeed = 0.1f;
	    float newZoom = camera.zoom + amountY * zoomSpeed;
	    newZoom = Math.max(0.5f, Math.min(newZoom, 5.0f));
	  
	    camera.zoom = newZoom;
	    camera.update();

	    if (dr instanceof DebugRenderer) {
	        DebugRenderer debugRenderer = (DebugRenderer) dr;
	        debugRenderer.setZoom(newZoom);
	    }
	    
	    return true;
	}
    
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {return false; }

}

