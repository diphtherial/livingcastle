package com.escherial.livingcastle.systems.rendering;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.escherial.livingcastle.structure.Constants;
import com.escherial.livingcastle.structure.Level;

public class LayerRenderSystem extends BaseSystem {
    protected final SpriteBatch batch;
    protected final Level level;
    protected final int[] layers;
    protected final TiledMapRenderer renderer;
    private final OrthographicCamera camera;

    public LayerRenderSystem(SpriteBatch batch, OrthographicCamera camera, Level curLevel, int[] layers) {
        super();
        this.batch = batch;
        this.camera = camera;
        this.level = curLevel;
        this.layers = layers;

        renderer = (new OrthogonalTiledMapRenderer(curLevel.getMap(), Constants.TILE_PIXEL_MAPPING, batch) {
            @Override
            protected void beginRender() {
                AnimatedTiledMapTile.updateAnimationBaseTime();
                // don't open the batch, unlike our parent class
            }

            @Override
            protected void endRender() {
                // also don't close the batch
            }
        });
    }

    @Override
    protected void processSystem() {
        renderer.setView(camera);
        renderer.render(layers);
    }
}
