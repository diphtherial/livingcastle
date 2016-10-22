package com.escherial.livingcastle.components;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Faisal on 10/21/2016.
 */
public class Box2Dified extends Component {
    public Body body;
    public int foot_touches; // how many bodies the foot is touching (> 0 implies on ground)
}
