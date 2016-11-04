package com.escherial.livingcastle.components;

import com.artemis.Component;

/**
 * Created by Faisal on 10/12/2016.
 */
public class PlayerControlled extends Component {
    public float firing_rate = 0.5f;
    public float fire_cooldown = 0f;

    public enum State {
        STANDING, MOVING, JUMPING, LANDED, DUCKING, AIRBORNE
    }

    public State state = State.STANDING;
}
