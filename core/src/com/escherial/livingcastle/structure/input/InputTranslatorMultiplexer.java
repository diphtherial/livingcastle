package com.escherial.livingcastle.structure.input;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Faisal on 10/23/2016.
 */
public class InputTranslatorMultiplexer extends InputEventTranslator implements InputEventListener {
    ArrayList<InputEventTranslator> translators = new ArrayList<InputEventTranslator>();

    public InputTranslatorMultiplexer(InputEventTranslator... translators) {
        Collections.addAll(this.translators, translators);

        // also subscribe to their pressed/released events
        for (InputEventTranslator translator : translators) {
            translator.addListener(this);
        }
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

    // FIXME: we need a way to pass on notifications from the kids to any subscribers
    // one approach would be for us to subscribe to the kids, then re-raise the notifications...yeah, that works

    @Override
    public void onPressed(Action action) {
        // pass the message on
        notifyOnPress(action);
    }

    @Override
    public void onReleased(Action action) {
        notifyOnRelease(action);
    }
}
