package com.escherial.livingcastle;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.escherial.livingcastle.structure.Constants;
import com.escherial.livingcastle.structure.EntityFactory;
import com.escherial.livingcastle.structure.Level;
import com.escherial.livingcastle.systems.dynamics.ArcadePhysicsSystem;
import com.escherial.livingcastle.systems.control.EntityObserverSystem;
import com.escherial.livingcastle.systems.control.PlayerControlSystem;
import com.escherial.livingcastle.systems.dynamics.BoxPhysicsSystem;
import com.escherial.livingcastle.systems.rendering.BGLayerRenderSystem;
import com.escherial.livingcastle.systems.rendering.EntityRenderSystem;
import com.escherial.livingcastle.systems.rendering.FGLayerRenderSystem;

public class LivingCastleMain extends ApplicationAdapter {
    World world;
    private SpriteBatch batch;
    OrthographicCamera camera;
    private BoxPhysicsSystem physicsSystem;

    @Override
    public void create() {
        // we have to load the level first in order to parameterize the level layer rendering systems properly
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        Level curLevel = new Level("levels/intro.tmx");

        physicsSystem = new BoxPhysicsSystem(curLevel, camera);
        PlayerControlSystem pcontrol = new PlayerControlSystem();
        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(new EntityObserverSystem(camera), pcontrol, physicsSystem)
                .with(
                        new BGLayerRenderSystem(batch, camera, curLevel, curLevel.getLayers(true)),
                        new EntityRenderSystem(batch, camera),
                        new FGLayerRenderSystem(batch, camera, curLevel, curLevel.getLayers(false))
                )
                .build();
        world = new World(config);

        // scan the entity layer of the map looking for the player
        TiledMapTileMapObject player = curLevel.getPlayer();
        float px = 0, py = 0;

        if (player != null) {
            // have to map from tiled pixel coordinates into world coordinates
            px = player.getX() * Constants.TILE_PIXEL_MAPPING;
            py = player.getY() * Constants.TILE_PIXEL_MAPPING;
        }

        // create the player and adds them to the world
        EntityFactory.createPlayer(world, px, py);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(32 / 255.0f, 20 / 255.0f, 41 / 255.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        world.setDelta(Gdx.graphics.getDeltaTime());
        world.process();

        batch.end();

        // have the physics system render whatever HUDy stuff it wants now
        physicsSystem.debugRender();
    }

//    @Override
//    public void resize(int width, int height) {
//        camera.viewportHeight = (Constants.VIEWPORT_WIDTH / width) * height;
//        camera.update();
//    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
    }
}
