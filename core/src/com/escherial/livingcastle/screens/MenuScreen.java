package com.escherial.livingcastle.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.escherial.livingcastle.LivingCastleMain;

/**
 * Created by Faisal on 10/30/2016.
 */
public class MenuScreen extends ScreenAdapter {
    LivingCastleMain game;
    Stage stage;

    public MenuScreen(LivingCastleMain game) {
        super();
        this.game = game;
        stage = new Stage(new ScreenViewport());

        Label title = new Label("LIVING CASTLE", game.skin);
        stage.addActor(title);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        stage.act(delta);
        stage.draw();

        // FIXME: either poll for menu input or subscribe to events
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
