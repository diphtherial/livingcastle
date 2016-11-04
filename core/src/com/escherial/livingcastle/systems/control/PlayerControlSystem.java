package com.escherial.livingcastle.systems.control;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.escherial.livingcastle.components.*;
import com.escherial.livingcastle.structure.input.InputEventTranslator;

import static com.escherial.livingcastle.components.PlayerControlled.State.*;

/**
 * Created by Faisal on 10/12/2016.
 */
public class PlayerControlSystem extends IteratingSystem {
    private final Sound landing_clunk;
    private final Sound firing_sound;

    protected ComponentMapper<Physical> mPhysical;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Sprited> mSprited;
    protected ComponentMapper<PlayerControlled> mPlayerControlled;
    public static float PLAYER_MOVE_THRUST = 150.0f;
    public static float PLAYER_JUMP_THRUST_INITIAL = 4000.0f;
    public static float PLAYER_JUMP_THRUST_CONTINUED = 2000.0f;

    // deals with mapping real input (keystrokes, controller presses) into game input (jumps, firing, etc.)
    InputEventTranslator input;

    public PlayerControlSystem(InputEventTranslator input) {
        super(Aspect.all(PlayerControlled.class, Physical.class, Sprited.class));
        this.input = input;

        landing_clunk = Gdx.audio.newSound(Gdx.files.internal("sounds/retrosounds/General Sounds/Impacts/sfx_sounds_impact1.wav"));
        firing_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/retrosounds/Weapons/Lasers/sfx_wpn_laser1.wav"));
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void process(int e) {
        Physical physical = mPhysical.get(e);
        Position p = mPosition.get(e);
        PlayerControlled con = mPlayerControlled.get(e);
        Sprited sprited = mSprited.get(e);

        switch (con.state) {
            case STANDING:
                if (input.isPressed(InputEventTranslator.Action.JUMP)) {
                    // apply an impulse
                    physical.force.y += PLAYER_JUMP_THRUST_INITIAL;
                    con.state = JUMPING;
                } else if (physical.isOnGround && input.isPressed(InputEventTranslator.Action.DUCK)) {
                    // i guess we can duck?
                    con.state = DUCKING;
                }
                break;

            case JUMPING:
                if (!physical.isOnGround) {
                    // if our foot's no longer making contact, we're airborne
                    con.state = AIRBORNE;
                }
                else {
                    // continue to apply thrust
                    if (input.isPressed(InputEventTranslator.Action.JUMP)) {
                        physical.force.y += PLAYER_JUMP_THRUST_CONTINUED;
                    }
                }
                break;

            case AIRBORNE:
                // check if we're on the ground so we can resume standing
                if (physical.isOnGround) {
                    con.state = LANDED; // thump! we hit the ground
                }
                break;

            case LANDED:
                // assumedly we're here b/c we were previously airborne
                {
                    // clunk and move on with our lives
                    // FIXME: we only land if we were airborne, and we're only airborne if we jumped
                    // FIXME: the above means moving off a cliff and hitting the ground makes no noise
                    landing_clunk.play(Math.abs(physical.body.getLinearVelocity().y)/10.0f);
                    con.state = STANDING;
                }
                break;

            case DUCKING:
                if (!input.isPressed(InputEventTranslator.Action.DUCK)) {
                    con.state = STANDING;
                }
                else {
                    // we're still ducking, but if we jump it's a "rocket jump"
                    if (input.isPressed(InputEventTranslator.Action.JUMP)) {
                        // apply an impulse
                        physical.force.y += PLAYER_JUMP_THRUST_INITIAL * 8f;
                        con.state = JUMPING;
                    }
                }
                break;
        }

        // we can always move left and right
        /*
        if (input.isPressed(InputEventTranslator.Action.MOVE_LEFT)) {
            physical.force.x -= PLAYER_MOVE_THRUST;
        } else if (input.isPressed(InputEventTranslator.Action.MOVE_RIGHT)) {
            physical.force.x += PLAYER_MOVE_THRUST;
        }
        */

        if (input.isPressed(InputEventTranslator.Action.MOVE_LEFT)) {
            sprited.facing = Sprited.Facing.LEFT;
            if (Math.abs(physical.body.getLinearVelocity().x) < 10f)
                physical.body.applyLinearImpulse(-5, 0, 1, 1, true);
        }

        if (input.isPressed(InputEventTranslator.Action.MOVE_RIGHT)) {
            sprited.facing = Sprited.Facing.RIGHT;
            if (Math.abs(physical.body.getLinearVelocity().x) < 10f)
                physical.body.applyLinearImpulse(5, 0, 1, 1, true);
        }

        // we can always fire, too
        if (input.isPressed(InputEventTranslator.Action.FIRE) && con.fire_cooldown <= 0f) {
            // spawn a doohickey
            int bulletID = world.create();

            // configure the physics of this bullet
            Physical bullet_physical = new Physical();
            bullet_physical.vel.set(sprited.facing == Sprited.Facing.LEFT?-100:100, 0);
            bullet_physical.isBullet = true;
            bullet_physical.collideCategory = Physical.CollisionCategory.PLAYER;

            world.edit(bulletID)
                .add(new Position(p.pos.x, p.pos.y, 1f, 0.5f))
                .add(new Sprited("sprites/missile.png", 0.5f, 0.5f, Sprited.Facing.VELOCITY))
                .add(new Bullet(e))
                .add(bullet_physical);

            // play a cool noise, too!
            firing_sound.play();
            con.fire_cooldown = con.firing_rate;
        }

        if (con.fire_cooldown > 0f) {
            con.fire_cooldown -= world.getDelta();

            if (Math.abs(con.fire_cooldown) < 0.0001f)
                con.fire_cooldown = 0f;
        }
    }
}
