package com.escherial.livingcastle.systems.control;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.escherial.livingcastle.components.Physical;
import com.escherial.livingcastle.components.PlayerControlled;

import java.util.HashSet;

import static com.escherial.livingcastle.components.PlayerControlled.State.*;

/**
 * Created by Faisal on 10/12/2016.
 */
public class PlayerControlSystem extends IteratingSystem implements InputProcessor {
    protected HashSet<Integer> pressed = new HashSet<Integer>();
    protected ComponentMapper<Physical> mPhysical;
    protected ComponentMapper<PlayerControlled> mPlayerControlled;
    final float PLAYER_SPEED = 12.0f;
    final float PLAYER_JUMP_THRUST = 300.0f;

    public PlayerControlSystem() {
        super(Aspect.all(PlayerControlled.class, Physical.class));
    }

    @Override
    protected void initialize() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    protected void process(int e) {
        Physical physical = mPhysical.get(e);
        PlayerControlled con = mPlayerControlled.get(e);

        switch (con.state) {
            case STANDING:
                if (pressed.contains(Input.Keys.UP)) {
                    // apply an impulse
                    physical.force.y += PLAYER_JUMP_THRUST;
                    physical.isOnGround = false;
                    con.state = JUMPING;
                } else if (pressed.contains(Input.Keys.DOWN)) {
                    // i guess we can duck?
                    con.state = DUCKING;
                }
                break;

            case JUMPING:
                // check if we're on the ground so we can resume standing
                if (physical.isOnGround) {
                    con.state = STANDING;
                }
                break;

            case DUCKING:
                if (!pressed.contains(Input.Keys.DOWN)) {
                    con.state = STANDING;
                }
                break;
        }

        // we can always move left and right
        if (pressed.contains(Input.Keys.LEFT)) {
            physical.force.x -= PLAYER_SPEED;
        } else if (pressed.contains(Input.Keys.RIGHT)) {
            physical.force.x += PLAYER_SPEED;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        pressed.add(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        pressed.remove(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
