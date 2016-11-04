package com.escherial.livingcastle.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Faisal on 10/13/2016.
 */
public class Physical extends Component {
    public Vector2 force = new Vector2();
    public Vector2 vel = new Vector2();
    public float mass = 1.0f;
    public boolean tracksGround = false;
    public boolean isOnGround = true;

    public Body body;
    public int foot_touches; // how many bodies the foot is touching (> 0 implies on ground)
    public boolean isBullet;

    public enum CollisionCategory {
        NONE, PLAYER, ENEMY
    }
    public CollisionCategory collideCategory = CollisionCategory.NONE;
}
