package com.escherial.livingcastle.structure.input;

/**
 * Created by Faisal on 11/4/2016.
 */
public interface InputEventListener {
    void onPressed(InputEventTranslator.Action action);
    void onReleased(InputEventTranslator.Action action);
}
