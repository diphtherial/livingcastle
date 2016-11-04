package com.escherial.livingcastle.structure.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Faisal on 10/23/2016.
 */
public class KeyboardEventTranslator extends InputEventTranslator implements InputProcessor {
    Map<Integer, Action> action_map = new HashMap<Integer, Action>();

    public KeyboardEventTranslator() {
        action_map.put(Input.Keys.LEFT, Action.MOVE_LEFT);
        action_map.put(Input.Keys.RIGHT, Action.MOVE_RIGHT);
        action_map.put(Input.Keys.DOWN, Action.DUCK);
        action_map.put(Input.Keys.UP, Action.JUMP);
        action_map.put(Input.Keys.SPACE, Action.FIRE);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (action_map.containsKey(keycode)) {
            Action action = action_map.get(keycode);
            pressed.add(action);
            notifyOnPress(action);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (action_map.containsKey(keycode)) {
            Action action = action_map.get(keycode);
            pressed.remove(action);
            notifyOnRelease(action);
            return true;
        }
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
