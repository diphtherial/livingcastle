package com.escherial.livingcastle.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Faisal on 10/12/2016.
 */
public class Position extends Component {
    public Vector2 pos = new Vector2();

    public Position() {
        pos.set(0, 0);
    }

    public Position(float x, float y) {
        pos.set(x, y);
    }
}
