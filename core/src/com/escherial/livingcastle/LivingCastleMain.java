package com.escherial.livingcastle;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.escherial.livingcastle.screens.GameScreen;
import com.escherial.livingcastle.screens.LoadingScreen;
import com.escherial.livingcastle.structure.input.KeyboardEventTranslator;
import com.escherial.livingcastle.structure.input.XboxControllerTranslator;
import com.escherial.livingcastle.systems.dynamics.BoxPhysicsSystem;

public class LivingCastleMain extends Game implements ApplicationListener {
    public SpriteBatch batch;
    public BoxPhysicsSystem physicsSystem;
    public KeyboardEventTranslator kbd_trans;
    public XboxControllerTranslator xbox_trans;

    public AssetManager assets;
    public Skin skin;

    public BitmapFont shinyfont;
    public BitmapFont microfont;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new AssetManager();
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));

        microfont = new BitmapFont(Gdx.files.internal("fonts/microlaser.fnt"));
        shinyfont = new BitmapFont(Gdx.files.internal("fonts/lasersword.fnt"));

        // create a keyboard input processor that we'll be using to translate key events into player control
        KeyboardEventTranslator kbd_trans = new KeyboardEventTranslator();
        Gdx.input.setInputProcessor(kbd_trans);
        XboxControllerTranslator xbox_trans = new XboxControllerTranslator();
        Controllers.addListener(xbox_trans);

        this.kbd_trans = kbd_trans;
        this.xbox_trans = xbox_trans;

        this.setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
    }
}
