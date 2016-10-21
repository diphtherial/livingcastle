package com.escherial.livingcastle.components;

import com.artemis.Component;
import com.artemis.annotations.Transient;
import com.badlogic.gdx.graphics.Texture;

@Transient
public class Sprited extends Component {
    public Texture img;

    public Sprited() {
        img = null;
    }

    public Sprited(String s) {
        img = new Texture(s);
    }
}
