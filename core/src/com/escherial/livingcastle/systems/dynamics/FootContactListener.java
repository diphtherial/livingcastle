package com.escherial.livingcastle.systems.dynamics;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.*;
import com.escherial.livingcastle.components.Physical;

/**
 * Created by Faisal on 10/21/2016.
 */
public class FootContactListener implements ContactListener {
    ComponentMapper<Physical> mPhysical;

    public FootContactListener(World world) {
        mPhysical = new ComponentMapper<Physical>(Physical.class, world);
    }

    public FootContactListener(ComponentMapper<Physical> mPhysical) {
        this.mPhysical = mPhysical;
    }

    @Override
    public void beginContact(Contact contact) {
        Physical physical = getPhysical(contact);

        if (physical != null) {
            physical.foot_touches += 1;
            physical.isOnGround = (physical.foot_touches > 0);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Physical physical = getPhysical(contact);

        if (physical != null) {
            physical.foot_touches -= 1;
            physical.isOnGround = (physical.foot_touches > 0);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private Physical getPhysical(Contact contact) {
        Fixture[] fixtures = new Fixture[]{contact.getFixtureA(), contact.getFixtureB()};

        for (Fixture f : fixtures) {
            if (f.isSensor() && f.getUserData() instanceof Integer) {
                int entityID = (Integer) f.getUserData();

                // check if it's a physical thing all the same
                if (mPhysical.has(entityID)) {
                    return mPhysical.get(entityID);
                }
            }
        }

        return null;
    }
}
