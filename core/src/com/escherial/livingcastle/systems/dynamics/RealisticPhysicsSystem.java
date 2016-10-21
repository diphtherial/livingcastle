package com.escherial.livingcastle.systems.dynamics;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.escherial.livingcastle.components.Physical;
import com.escherial.livingcastle.components.Position;
import com.escherial.livingcastle.structure.Level;

import java.util.ArrayList;

import static com.escherial.livingcastle.structure.Level.TILEW;

public class RealisticPhysicsSystem extends BasePhysicsSystem {

    public RealisticPhysicsSystem(Level level, OrthographicCamera camera) {
        super(Aspect.all(Physical.class, Position.class), level, camera);
    }

    @Override
    protected void process(int e) {
        float delta = getWorld().getDelta();

        Position p = mPos.get(e);
        Physical physical = mPhysical.get(e);

        //
        // step 1. run the equations of motion
        //

        // apply gravity as force
        physical.force.y -= 9.8f;

        /*
        // F = m*a, so a = F/m
        Vector2 accel = physical.force.scl(1.0f/physical.mass);

        physical.vel.scl(delta)

        // v = v0 + a*t
        physical.force.scl(delta);
        physical.vel.add(physical.force);

        // x = x0 + v*t
        p.pos.add(vel);
        */

        // use verlet integration, described here: http://buildnewgames.com/gamephysics/
        /*
        Vector2 last_acceleration = new Vector2(physical.accel);
        Vector2 lac = new Vector2(last_acceleration).scl(0.5f * (float)Math.pow(delta, 2));
        p.pos.add(physical.vel.scl(delta).add(lac)); // pos += v*dt + (0.5 * last_accel * dt^2)

        // update our velocity for next time, i guess?
        physical.accel = new Vector2(physical.force).scl(1.0f/physical.mass);
        Vector2 new_acceleration = new Vector2(physical.accel);
        new_acceleration.add(last_acceleration).scl(0.5f); // (last_accel + new_accel)/2
        physical.vel.add(new_acceleration.scl(delta));
        */

        //
        // step 2. deal with collisions
        //

        // make it so we can't fall out of the world
        if (p.pos.y < 0) {
            p.pos.y = 0;
            physical.isOnGround = true;
        }

        // checks an area around the player's rectangle, calling collideWithTile() on collisions
        checkTileMask(physical, p);

        // degrade velocity due to friction
        if (physical.isOnGround)
            physical.vel.scl(0.7f); // ground friction
        else
            physical.vel.scl(0.85f); // air friction

        // clear out forces for next time
        physical.force.set(0, 0);
    }

    @Override
    protected void collideWithTile(Physical physical, Position p, Rectangle playerRect, Rectangle other, int i, int j) {
        super.collideWithTile(physical, p, playerRect, other, i, j);

        // TODO: provide some fancy physics-based restitution for this collision
    }
}
