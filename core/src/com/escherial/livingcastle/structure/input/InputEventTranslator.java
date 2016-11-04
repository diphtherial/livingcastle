package com.escherial.livingcastle.structure.input;

import java.util.HashSet;

/**
 * Translates raw input events (keyboard button presses, controller interaction) into player control events (moving, jumping, firing, etc.)
 */
public abstract class InputEventTranslator {
    protected HashSet<Action> pressed = new HashSet<Action>();

    public enum Action {
        MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN,
        JUMP, DUCK, FIRE, ROCKETJUMP
    }

    public boolean isPressed(Action query) {
        return pressed.contains(query);
    }
}
