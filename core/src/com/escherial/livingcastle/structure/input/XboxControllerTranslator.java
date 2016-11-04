package com.escherial.livingcastle.structure.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Faisal on 10/23/2016.
 */
public class XboxControllerTranslator extends InputEventTranslator implements ControllerListener {
    Map<Integer, Action> action_map = new HashMap<Integer, Action>();
    final boolean debug = false;

    // our own butto map b/c Xbox is having issues
    final int Us_A = 0;
    final int Us_B = 1;
    final int Us_X = 2;
    final int Us_Y = 3;

    public XboxControllerTranslator() {
        action_map.put(PovDirection.west.ordinal(), Action.MOVE_LEFT);
        action_map.put(PovDirection.east.ordinal(), Action.MOVE_RIGHT);
        action_map.put(PovDirection.north.ordinal(), Action.MOVE_UP);
        action_map.put(PovDirection.south.ordinal(), Action.DUCK);
        action_map.put(Us_A, Action.JUMP);
        action_map.put(Us_X, Action.FIRE);
    }

    public int indexOf (Controller controller) {
        return Controllers.getControllers().indexOf(controller, true);
    }

    @Override
    public void connected(Controller controller) {
        // i guess we should do something?
    }

    @Override
    public void disconnected(Controller controller) {
        // ditto
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (debug)
            System.out.println("Key pressed: " + buttonCode);

        if (action_map.containsKey(buttonCode)) {
            pressed.add(action_map.get(buttonCode));
            return true;
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (debug)
            System.out.println("Key released: " + buttonCode);

        if (action_map.containsKey(buttonCode)) {
            pressed.remove(action_map.get(buttonCode));
            return true;
        }
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        // System.out.println("Axis moved: " + axisCode + ", " + value);
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        if (debug)
            System.out.println("POV moved: " + povCode + ", " + value);

        if (povCode == 0) {
            switch (value) {
                case north:
                case south:
                case east:
                case west:
                    Action p = action_map.get(value.ordinal());
                    if (p != null)
                        pressed.add(p);
                    return true;
                case center:
                    // remove all directions from set
                    pressed.remove(Action.MOVE_RIGHT);
                    pressed.remove(Action.MOVE_LEFT);
                    pressed.remove(Action.DUCK);
                    pressed.remove(Action.MOVE_UP);
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }
}
