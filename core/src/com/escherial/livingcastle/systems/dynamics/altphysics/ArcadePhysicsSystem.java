package com.escherial.livingcastle.systems.dynamics.altphysics;

import com.artemis.Aspect;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.escherial.livingcastle.components.Physical;
import com.escherial.livingcastle.components.Position;
import com.escherial.livingcastle.structure.Level;

import static com.escherial.livingcastle.structure.Level.TILEW;

public class ArcadePhysicsSystem extends BasePhysicsSystem {
    public ArcadePhysicsSystem(Level level, OrthographicCamera camera) {
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
        physical.force.y -= 9.8f * physical.mass;

        // F = m*a, so a = F/m
        // we just reuse force for a here
        physical.force.scl(1.0f/physical.mass);

        // v = v0 + a*t
        physical.force.scl(delta);
        physical.vel.add(physical.force);

        // x = x0 + v*t
        // physical.vel.scl(delta);
        p.pos.add(physical.vel);

        //
        // step 2. deal with collisions
        //

        // make it so we can't fall out of the world
        if (p.pos.y < 0) {
            p.pos.y = 0;
            physical.isOnGround = true;
        }

        // we're by default ungrounded (unless something below declares itself our ground)
        physical.isOnGround = false;

        // check if our bbox is colliding with a collision block
        // we're making the tile coordinates based on the center, not the corner, of our little dude
        int px = (int) ((p.pos.x + TILEW/2.0f) / TILEW);
        int py = (int) ((p.pos.y + TILEW/2.0f) / TILEW);

        Rectangle playerRect = rectanglePool.obtain();
        playerRect.set(p.pos.x, p.pos.y, TILEW, TILEW);

        // checks an area around the player's rectangle, calling collideWithTile() on collisions
        checkTileMask(physical, p);

        // clear out acceleration
        physical.force.set(0, 0);

        // degrade velocity due to friction
        if (physical.isOnGround)
            physical.vel.scl(0.7f); // ground friction
        else
            physical.vel.scl(0.8f); // air friction
    }

    @Override
    protected void collideWithTile(Physical physical, Position p, Rectangle playerRect, Rectangle other, int i, int j) {
        // let overriding classes deal with this
        // if it's on our side, move us in the X direction
        if (j == 0) {
            if (i == -1) // tile on the left of us
                p.pos.x = other.x + other.getWidth();
            else if (i == 1) // tile on the right of us
                p.pos.x = other.x - other.getWidth();
        }

        // if it's below us, move us upward
        if (j == -1 && (p.pos.y + 3) >= (other.y + other.getHeight())) {
            physical.isOnGround = true;
            p.pos.y = other.y + other.getHeight();
        }
    }
}
