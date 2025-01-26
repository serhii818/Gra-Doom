package io.github.gra_doom;

import io.github.gra_doom.entity.Entity;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class MapEditor {
	
    private Map map;
    
    public MapEditor(Map map) {
        this.map = map;
    }
	
    public void setTile(int x, int y, int value) {
        if (x >= 0 && x < map.arr.length && y >= 0 && y < map.arr[0].length) {
            map.arr[x][y] = value;
        } else {
            System.out.println("Invalid coordinates for tile update");
        }
    }
    
    
    public static void saveMap(int[][] map, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            filePath = "map.json";
        }

        FileHandle file = Gdx.files.local(filePath);
        Json json = new Json();
        String mapJson = json.toJson(map);
        file.writeString(mapJson, false);
    }

    
    public static int[][] loadMap(String filePath, int mapWidth, int mapHeight) {
    	if (mapWidth == 0 || mapHeight == 0) {
            Json json = new Json();
            String mapData = Gdx.files.local(filePath).readString();
            return json.fromJson(int[][].class, mapData);
    	}
    	else {
    		int[][] map = new int[mapWidth][mapHeight];
    		
            for (int x = 0; x < mapWidth; x++) {
                for (int y = 0; y < mapHeight; y++) {
                	map[0][y] = 1;
                	map[x][0] = 1;
                	map[mapWidth-1][y] = 1;
                	map[x][mapHeight - 1] = 1;
                }
            }
            return map;
    	}
    }
        
}
