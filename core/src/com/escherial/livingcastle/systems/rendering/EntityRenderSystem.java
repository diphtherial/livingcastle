package com.escherial.livingcastle.systems.rendering;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.escherial.livingcastle.components.Physical;
import com.escherial.livingcastle.components.Position;
import com.escherial.livingcastle.components.Sprited;

public class EntityRenderSystem extends IteratingSystem {
    ComponentMapper<Sprited> mSprited;
    ComponentMapper<Position> mPosition;
    ComponentMapper<Physical> mPhysical;
    SpriteBatch batch;
    OrthographicCamera camera;

    public EntityRenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Aspect.all(Sprited.class, Position.class));
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    protected void process(int e) {
        Sprited re = mSprited.get(e);
        Position p = mPosition.get(e);

        if (re.facing == Sprited.Facing.LEFT)
            batch.draw(re.img, p.pos.x - re.offx, p.pos.y - re.offy, p.width, p.height, 0, 0, re.img.getWidth(), re.img.getHeight(), true, false);
        else if (re.facing == Sprited.Facing.VELOCITY && mPhysical.has(e) && mPhysical.get(e).body != null) {
            Physical phys = mPhysical.get(e);
            // this is absurd, but whatevs...
            batch.draw(re.img, p.pos.x - re.offx, p.pos.y - re.offy, 0, 0, p.width, p.height, 1f, 1f, phys.body.getLinearVelocity().angle(), 0, 0, re.img.getWidth(), re.img.getHeight(), false, false);
        }
        else
            batch.draw(re.img, p.pos.x - re.offx, p.pos.y - re.offy, p.width, p.height);
    }
}
