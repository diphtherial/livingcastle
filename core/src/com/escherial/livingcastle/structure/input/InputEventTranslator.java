package com.escherial.livingcastle.structure.input;

import java.util.HashSet;

/**
 * Translates raw input events (keyboard button presses, controller interaction) into player control events (moving, jumping, firing, etc.)
 */
public abstract class InputEventTranslator {
    protected HashSet<Action> pressed = new HashSet<Action>();
    protected HashSet<InputEventListener> listeners = new HashSet<InputEventListener>();

    public enum Action {
        MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN,
        JUMP, DUCK, FIRE, ROCKETJUMP
    }

    public boolean isPressed(Action query) {
        return pressed.contains(query);
    }

    /**
     * Adds the given listener to the list of instances that will receive pressed/released actions.
     * @param listener an instance that will have onPressed/onReleased invoked on it
     */
    public void addListener(InputEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the given listener from receiving pressed/released actions.
     *
     * If listener is not in the list, nothing happens.
     * @param listener a previously-registered instance to remove from the notification list
     */
    public void removeListener(InputEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Should be called by inheriting classes when an input event starts (e.g. a key is pressed)
     * @param action the game action that corresponds to the key press
     */
    protected void notifyOnPress(Action action) {
        for (InputEventListener listener : listeners) {
            listener.onPressed(action);
        }
    }

    /**
     * Should be called by inheriting classes when an input event ends (e.g. a key is released)
     * @param action the game action that corresponds to the key that's been released
     */
    protected void notifyOnRelease(Action action) {
        for (InputEventListener listener : listeners) {
            listener.onReleased(action);
        }
    }
}
