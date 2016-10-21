package com.escherial.livingcastle.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Faisal on 10/13/2016.
 */
public class Physical extends Component {
    public Vector2 force = new Vector2();
    public Vector2 vel = new Vector2();
    public float mass = 100.0f;
    public boolean isOnGround = true;
}
