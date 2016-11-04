package com.escherial.livingcastle.structure.input;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Faisal on 10/23/2016.
 */
public class InputTranslatorMultiplexer extends InputEventTranslator {
    ArrayList<InputEventTranslator> translators = new ArrayList<InputEventTranslator>();

    public InputTranslatorMultiplexer(InputEventTranslator... translators) {
        Collections.addAll(this.translators, translators);
    }

    /**
     * Returns true if the button is pressed on any of our input translators.
     * @param query the action to check is enabled
     * @return true if the action is being done, false otherwise
     */
    @Override
    public boolean isPressed(Action query) {
        for (InputEventTranslator t : translators) {
            if (t.isPressed(query))
                return true;
        }

        return false;
    }
}
