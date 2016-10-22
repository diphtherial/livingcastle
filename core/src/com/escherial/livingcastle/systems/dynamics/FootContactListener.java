package com.escherial.livingcastle.systems.dynamics;

import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.*;
import com.escherial.livingcastle.components.Box2Dified;
import com.escherial.livingcastle.components.Physical;
import javafx.util.Pair;

/**
 * Created by Faisal on 10/21/2016.
 */
public class FootContactListener implements ContactListener {
    ComponentMapper<Physical> mPhysical;
    ComponentMapper<Box2Dified> mBox2dified;

    public FootContactListener(World world) {
        mPhysical = new ComponentMapper<Physical>(Physical.class, world);
        mBox2dified = new ComponentMapper<Box2Dified>(Box2Dified.class, world);
    }

    public FootContactListener(ComponentMapper<Physical> mPhysical, ComponentMapper<Box2Dified> mBox2dified) {
        this.mPhysical = mPhysical;
        this.mBox2dified = mBox2dified;
    }

    @Override
    public void beginContact(Contact contact) {
        Pair<Box2Dified, Physical> items = getPhysicals(contact);

        if (items != null) {
            items.getKey().foot_touches += 1;
            items.getValue().isOnGround = (items.getKey().foot_touches > 0);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Pair<Box2Dified, Physical> items = getPhysicals(contact);

        if (items != null) {
            items.getKey().foot_touches -= 1;
            items.getValue().isOnGround = (items.getKey().foot_touches > 0);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private Pair<Box2Dified, Physical> getPhysicals(Contact contact) {
        Fixture[] fixtures = new Fixture[]{contact.getFixtureA(), contact.getFixtureB()};

        for (Fixture f : fixtures) {
            if (f.isSensor() && f.getUserData() instanceof Integer) {
                int entityID = (Integer) f.getUserData();

                // check if it's a physical thing all the same
                if (mBox2dified.has(entityID) && mPhysical.has(entityID)) {
                    return new Pair<Box2Dified, Physical>(mBox2dified.get(entityID), mPhysical.get(entityID));
                }
            }
        }

        return null;
    }
}
