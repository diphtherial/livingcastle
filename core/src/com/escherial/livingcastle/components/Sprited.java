package com.escherial.livingcastle.components;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.badlogic.gdx.graphics.Texture;

@Transient
public class Sprited extends Component {
    public Texture img;
    public int offx = 0, offy = 0;

    public Sprited() {
        img = null;
    }

    public Sprited(String s) {
        img = new Texture(s);
    }

    public Sprited(String s, int offx, int offy) {
        this.img = new Texture(s);
        this.offx = offx;
        this.offy = offy;
    }
}
