package com.escherial.livingcastle.structure.input;

import com.badlogic.gdx.InputProcessor;

/**
 * Created by Faisal on 11/2/2016.
 */
public interface InputEventProxy extends InputProcessor {
    public InputEventTranslator.Action actionDown();
    public InputEventTranslator.Action actionUp();
}
