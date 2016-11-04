package com.escherial.livingcastle.structure;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;

import java.util.Arrays;

import static com.escherial.livingcastle.structure.Tools.range;

public class Level {
    public static final int TILEW = 2; // in meters

    protected final TiledMap map;
    public final int mapWidth;
    public final int mapHeight;
    public final int tilePixelWidth;
    public final int tilePixelHeight;
    public final int mapPixelWidth;
    public final int mapPixelHeight;
    protected int[] bg_layers;
    protected int[] fg_layers;
    protected int entityLayerID;
    TiledMapTileLayer collider_layer;

    public Level(String mapPath) {
        map = new TmxMapLoader().load(mapPath);
        MapProperties prop = map.getProperties();

        // useful metadata
        mapWidth = prop.get("width", Integer.class);
        mapHeight = prop.get("height", Integer.class);
        tilePixelWidth = prop.get("tilewidth", Integer.class);
        tilePixelHeight = prop.get("tileheight", Integer.class);

        mapPixelWidth = mapWidth * tilePixelWidth;
        mapPixelHeight = mapHeight * tilePixelHeight;

        // find where the entities layer is
        entityLayerID = map.getLayers().getIndex("entities");
        if (entityLayerID >= 0) {
            // separate layers into fg and bg
            bg_layers = range(0, entityLayerID);
            fg_layers = range(entityLayerID + 1, map.getLayers().getCount());
            System.out.println("BG Layers: " + Arrays.toString(bg_layers));
            System.out.println("Entity Layer: " + entityLayerID);
            System.out.println("FG Layers: " + Arrays.toString(fg_layers));
        } else {
            System.out.println("No entity layer detected");
        }

        collider_layer = (TiledMapTileLayer) map.getLayers().get("collider");
    }

    public TiledMap getMap() {
        return map;
    }

    public TiledMapTileMapObject getPlayer() {
        if (entityLayerID < 0)
            return null;

        // scan the entity layer of the map looking for the player
        return (TiledMapTileMapObject) map.getLayers().get(entityLayerID).getObjects().get("player");
    }

    public boolean occupied(int x, int y) {
        // first, world coords are apparently the reverse of level coords
        return false;
    }

    public int[] getLayers(boolean bg) {
        return (bg) ? bg_layers : fg_layers;
    }

    public boolean collidable(int x, int y) {
        return collider_layer.getCell(x, y) != null;
    }
}
