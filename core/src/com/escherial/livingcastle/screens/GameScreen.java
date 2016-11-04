package com.escherial.livingcastle.screens;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.escherial.livingcastle.LivingCastleMain;
import com.escherial.livingcastle.structure.Constants;
import com.escherial.livingcastle.structure.EntityFactory;
import com.escherial.livingcastle.structure.Level;
import com.escherial.livingcastle.structure.input.InputTranslatorMultiplexer;
import com.escherial.livingcastle.systems.control.EntityObserverSystem;
import com.escherial.livingcastle.systems.control.PlayerControlSystem;
import com.escherial.livingcastle.systems.dynamics.BoxPhysicsSystem;
import com.escherial.livingcastle.systems.rendering.BGLayerRenderSystem;
import com.escherial.livingcastle.systems.rendering.EntityRenderSystem;
import com.escherial.livingcastle.systems.rendering.FGLayerRenderSystem;

public class GameScreen extends ScreenAdapter {
    World world;
    SpriteBatch batch;
    OrthographicCamera camera;
    BoxPhysicsSystem physicsSystem;

    private final Stage stage;
    private final Table table;

    public GameScreen(final LivingCastleMain game) {
        super();

        // we have to load the level first in order to parameterize the level layer rendering systems properly
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        Level curLevel = new Level("levels/intro.tmx");

        batch = game.batch;

        physicsSystem = new BoxPhysicsSystem(curLevel, camera);
        PlayerControlSystem pcontrol = new PlayerControlSystem(game.muxed_trans);
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

        // also set up the stage
        stage = new Stage(new ScreenViewport());

        table = new Table();
        table.setFillParent(true);
        table.left().top();
        stage.addActor(table);

        for (int i = 0; i < 12; i++) {
            Image missle = new Image(new Texture("sprites/missile.png"));
            table.add(missle).pad(5);
        }


        table.setDebug(true);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(32 / 255.0f, 20 / 255.0f, 41 / 255.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        world.setDelta(delta);
        world.process();

        batch.end();

        // have the physics system render whatever HUDy stuff it wants now
        physicsSystem.debugRender();

        // draw the stage on top of everything
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = (height * Constants.VIEWPORT_WIDTH)/width;
        camera.update();

        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
        stage.dispose();
    }
}
