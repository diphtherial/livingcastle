package com.escherial.livingcastle.components;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.badlogic.gdx.graphics.Texture;

@Transient
public class Sprited extends Component {
    public Texture img;
    public float offx = 0, offy = 0;
    public enum Facing {
        DEFAULT, LEFT, RIGHT, UP, DOWN, VELOCITY
    }
    public Facing facing = Facing.DEFAULT;

    public Sprited() {
        img = null;
    }

    public Sprited(String s) {
        img = new Texture(s);
    }

    public Sprited(String s, float offx, float offy) {
        this.img = new Texture(s);
        this.offx = offx;
        this.offy = offy;
    }

    public Sprited(String s, float offx, float offy, Facing facing) {
        this.img = new Texture(s);
        this.offx = offx;
        this.offy = offy;
        this.facing = facing;
    }
}
