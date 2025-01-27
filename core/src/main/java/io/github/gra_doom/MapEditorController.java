package io.github.gra_doom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.github.gra_doom.entity.Enemy;
import io.github.gra_doom.entity.Entity;
import io.github.gra_doom.entity.PickUpItem;

public class MapEditorController implements InputProcessor {
	
	private Renderer dr;
	private OrthographicCamera camera;
	Vector3 dir =  new Vector3();
    private int tileWidth, tileHeight;
    MapEditor editor;
    int selectedBlock;
    Map map;
    
    public enum Type {
        DEMON,
        IMP,
        ZOMBIE,
        X
    }
    
    Type EnemyType = Type.X;

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
		case Input.Keys.NUM_8:
			selectedBlock = 8;
			break;
		case Input.Keys.Q:
			selectedBlock = 9;
			break;
		case Input.Keys.W:
			selectedBlock = 10;
			break;
		case Input.Keys.E:
			selectedBlock = 11;
			break;
		case Input.Keys.R:
			selectedBlock = 12;
			break;
		case Input.Keys.T:
			selectedBlock = 13;
			break;
		case Input.Keys.Y:
			selectedBlock = 14;
			break;		
		case Input.Keys.S:
			MapEditor.saveMap(map.arr, "map.json", map);
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
            
            if(selectedBlock == 0) {
            	for(Entity entity : map.entities) {
            		if(entity.pos.x == tileX && entity.pos.y == tileY) {
            			entity.selfDestroy();
            		}
            	}
            }

            if(selectedBlock < 9) {
            	editor.setTile( tileY , tileX, selectedBlock);
            }
 
        	switch (selectedBlock) {
        		case 9:
        			map.addEntity(Enemy.makeEnemy(Enemy.Type.IMP, new Vector2(tileX, tileY)));
        			break;
        		case 10:
        			map.addEntity(Enemy.makeEnemy(Enemy.Type.DEMON, new Vector2(tileX, tileY)));
        			break;
        		case 11:
        			map.addEntity(Enemy.makeEnemy(Enemy.Type.ZOMBIE, new Vector2(tileX, tileY)));
        			break;
        		case 12:
        			map.addEntity(PickUpItem.makeItem(new Vector2(tileX, tileY), PickUpItem.Item.HEALTH25));
        			break;
        		case 13:
        			map.addEntity(PickUpItem.makeItem(new Vector2(tileX, tileY), PickUpItem.Item.HEALTH50));
        			break;
        		case 14:
        			map.addEntity(PickUpItem.makeItem(new Vector2(tileX, tileY), PickUpItem.Item.HEALTH100));
        			break;
        		default:
        			break;
        	}
        	
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

