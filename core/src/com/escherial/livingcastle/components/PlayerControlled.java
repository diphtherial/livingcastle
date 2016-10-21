package com.escherial.livingcastle.components;

import com.artemis.Component;

/**
 * Created by Faisal on 10/12/2016.
 */
public class PlayerControlled extends Component {
    public enum State {
        STANDING, MOVING, JUMPING, DUCKING
    }

    public State state = State.STANDING;
}
