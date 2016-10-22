package com.escherial.livingcastle.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Faisal on 10/12/2016.
 */
public class Position extends Component {
    public Vector2 pos = new Vector2();
    public float width = 1f, height = 1f;

    public Position() {
        pos.set(0, 0);
        width = 1f; height = 1f;
    }

    public Position(float x, float y) {
        pos.set(x, y);
    }

    public Position(float x, float y, float width, float height) {
        pos.set(x, y);
        this.width = width;
        this.height = height;
    }
}
