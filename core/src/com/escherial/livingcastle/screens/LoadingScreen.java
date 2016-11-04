package com.escherial.livingcastle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Align;
import com.escherial.livingcastle.LivingCastleMain;

/**
 * Created by Faisal on 10/27/2016.
 */
public class LoadingScreen extends ScreenAdapter {
    private final LivingCastleMain game;
    final float MAX_BLINK = 1.0f;
    private final AssetManager assets;
    float blink_time = MAX_BLINK;

    public LoadingScreen(LivingCastleMain game) {
        this.game = game;
        this.assets = game.assets;

        // i assume we'll load everything here?
        assets.load("sprites/bluetank-still.png", Texture.class);
        assets.load("sprites/missile.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (game.assets.update()) {
            // if it returns true, we're done, so go to the next screen
            game.setScreen(new GameScreen(game));
        }

        // render some loading text?
        game.batch.begin();
        if (blink_time > MAX_BLINK/2f) {
            game.shinyfont.draw(game.batch, "-= L O A D I N G =-", 0, Gdx.graphics.getHeight()/2, Gdx.graphics.getWidth(), Align.center, true);
        }
        game.batch.end();

        blink_time -= delta;
        if (blink_time <= 0)
            blink_time = MAX_BLINK;
    }
}
