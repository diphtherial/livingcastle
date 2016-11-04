package com.escherial.livingcastle.components;

import com.artemis.Component;

/**
 * Created by Faisal on 10/25/2016.
 */
public class Bullet extends Component {
    public int ownerID; // our owner
    public boolean destroyOnCollision = true;

    public Bullet() {
    }

    public Bullet(int ownerID) {
        this.ownerID = ownerID;
    }
}
